package de.uniks.stpmon.k.views.world;

import de.uniks.stpmon.k.constants.NoneConstants;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.TrainerAreaCache;
import de.uniks.stpmon.k.world.ShadowTransform;
import javafx.scene.Group;
import javafx.scene.Node;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NPCCollectiveView extends WorldViewable {

    private final Map<String, Node> npcViews = new HashMap<>();
    @Inject
    RegionService regionService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    Provider<NPCView> npcViewProvider;
    @Inject
    TrainerService trainerService;
    @Inject
    CacheManager cacheManager;
    TrainerAreaCache trainerCache;

    @Inject
    public NPCCollectiveView() {
    }

    @Override
    public void init() {
        super.init();
        trainerCache = cacheManager.trainerAreaCache();
    }


    @Override
    public void destroy() {
        super.destroy();
        for (Node view : npcViews.values()) {
            if (view.getUserData() instanceof NPCView npcView) {
                npcView.destroy();
            }
            view.setUserData(null);
        }
        npcViews.clear();
        npcGroup.getChildren().clear();
        npcGroup = null;
    }

    private Group npcGroup;

    @Override
    public Node render() {
        Group npcGroup = new Group();
        if (regionStorage.isEmpty()) {
            return npcGroup;
        }
        this.npcGroup = npcGroup;
        npcGroup.setId("npcGroup");
        subscribe(trainerCache.getValues().take(1),
                (npcs) -> {
                    for (Trainer npc : npcs) {
                        if (Objects.equals(npc._id(), trainerService.getMe()._id())
                                || npc == NoneConstants.NONE_TRAINER) {
                            continue;
                        }
                        addNpcView(npc);
                    }
                });
        subscribe(trainerCache.onCreation(), this::addNpcView);
        subscribe(trainerCache.onDeletion(), (trainer) -> {
            Node view = npcViews.get(trainer._id());
            if (view != null) {
                npcGroup.getChildren().remove(view);
                npcViews.remove(trainer._id());
            }
        });
        return npcGroup;
    }

    @Override
    public void updateShadow(ShadowTransform transform) {
        for (Node view : npcViews.values()) {
            if (!(view.getUserData() instanceof NPCView npcView)) {
                continue;
            }
            npcView.updateShadow(transform);
        }
    }

    private void addNpcView(Trainer trainer) {
        if (Objects.equals(trainer._id(), trainerService.getMe()._id())
                || trainer == NoneConstants.NONE_TRAINER) {
            return;
        }
        NPCView npcV = getNPCView();
        npcV.getProvider().setTrainer(trainer);
        npcV.init();
        Node rendered = npcV.render();

        rendered.setUserData(npcV);
        npcGroup.getChildren().add(rendered);
        npcViews.put(trainer._id(), rendered);
    }

    private NPCView getNPCView() {
        return npcViewProvider.get();
    }

}
