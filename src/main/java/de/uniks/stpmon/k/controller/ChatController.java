package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.Region;
import de.uniks.stpmon.k.rest.GroupApiService;
import de.uniks.stpmon.k.service.MessageService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.views.MessageCell;
import de.uniks.stpmon.k.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Optional;

public class ChatController extends Controller {
    @FXML
    public Button backButton;
    @FXML
    public VBox messageArea;
    @FXML
    public TextField messageField;
    @FXML
    public Button sendButton;
    @FXML
    public Button settingsButton;
    @FXML
    public ChoiceBox<String> regionPicker;
    @FXML
    public Text groupName;
    @FXML
    public VBox chatScreen;


    @Inject
    MessageService msgService;
    @Inject
    RegionService regionService;
    @Inject
    GroupApiService groupApiService;
    @Inject
    UserService userService;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    EventListener eventListener;

    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private ListView<Message> messagesListView;
    private Group group;
    private final HashMap<String, String> groupMembers = new HashMap<>();
    private Message editMessage;

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Inject
    public ChatController() {
    }

    @Override
    public void init() { // get all messages in one chat
        messages.clear();
        // populate a group users hashmap with just one REST call to not run into rate limit
        disposables.add(userService
                .getUsers(group.members())
                .observeOn(FX_SCHEDULER)
                .subscribe(users -> users.forEach(user -> groupMembers.put(user._id(), user.name())), this::handleError
                )
        );
        System.out.println("group name is: " + group.name());
        disposables.add(msgService
                .getAllMessages("groups", group._id()).observeOn(FX_SCHEDULER).subscribe(this.messages::setAll, this::handleError));

        // with dispose the subscribed event is going to be unsubscribed
        disposables.add(eventListener
                .listen("groups.%s.messages.*.*".formatted(group._id()), Message.class).observeOn(FX_SCHEDULER).subscribe(event -> {
                            // only listen to messages in the current specific group
                            // ( event format is: group.group_id.messages.message_id.{created,updated,deleted} )
                            final Message msg = event.data();
                            System.out.println(msg);
                            switch (event.suffix()) {
                                case "created" -> this.messages.add(msg);
                                // checks and updates all messages that have been edited by a user
                                case "updated" -> this.messages.replaceAll(m -> m._id().equals(msg._id()) ? msg : m);
                                case "deleted" -> this.messages.removeIf(m -> m._id().equals(msg._id()));
                            }
                            if (event.suffix().equals("created")) {
                                messagesListView.scrollTo(msg);
                            }
                        }, this::handleError
                )
        );
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        backButton.setOnAction(e -> leaveChat());

        settingsButton.setOnAction(e -> openSettings());

        groupName.setText(group.name());
        addRegionsToChoiceBox();

        // the factory creates the initial message list in the chat ui
        messagesListView = new ListView<>(this.messages);
        messagesListView.setCellFactory(param -> new MessageCell(userService.getMe(), groupMembers));
        messagesListView.prefHeightProperty().bind(messageArea.heightProperty());
        messagesListView.prefWidthProperty().bind(messageArea.widthProperty());
        // scrolls to the bottom of the listview
        messagesListView.scrollTo(1);

        messagesListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // handle selected message
                        messageField.setText(newValue.body());
                        editMessage = newValue;
                    } else if (oldValue != null) {
                        messageField.setText("");
                        editMessage = null;
                    }
                });


        // disable button if field empty
        sendButton.disableProperty().bind(messageField.textProperty().isEmpty());
        sendButton.disableProperty().bind(messageField.textProperty().length().greaterThan(16000));
        sendButton.setOnAction(click -> sendMessage());
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        messageArea.getChildren().setAll(messagesListView);

        return parent;
    }

    // TODO: this is just for testing remove afterwards or use it if you want
    public void addRegionsToChoiceBox() {
        disposables.add(regionService
                .getRegions()
                .subscribe(regions -> {
                    // add all region names to the choice box
                    regions.forEach(region -> regionPicker.getItems().add(region.name()));
                })
        );
    }

    @FXML
    public void sendMessage() {
        //remove spaces at end of string
        String message = messageField.getText().trim();

        //check if a message is edited
        if (editMessage != null) {
            //check if message should be deleted
            if (message.isEmpty()) {
                disposables.add(msgService
                        .deleteMessage(editMessage, "groups", group._id())
                        .observeOn(FX_SCHEDULER)
                        .subscribe(msg -> {
                                    System.out.println("Message sent: " + msg.body());
                                    messageField.clear();

                                    messagesListView.getSelectionModel().clearSelection();
                                }, this::handleError
                        )
                );
            } else {
                //updateMessage
                disposables.add(msgService
                        .editMessage(editMessage, "groups", group._id(), message)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(msg -> {
                                    System.out.println("Message sent: " + msg.body());
                                    messageField.clear();
                                    messagesListView.getSelectionModel().clearSelection();
                                }, this::handleError
                        )
                );
            }

            editMessage = null;

        } else {
            //check for region selection
            if (!regionPicker.getSelectionModel().isEmpty()) {
                String regionName = regionPicker.getSelectionModel().getSelectedItem();
                Optional<Region> regionOptional = regionService.getRegions().blockingFirst()
                        .stream().filter(r -> r.name().equals(regionName)).findFirst();

                if(regionOptional.isPresent()) {
                    String regionId = regionOptional.get()._id();

                    String invitationText = "Join " + regionId;

                    disposables.add(msgService
                            .sendMessage(invitationText, "groups", group._id())
                            .observeOn(FX_SCHEDULER)
                            .subscribe(msg -> {
                                System.out.println("Message sent: " + msg.body());
                                messagesListView.scrollTo(msg);
                            }, this::handleError)
                    );
                    regionPicker.getSelectionModel().clearSelection();
                }
            }

            if (message.isEmpty()) {
                return;
            }
            disposables.add(msgService
                    .sendMessage(message, "groups", group._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(msg -> {
                                System.out.println("Message sent: " + msg.body());
                                messageField.clear();
                                messagesListView.scrollTo(msg);
                            }, this::handleError
                    )
            );
        }
    }

    @FXML
    public void openSettings() {
        app.show(hybridControllerProvider.get());
        hybridControllerProvider.get().openSidebar("createChat");
    }

    public void leaveChat() {
        messages.clear();
        app.show(hybridControllerProvider.get());
        hybridControllerProvider.get().openSidebar("chatList");
    }

    // reusable handle error function for the onError of an Observable
    private void handleError(Throwable error) {
        System.out.println("Look here for the error: " + error);
        error.printStackTrace();
    }
}
