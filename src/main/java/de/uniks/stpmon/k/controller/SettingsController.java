package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.storage.UserStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;

public class SettingsController extends Controller {

    @FXML
    public VBox settingsScreen;
    @FXML
    public Button backButton;
    @FXML
    public Button editUserButton;
    @FXML
    public ImageView userSprite;
    @FXML
    public Text usernameValue;
    @FXML
    public Text userRegionValue;
    @FXML
    public Text userTrainerValue;
    @FXML
    public Text username;
    @FXML
    public Text userRegion;
    @FXML
    public Text userTrainer;
    @Inject
    UserStorage userStorage;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<UserManagementController> userManagementControllerProvider;

    @Inject
    public SettingsController() {
    }

    public Parent render() {
        final Parent parent = super.render();
        settingsScreen.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));

        Rectangle rectangle = new Rectangle(0, 0, 200, 150);
        rectangle.setArcWidth(20);
        rectangle.setArcHeight(20);
        userSprite.setClip(rectangle);

        User user = userStorage.getUser();
        usernameValue.setText(user.name());
        // TODO userRegionValue.setText(user.region()); and userTrainerValue.setText(user.trainer());
        
        backButton.setOnAction(click -> backToMainScreen());
        editUserButton.setOnAction(click -> editUser());

        return parent;
    }

    public void backToMainScreen() {
        hybridControllerProvider.get().forceTab(SidebarTab.SETTINGS);
    }

    public void editUser() {
        hybridControllerProvider.get().pushTab(SidebarTab.USER_MANAGEMENT);
    }

    public void editTrainer() {
        hybridControllerProvider.get().pushTab(SidebarTab.TRAINER_MANAGEMENT);
    }
}
