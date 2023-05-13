package de.uniks.stpmon.k.controller;

import javax.inject.Inject;
import javax.inject.Provider;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.rest.GroupApiService;
import de.uniks.stpmon.k.service.MessageService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.views.MessageCell;
import de.uniks.stpmon.k.ws.EventListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
    public ChoiceBox<String> regionPicker;

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


    private StringProperty regionName;
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private ListView<Message> messagesListView;
    private Group group;

    public Group getGroup() { return this.group; }
    public void setGroup(Group group) { this.group = group; }

    @Inject
    public ChatController() {
    }

    @Override
    public void init() { // get all messages in one chat
        System.out.println("group name is: " + group.name());
        disposables.add(msgService
            .getAllMessages("groups", group._id()).observeOn(FX_SCHEDULER).subscribe(this.messages::setAll, this::handleError));
        // with dispose the subscribed event is going to be unsubscribed
        disposables.add(eventListener
            // only listen to messages in the current specific group
            .listen("groups.%s.messages.*".formatted(group._id()), Message.class).observeOn(FX_SCHEDULER).subscribe(event -> {
                final Message msg = event.data();
                switch (event.suffix()) {
                    case "created" -> messages.add(msg);
                    // checks and updates all messages that have been edited by a user
                    case "updated" -> messages.replaceAll(m -> m._id().equals(msg._id()) ? msg : m);
                    case "deleted" -> messages.removeIf(m -> m._id().equals(msg._id()));
                }
            }, this::handleError
            ));
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        backButton.setOnAction(e -> leaveChat());

        addRegionsToChoiceBox();

        // the factory creates the initial message list in the chat ui
        messagesListView = new ListView<>(this.messages);
        messagesListView.setCellFactory(param -> new MessageCell(userService));
        messagesListView.prefHeightProperty().bind(messageArea.heightProperty());
        messagesListView.prefWidthProperty().bind(messageArea.widthProperty());



        // TODO: edit and delete single messages by chosing them and some listener stuff @basbaer
        // messages.getSelectionModel().selectedItemProperty()... listener stuff



        // disable button if field empty
        sendButton.disableProperty().bind(messageField.textProperty().isEmpty());
        sendButton.setOnAction(click -> {
            // clear message field and send on button click
            sendMessage();
            messageField.clear();
        });
        sendButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
                messageField.clear();
            }
        });

        regionName = new SimpleStringProperty("");
        // bind regionName to selected choice box item
        regionName.bind(regionPicker.getSelectionModel().selectedItemProperty());
        messageArea.getChildren().setAll(messagesListView);

        return parent;
    }

    // TODO: this is just for testing remove afterwards or use it if you want
    public void addRegionsToChoiceBox() {
        regionService
            .getRegions()
            .subscribe(regions -> {
                // add all region names to the choice box
                regions.forEach(region -> regionPicker.getItems().add(region.name()));
            });
    }

    @FXML
    public void sendMessage() {
        // TODO: remove afterwards (non-functional: visual testing only)
        // messageArea.getChildren().add(new MessageController(new Message(null, null, null, userStorage.getUser().name(), messageField.getText())).render());
        disposables.add(msgService
            .sendMessage(messageField.getText(), "groups", group._id())
            .observeOn(FX_SCHEDULER)
            .subscribe(msg -> {
                System.out.println("Message sent: " + msg.body());
                messages.add(msg);
                messagesListView.scrollTo(msg);
                // messageArea.getChildren().add(new MessageCell());
            },this::handleError
            )
        );

    }

    @FXML
    public void openSettings() {
        //TODO: create method openSettings
    }

    public void leaveChat() {
        app.show(hybridControllerProvider.get());
        hybridControllerProvider.get().openSidebar("chatList");
    }

    // reusable handle error function for the onError of an Observable
    private void handleError(Throwable error) {
        System.out.println("Look here for the error: " + error);
        error.printStackTrace();
    }
}
