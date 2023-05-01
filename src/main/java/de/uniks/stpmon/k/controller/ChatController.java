package de.uniks.stpmon.k.controller;


import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
    public ChoiceBox regionPicker;


    public ChatController() {

    }


    @Override
    public Parent render() {
        final Parent parent = super.render();

        //TODO

        messageArea.getChildren().add(new MessageController().render());
        messageArea.getChildren().add(new MessageController().render());
        messageArea.getChildren().add(new MessageController().render());

        return parent;
    }

    @FXML
    public void sendMessage() {
        //TODO: create method sendMessage
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
