package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerProvider;
import javafx.scene.Node;

import javax.inject.Inject;

public class NPCView extends EntityView {

    @Inject
    TrainerProvider trainerProvider;
    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;

    @Inject
    public NPCView() {
    }

    public Node render() {
        subscribe(regionService.getNPCs(regionStorage.getRegion().toString()), (list) -> {
            list.forEach(npc -> getProvider().setTrainer(npc));
        }, (error) -> {
        });

    }

    @Override
    protected TrainerProvider getProvider() {
        return trainerProvider;
    }
}
