package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.scene.Group;
import javafx.scene.Node;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Objects;

public class NPCCollectiveView extends EntityView {

    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    Provider<NPCView> npcViewProvider;
    @Inject
    TrainerService trainerService;

    @Inject
    public NPCCollectiveView() {
    }

    @Override
    public void init() {
    }

    @Override
    public Node render() {
        Group npcGroup = new Group();
        if (regionStorage.isEmpty()) {
            return npcGroup;
        }
        disposables.add(regionService.getAllTrainer(regionStorage.getRegion()._id(), regionStorage.getArea()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(((npcs) -> {
                    for (Trainer npc : npcs) {
                        if (Objects.equals(npc._id(), trainerService.getMe()._id()) || npc == NoneConstants.NONE_TRAINER) {
                            continue;
                        }
                        NPCView npcV = getNPCView();
                        npcV.getProvider().setTrainer(npc);
                        npcV.init();
                        npcGroup.setId("npcGroup");
                        npcGroup.getChildren().add(npcV.render());
                    }
                })));
        return npcGroup;
    }

    private NPCView getNPCView() {
        return npcViewProvider.get();
    }
}
