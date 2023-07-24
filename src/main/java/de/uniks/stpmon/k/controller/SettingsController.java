package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.service.world.TextureSetService;
import de.uniks.stpmon.k.utils.Direction;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;

public class SettingsController extends ToastedController {

    @FXML
    public VBox settingsScreen;
    @FXML
    public Button backButton;
    @FXML
    public Button mdmzSettings;
    @FXML
    public Button editUserButton;
    @FXML
    public Button editTrainerButton;
    @FXML
    public StackPane spriteContainer;
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
    TrainerStorage trainerStorage;
    @Inject
    RegionStorage regionStorage;

    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    TextureSetService textureService;

    private final SimpleStringProperty usernameProperty = new SimpleStringProperty();
    private final SimpleStringProperty regionProperty = new SimpleStringProperty();
    private final SimpleStringProperty trainerProperty = new SimpleStringProperty();

    @Inject
    public SettingsController() {
    }

    public Parent render() {
        final Parent parent = super.render();
        settingsScreen.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));

        User user = userStorage.getUser();
        SimpleBooleanProperty trainerLoaded = trainerStorage.getTrainerLoaded();

        subscribe(trainerStorage.onTrainer(), trainer -> {
            if (trainer.isPresent()) {
                trainerProperty.set(trainer.get().name());
                userTrainerValue.textProperty().bind(trainerProperty);
                regionProperty.set(regionStorage.getRegion().name());
                userRegionValue.textProperty().bind(regionProperty);

                subscribe(
                        setSpriteImage(spriteContainer, userSprite, Direction.BOTTOM, trainer.get().image(), textureService),
                        this::handleError
                );
            }
        });


        editTrainerButton.disableProperty().bind(trainerLoaded.not());
        userTrainerValue.visibleProperty().bind(trainerLoaded);
        userTrainer.visibleProperty().bind(trainerLoaded);
        userRegion.visibleProperty().bind(trainerLoaded);
        userRegionValue.visibleProperty().bind(trainerLoaded);
        spriteContainer.visibleProperty().bind(trainerLoaded);

        usernameProperty.set(user.name());
        usernameValue.textProperty().bind(usernameProperty);

        backButton.setOnAction(click -> backToMainScreen());
        mdmzSettings.setOnAction(click -> sound());
        editUserButton.setOnAction(click -> editUser());
        editTrainerButton.setOnAction(click -> editTrainer());

        return parent;
    }

    public void sound() {
        hybridControllerProvider.get().pushTab(SidebarTab.SOUND);
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
