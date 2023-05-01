package de.uniks.stpmon.k.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class MessageController extends Controller {

    @FXML
    public HBox textBox;
    @FXML
    public Text bodyText;
    @FXML
    public Text senderName;
    @FXML
    public Text sendTime;

    public MessageController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        bodyText.setText("Testnachricht");

        //TODO: implement method when Message is ready
//        senderName.setText(message.sender());
//        bodyText.setText(message.body());

        return parent;
    }
}
