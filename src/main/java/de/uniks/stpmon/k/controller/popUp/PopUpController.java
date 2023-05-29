package de.uniks.stpmon.k.controller.popUp;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import javax.inject.Inject;

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
    public PopUpController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        return parent;
    }


    public void approve() {

    }

    public void cancel() {

    }
}
