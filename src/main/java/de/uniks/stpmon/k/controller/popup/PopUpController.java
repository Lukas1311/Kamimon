package de.uniks.stpmon.k.controller.popup;

import de.uniks.stpmon.k.controller.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import javax.inject.Inject;

/**
 * The PopUpController shows different text depending on in which scenario it is used
 * (The text in the buttons are always the same except in den DELETEUSER scenario)
 * NOTE: While a PopUp is active, nothing is clickable except elements of the popUp itself.
 * But the buttons still have to be disabled, to make it clear to the user
 * ---------------------------------------------------------------------------------
 * How to add a new customized controller:
 * 1. Add you scenario in the PopUpScenario.java enum (the string is the main text of the popup)
 * 2. Add the following method to the controller from which the popUp is called:
 * public void showPopUp(ModalCallback callback) {
 *  PopUpController popUp = popUpControllerProvider.get();
 *  popUp.setScenario(PopUpScenario.<your_popUp_scenario_here>);
 *  <disable buttons and of your controller here>
 *  popUp.showModal(callback);
 * }
 * 4. Use the callback to implement the functionality in your controller
 * e.g. something like this:
 * public void saveChanges() {
 *  showPopUp(result -> {
 *  <enable buttons and again>
 *  if (!result) return; //changes are not save
 *      //implement here what should happen, when changes are saved
 *  });
 * }
 */
public class PopUpController extends Controller {
    @FXML
    public Button popUpCancelButton;
    @FXML
    public Text popUpMainText;
    @FXML
    public Button approveButton;
    @FXML
    public Button discardButton;
    
    private Stage modalStage;
    private ModalCallback callback;
    private PopUpScenario scenario;

    @Inject
    public PopUpController() {
    }

    @Override
    public Parent render(){
        final Parent parent = super.render();

    }

    @Override
        
        return parent;
    }

    public void setScenario(PopUpScenario popUpScenario) {
        this.scenario = popUpScenario;
    }

    public void showModal(ModalCallback callback) {
        this.callback = callback;

        // create new stage for modal dialog
        modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initStyle(StageStyle.UNDECORATED);

        // set scene for the modal dialog
        Scene scene = new Scene(render());
        modalStage.setScene(scene);

        // main text has to be set here after the render() call otherwise it will fail because the fxml is not available yet 
        popUpMainText.setText(translateString(scenario.toString()));

        // set owner of modal to parent window to retrieve e.g. parent windows sizes
        Window parentWindow = app.getStage().getScene().getWindow();
        // init owner before stage gets visible
        modalStage.initOwner(parentWindow);
        modalStage.show();
        modalStage.hide();
        // calculate the center position relative to the parent window
        double centerX = parentWindow.getX() + (parentWindow.getWidth() - modalStage.getWidth()) / 2;
        double centerY = parentWindow.getY() + (parentWindow.getHeight() - modalStage.getHeight()) / 2;

        // set the position of the modal dialog
        modalStage.setX(centerX);
        modalStage.setY(centerY);

        // bind buttons to actions
        approveButton.setOnAction(click -> approve());
        discardButton.setOnAction(click -> cancel());
        popUpCancelButton.setOnAction(click -> cancel());

        // show modal dialog and wait for interactions of the user
        modalStage.showAndWait();
    }

    public void approve() {
        boolean result = true;
        if (callback != null) {
            callback.onModalResult(result);
        }
        //enable buttons
        modalStage.close();
    }

    public void cancel() {
        boolean result = false;
        if (callback != null) {
            callback.onModalResult(result);
        }
        //enable buttons
        modalStage.close();
    }
}
