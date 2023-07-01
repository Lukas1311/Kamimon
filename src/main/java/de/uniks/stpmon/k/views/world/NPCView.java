package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.storage.TrainerProvider;

import javax.inject.Inject;

public class NPCView extends EntityView {

    @Inject
    TrainerProvider trainerProvider;

    @Inject
    public NPCView() {
    }

    @Override
    protected TrainerProvider getProvider() {
        return trainerProvider;
    }

}
