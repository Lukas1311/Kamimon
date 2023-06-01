package de.uniks.stpmon.k.controller;

import javax.inject.Inject;
import javax.inject.Provider;

import de.uniks.stpmon.k.controller.popup.ModalCallback;
import de.uniks.stpmon.k.controller.popup.PopUpController;
import de.uniks.stpmon.k.controller.popup.PopUpScenario;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.service.RegionService;
import javafx.scene.Parent;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

// TODO: add all missing imports and fxml here




// TODO: dummy class
public class CreateTrainerController extends Controller {

    @Inject
    RegionService regionService;
    @Inject
    Provider<PopUpController> popUpControllerProvider;
    @Inject
    Provider<IngameController> ingameControllerProvider;

    private BooleanProperty isPopUpShown = new SimpleBooleanProperty(false);

    @Override
    public Parent render() {
        final Parent parent = super.render();
        // TODO: ...
        // these three elements have to be disabled when pop up is shown
        aPossibleTrainerSprite.disableProperty().bind(isPopUpShown);
        aPossibleCreateSpriteButton.disableProperty().bind(isPopUpShown);
        aPossibleCreateTrainerButton.disableProperty().bind(isPopUpShown);
        // TODO: ...
        return super.render();
    }
    
    // TODO: everything missing here just dummy

    public void createTrainer() {
        showPopUp(PopUpScenario.CREATE_TRAINER, result -> {
            if (!result) return;
            // TODO: get values regionId, name and image
            disposables.add(regionService
                .createTrainer(null, null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(trainer -> {
                    app.show(ingameControllerProvider.get());
                })

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
