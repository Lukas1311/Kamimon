package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ResourceBundle;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;

public class InvitationController extends Controller {
    @FXML
    public VBox messageBox;
    @FXML
    public Text invited_text;
    @FXML
    public Button joinButton;
    @FXML
    public Text senderName;
    @FXML
    public Text sendTime;

    Provider<HybridController> hybridController;
    Provider<ResourceBundle> resourceBundleProvider;


    private final Message message;
    private final String username;

    @Inject
    public InvitationController(Message msg, String senderUsername, User me, Provider<HybridController> hybridController, Provider<ResourceBundle> resourceBundleProvider) {
        this.message = msg;
        this.username = senderUsername;
        this.hybridController = hybridController;
        this.resourceBundleProvider = resourceBundleProvider;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        invited_text.setText(resourceBundleProvider.get().getString("you.are.invited"));
        joinButton.setText(resourceBundleProvider.get().getString("joinButton"));
        senderName.setText(username);
        sendTime.setText(MessageController.convertDateTimeToTime(message.createdAt()));


        return parent;
    }

    public void join_region() {
        hybridController.get().openMain(INGAME);
    }
}
