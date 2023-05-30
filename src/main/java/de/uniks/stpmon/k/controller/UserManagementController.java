package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.popUp.PopUpController;
import de.uniks.stpmon.k.controller.popUp.PopUpScenario;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.UserService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;

public class UserManagementController extends Controller {
    @FXML
    public VBox userManagementScreen;
    @FXML
    public TextField usernameInput;
    @FXML
    public TextField passwordInput;
    @FXML
    public Label usernameInfo;
    @FXML
    public Label passwordInfo;
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
    Provider<PopUpController> popUpControllerProvider;

    private final SimpleStringProperty username = new SimpleStringProperty();
    private final SimpleStringProperty password = new SimpleStringProperty();
    private final SimpleStringProperty usernameError = new SimpleStringProperty();
    private final SimpleStringProperty passwordError = new SimpleStringProperty();
    private BooleanBinding passwordTooShort;
    private BooleanBinding usernameTooLong;
    private BooleanBinding usernameInvalid;
    private BooleanBinding passwordInvalid;
    private Boolean changesSaved = false;
    private BooleanBinding changesMade;



    @Inject
    public UserManagementController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        userManagementScreen.prefHeightProperty().bind(app.getStage().heightProperty().subtract(35));

        // bindings:
        usernameTooLong = username.length().greaterThan(32);
        passwordTooShort = password.length().lessThan(8);
        usernameInvalid = username.isEmpty().or(usernameTooLong);
        passwordInvalid = passwordTooShort;
        changesMade = usernameInvalid.not().or(passwordInvalid.not());


        // bind bindings to fxml:
        usernameInput.textProperty().bindBidirectional(username);
        passwordInput.textProperty().bindBidirectional(password);
        usernameInfo.textProperty().bind(usernameError);

        saveChangesButton.disableProperty().bind(changesMade.not());

        usernameInfo.textProperty().bind(
            Bindings.when(usernameTooLong)
            .then(translateString("username.too.long."))
            .otherwise("")
        );
        passwordInfo.textProperty().bind(
            Bindings.when(passwordTooShort.and(password.length().greaterThan(0)))
            .then(translateString("password.too.short."))
            .otherwise("")
        );


        // ui functions:
        backButton.setOnAction(click -> backToSettings());
        saveChangesButton.setOnAction(click -> saveChanges());
        deleteUserButton.setOnAction(click -> deleteUser());
        return parent;
    }

    public void showPopUp(PopUpController.ModalCallback callback) {
        PopUpController popUp = popUpControllerProvider.get();
        popUp.setScenario(PopUpScenario.CHANGELOGINDATA);
        popUp.showModal(callback);
    }

    public void backToSettings() {
        if (changesMade.get() && !changesSaved) {
            // TODO: add pop confirmation
            // if OK: {
            //     saveChanges();
            // } else CANCEL or CLOSE {
            // return;
            // }
            new Alert(Alert.AlertType.CONFIRMATION, "there are unsaved changes.\nsave them?").showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    saveChanges();    
                }
            });
        }
        hybridControllerProvider.get().popTab();
    }

    public void saveChanges() {
        showPopUp(result -> {
            if (!result) return;
            if (!usernameInvalid.get()) {
                saveUsername(username.get());
            }
            if (!passwordInvalid.get()) {
                savePassword(password.get());
            }
            changesSaved = true;
        });
    }

    private void saveUsername(String newUsername) {
        disposables.add(
            userService.setUsername(newUsername).observeOn(FX_SCHEDULER).subscribe(usr -> {
                // TODO: remove in clean up
                System.out.println(usr);
            }, err -> {
                usernameError.set("error");
                if (!(err instanceof HttpException ex)) return;
                if (!(ex.code() == 409)) return;
                usernameError.set("username.already.in.use");
            })

        );
    }

    private void savePassword(String newPassword) {
        disposables.add(
            userService.setPassword(newPassword).observeOn(FX_SCHEDULER).subscribe(usr -> {
                // TODO: remove in clean up
                System.out.println(usr);
            }, err -> {
                passwordError.set("error");
            })

        );
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
}
