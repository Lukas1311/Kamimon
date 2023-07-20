package de.uniks.stpmon.k.controller.action;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.service.EncounterService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.SessionService;
import de.uniks.stpmon.k.service.storage.EncounterStorage;

import javax.inject.Inject;
import javax.inject.Provider;

public abstract class BaseActionFieldController extends Controller {

    @Inject
    Provider<ActionFieldController> actionFieldControllerProvider;
    @Inject
    EncounterService encounterService;
    @Inject
    EncounterStorage encounterStorage;
    @Inject
    SessionService sessionService;
    @Inject
    PresetService presetService;

    protected ActionFieldController getActionField() {
        return actionFieldControllerProvider.get();
    }

}
