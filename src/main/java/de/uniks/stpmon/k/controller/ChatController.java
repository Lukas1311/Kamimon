package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.service.MessageService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.UserStorage;
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
    UserStorage userStorage;

    private StringProperty regionName;
    private StringProperty functionStatus;
    private ObservableList<Message> messageList = FXCollections.observableArrayList();

    @Inject
    public ChatController() {

    }


    @Override
    public Parent render() {
        final Parent parent = super.render();

        // binds the messageBoxList to the messageArea
        ListProperty<Message> messageProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        // messageProperty.bindBidirectional(messageList);
        // messageArea.getChildren().addAll(messageList);

        //TODO

        messageArea.getChildren().add(new MessageController(new Message(null, null, null, "nobody", "Testnachricht")).render());
        messageArea.getChildren().add(new MessageController(new Message(null, null, null, "jemand", "Testnachricht2")).render());
        

        addRegionsToChoiceBox();

        // clear message field on button click
        sendButton.setOnAction(click -> messageField.clear());
        // disable button if field empty
        sendButton.disableProperty().bind(messageField.textProperty().isEmpty());



        regionName = new SimpleStringProperty("");
        // bind regionName to selected choice box item
        regionName.bind(regionPicker.getSelectionModel().selectedItemProperty());
        
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
        messageArea.getChildren().add(new MessageController(new Message(null, null, null, userStorage.getUser().name(), messageField.getText())).render());
        //TODO: find out what the correct regions strings are
        //TODO: find out a proper message ID as the parent
        disposables.add(msgService
            .sendMessage(messageField.getText(), regionName.get(), "123")
            .observeOn(FX_SCHEDULER)
            .subscribe(msg -> {
                functionStatus.set("Message sent");
                System.out.println(msg);
                messageArea.getChildren().add(
                    new MessageController(msg).render()
                );
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
