package de.uniks.stpmon.k.controller.monDex;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.views.DexCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class MonDexController extends Controller {

    @FXML
    public AnchorPane monDexPane;
    @FXML
    public ListView<MonsterTypeDto> dexList;

    @Inject
    Provider<IngameController> ingameControllerProvider;
    private MonsterTypeDto activeDetail;

    @Inject
    MonsterService monService;
    @Inject
    PresetService presetService;
    @Inject
    Provider<ResourceService> resourceServiceProvider;
    @Inject
    Provider<TrainerService> trainerServiceProvider;

    private final ObservableList<MonsterTypeDto> allMonsters = FXCollections.observableArrayList();


    @Inject
    public MonDexController() {

    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadBgImage(monDexPane, "monDexBox.png");

        subscribe(presetService.getMonsters(), (monList) -> {
            if (!monList.isEmpty()) {
                //allMonsters.addAll(monList.subList(0, 10));
                allMonsters.addAll(monList);
                dexList.setCellFactory(param -> new DexCell(this, resourceServiceProvider, trainerServiceProvider));

                dexList.setItems(FXCollections.observableArrayList(allMonsters));
            }
        });


        return parent;
    }

    public void triggerDetail(MonsterTypeDto mon) {
        if (activeDetail == null) {
            openDetail(mon);
        } else {
            if (activeDetail == mon) {
                closeDetail();
            } else {
                closeDetail();
                openDetail(mon);
            }
        }
    }

    private void openDetail(MonsterTypeDto mon) {
        activeDetail = mon;
        ingameControllerProvider.get().openMonDexDetail(mon);
    }

    private void closeDetail() {
        ingameControllerProvider.get().removeChildren(2);
        activeDetail = null;
    }

    @Override
    public String getResourcePath() {
        return "monDex/";
    }
}
