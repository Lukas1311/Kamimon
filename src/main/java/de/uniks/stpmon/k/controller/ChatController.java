package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.rest.GroupApiService;
import de.uniks.stpmon.k.rest.MessageApiService;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.MessageService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.UserStorage;
import de.uniks.stpmon.k.views.MessageCell;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;

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

    private StringProperty regionName;
    private StringProperty functionStatus;
    private ObservableList<Message> messages = FXCollections.observableArrayList();

    @Inject
    public ChatController() {
    }

    @Override
    public void init() { // get all messages in one chat
        // TODO: get the chat = group_id from somewhere
        System.out.println("this should be a group id: " + groupApiService.getGroups().firstElement().blockingGet().get(0)._id());
        disposables.add(msgService
            .getAllMessages(
                // TODO: remove only for testing a group id
                "groups",
                "6457a3ce4d233ed4626d20c0"
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

        addRegionsToChoiceBox();

        final ListView<Message> messages = new ListView<>(this.messages);
        messages.setCellFactory(param -> new MessageCell());

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
            .sendMessage(messageField.getText(), "groups", "6457a3ce4d233ed4626d20c0")
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

    @FXML
    public void leaveChat() {
        //TODO: create method leaveChat
    }
}
