package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.UserService;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import retrofit2.HttpException;

import java.util.ArrayList;
import java.util.Arrays;

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
    private BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);
    private BooleanBinding passwordTooShort;
    private BooleanBinding usernameTooLong;
    private BooleanBinding usernameInvalid;
    private BooleanBinding passwordInvalid;
    private Boolean changesSaved = false;
    private BooleanBinding changesMade;
    private User currentUser;


    @Inject
    public UserManagementController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        currentUser = userService.getMe();
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

        // set bindings to buttons that should be disabled after the popup is shown
        saveChangesButton.disableProperty().bind(changesMade.not().or(isPopUpShown));
        deleteUserButton.disableProperty().bind(isPopUpShown);

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

    public void backToSettings() {
        if (hasUnsavedChanges()) {
            showPopUp(PopUpScenario.UNSAVED_CHANGES, result -> {
                if (!result) return;
                saveCredentials();
            });
        }
        hybridControllerProvider.get().popTab();
    }

    public Boolean hasUnsavedChanges() {
        return changesMade.get() && !changesSaved;
    }

    public void saveChanges() {
        showPopUp(PopUpScenario.SAVE_CHANGES, result -> {
            if (!result) return;
            saveCredentials();
            changesSaved = true;
        });
    }

    public void saveCredentials() {
        if (!usernameInvalid.get()) {
            saveUsername(username.get());
        }
        if (!passwordInvalid.get()) {
            savePassword(password.get());
        }
    }

    private void saveUsername(String newUsername) {
        disposables.add(
            userService.setUsername(newUsername).observeOn(FX_SCHEDULER).subscribe(usr -> {
                // set this to retrieve the newly set username
                currentUser = usr;
            }, err -> {
                usernameError.set("error");
                if (!(err instanceof HttpException ex)) return;
                if (!(ex.code() == 409)) return;
                usernameError.set(translateString("username.already.in.use"));
            })
        );
    }

    private void savePassword(String newPassword) {
        disposables.add(
            userService.setPassword(newPassword).observeOn(FX_SCHEDULER).subscribe(usr -> {
            }, err -> {
                passwordError.set(translateString("error"));
            })
        );
    }

    public void deleteUser() {
        PopUpScenario deleteScenario = PopUpScenario.DELETE_USER;
        // TODO: maybe set username initially and no "enter name" placeholder? ask SM / PO
        deleteScenario.setParams(new ArrayList<>(Arrays.asList(currentUser.name())));
        showPopUp(PopUpScenario.DELETE_USER, result -> {
            if (!result) return;
            disposables.add(userService
                .deleteMe()
                .observeOn(FX_SCHEDULER)
                .subscribe(usr -> {
                    PopUpScenario deleteConfirmScenario = PopUpScenario.DELETION_CONFIRMATION;
                    deleteConfirmScenario.setParams(new ArrayList<>(Arrays.asList(usr.name())));
                    showPopUp(deleteConfirmScenario, innerResult -> {
                        if (!innerResult) return;
                        app.show(loginControllerProvider.get());
                    });
                }, err -> app.show(loginControllerProvider.get()) // in case of e.g. 404 error
                )
            );
        });
    }

    public void showPopUp(PopUpScenario scenario, ModalCallback callback) {
        isPopUpShown.set(true);
        PopUpController popUp = popUpControllerProvider.get();
        popUp.setScenario(scenario);
        popUp.showModal(callback);
        isPopUpShown.set(false);
    }
}
