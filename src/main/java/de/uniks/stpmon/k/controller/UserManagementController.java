package de.uniks.stpmon.k.controller;

import javax.inject.Inject;
import javax.inject.Provider;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.SidebarTab;
import de.uniks.stpmon.k.service.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

public class UserManagementController extends Controller {
    @FXML
    public VBox userManagementScreen;
    @FXML
    public TextField usernameInput;
    @FXML
    public TextField passwordInput;
    @FXML
    public Button deleteUserButton;
    @FXML
    public Button saveChangesButton;
    @FXML
    public Button backButton;

    @Inject
    UserService userService;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<LoginController> loginControllerProvider;

    @Inject
    public UserManagementController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        userManagementScreen.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));

        // TODO: all ui functionality here

        backButton.setOnAction(click -> backToSettings());
        deleteUserButton.setOnAction(click -> deleteUser());
        return parent;
    }

    public void deleteUser() {
        // TODO: replace this with real modal pop pop up
        new Alert(Alert.AlertType.CONFIRMATION, "do you really want to delete the user?").showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // the user clicked OK
                // TODO: we definitely need two popups here!!!! Or three!!
                new Alert(Alert.AlertType.CONFIRMATION, "are you sure, you want to do this?").showAndWait().ifPresent(innerButtonType -> {
                    if (innerButtonType == ButtonType.OK) {
                        new Alert(Alert.AlertType.CONFIRMATION, "are you a 100 % sure that your user will be deleted afterwards?").showAndWait().ifPresent(innerInnerButtonType -> {
                            if (innerInnerButtonType == ButtonType.OK) {
                                new Alert(Alert.AlertType.CONFIRMATION, "there is no coming back!").showAndWait().ifPresent(innerInnerInnerButtonType -> {
                                    if (innerInnerInnerButtonType == ButtonType.OK) {
                                        disposables.add(userService
                                            .deleteMe()
                                            .observeOn(FX_SCHEDULER)
                                            .subscribe(usr -> {
                                                // TODO: user deleted pop up
                                                app.show(loginControllerProvider.get());
                                            })
                                        );
                                    }
                                });
                            }
                        });
                    }
                });


            } else if (buttonType == ButtonType.CANCEL) {
                // the user clicked CANCEL
            }
        });
    }

    public void saveChanges() {
        // TODO: replace this with real modal pop pop up
        new Alert(Alert.AlertType.CONFIRMATION, "save changes?").showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                saveChanges();
            } else if (buttonType == ButtonType.CANCEL) {
                // do nothing
            }
        });
    }

    public void backToSettings() {
        // TODO: add pop confirmation only when unsaved settings
        // TODO: replace this with real modal pop pop up
        Platform.runLater(() -> {
            new Alert(Alert.AlertType.CONFIRMATION, "do you want to go back?").showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    hybridControllerProvider.get().popTab();
                } else if (buttonType == ButtonType.CANCEL) {
                    // do nothing
                }
            });
        });

    }
}
