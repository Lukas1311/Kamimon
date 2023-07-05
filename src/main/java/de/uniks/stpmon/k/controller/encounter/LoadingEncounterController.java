package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.util.ArrayList;

public class LoadingEncounterController extends Controller {

    @FXML
    public Circle blackPoint;

    @Inject
    public LoadingEncounterController() {

    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        return parent;
    }
}
