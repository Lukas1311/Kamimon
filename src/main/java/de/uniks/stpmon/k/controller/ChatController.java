package de.uniks.stpmon.k.controller;

import javax.inject.Inject;
import javax.inject.Provider;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.rest.GroupApiService;
import de.uniks.stpmon.k.service.MessageService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.UserStorage;
import de.uniks.stpmon.k.views.MessageCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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
    UserStorage userStorage;
    @Inject
    Provider<HybridController> hybridControllerProvider;


    private StringProperty regionName;
    private StringProperty functionStatus;
    private ObservableList<Message> messages = FXCollections.observableArrayList();
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
            .getAllMessages(
                "groups",
                group._id()
                // groupService.getOwnGroups().firstElement().blockingGet().get(0)._id()
            ).observeOn(FX_SCHEDULER)
            .subscribe(messages -> {
                this.messages.setAll(messages);
            }, error -> {
                System.out.println("look here for the error: " + error);
            })
        );
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        backButton.setOnAction(e -> leaveChat());

        addRegionsToChoiceBox();

        // the factory creates the initial message list in the chat ui
        final ListView<Message> messages = new ListView<>(this.messages);
        messages.setCellFactory(param -> new MessageCell());
        messages.prefHeightProperty().bind(messageArea.heightProperty());
        messages.prefWidthProperty().bind(messageArea.widthProperty());

        // TODO: edit and delete single messages by chosing them and some listener stuff
        // messages.getSelectionModel().selectedItemProperty()... listener stuff

        // disable button if field empty
        sendButton.disableProperty().bind(messageField.textProperty().isEmpty());
        sendButton.setOnAction(click -> {
            // clear message field and send on button click
            messageField.clear();
            sendMessage();
        });

        regionName = new SimpleStringProperty("");
        // bind regionName to selected choice box item
        regionName.bind(regionPicker.getSelectionModel().selectedItemProperty());
        messageArea.getChildren().setAll(messages);

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
        System.out.println("msg: " + messageField.getText() + ",region: " +  regionName.get());
        // TODO: remove afterwards (non-functional: visual testing only)
        // messageArea.getChildren().add(new MessageController(new Message(null, null, null, userStorage.getUser().name(), messageField.getText())).render());
        disposables.add(msgService
            .sendMessage(messageField.getText(), "groups", group._id())
            .observeOn(FX_SCHEDULER)
            .subscribe(msg -> {
                functionStatus.set("Message sent");
                System.out.println(msg);
                messageArea.getChildren().add(new MessageCell());
            }, error -> {
                // TODO: this still receives a HTTP 400 and I don't know why
                System.out.println("look here for the error: " + error);
            })
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
}
