package de.uniks.stpmon.k.controller;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class CreateChatController extends Controller {


    public Button returnButton;
    public Button leaveGroupButton;
    public VBox groupMemberList;
    public TextField GroupNameField;
    public Button safeChangesButton;

    @Inject
    public CreateChatController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        returnButton.setOnAction(e -> returnToChatList());


        return parent;
    }

    public void returnToChatList() {
    }

    public void leaveGroup() {
    }

    public void safeChanges() {

    }
}
