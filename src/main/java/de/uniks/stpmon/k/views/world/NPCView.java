package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.storage.TrainerProvider;
import de.uniks.stpmon.k.service.world.MovementHandler;

import javax.inject.Inject;

public class NPCView extends EntityView {

    @Inject
    protected MovementHandler movementHandler;
    @Inject
    TrainerProvider trainerProvider;

    @Inject
    public NPCView() {
    }

    @Override
    protected TrainerProvider getProvider() {
        return trainerProvider;
    }

    @Override
    public MovementHandler getMovementHandler() {
        return movementHandler;
    }
}
