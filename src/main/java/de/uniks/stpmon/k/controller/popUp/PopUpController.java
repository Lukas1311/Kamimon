package de.uniks.stpmon.k.controller.popUp;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.UserManagementController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;

public class PopUpController extends Controller {
    @FXML
    public Button popUpCancelButton;
    @FXML
    public Text popUpMainText;
    @FXML
    public Button approveButton;
    @FXML
    public Button discardButton;
    @Inject
    UserService userService;
    @Inject
    Provider<UserManagementController> userManagementControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvier;
    private PopUpScenario scenario;

    @Inject
    public PopUpController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        switch (scenario) {
            case CHANGELOGINDATA -> {
                popUpMainText.setText(translateString("doYouWantToSaveChanges"));
                approveButton.setText(translateString("yes"));
                discardButton.setText(translateString("discard"));
            }
            case CHANGESPRITE -> {
                //do things when sprite is changed
            }
        }
        return parent;
    }

    public void setScenario(PopUpScenario popUpScenario) {
        scenario = popUpScenario;
    }

    public void approve() {
        switch (scenario) {
            case CHANGELOGINDATA -> saveLoginData();
            case CHANGESPRITE -> {
            }
        }
    }

    private void saveLoginData() {
        //get input from controller that handles the settings
        String username = userManagementControllerProvider.get().usernameInput.getText();
        if (!username.isEmpty()) {
            //send input to server
            disposables.add(userService
                    .setUsername(username)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(event -> {
                    }));
        }
        String password = userManagementControllerProvider.get().passwordInput.getText();
        if (!password.isEmpty()) {
            disposables.add(userService
                    .setPassword(password)
                    .observeOn(FX_SCHEDULER)
                    .subscribe());
        }

    }

    public void cancel() {
        //load settings from before
        switch (scenario) {
            case CHANGELOGINDATA -> hybridControllerProvier.get().popTab();
            case CHANGESPRITE -> {
            }
        }

    }
}
