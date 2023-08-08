package de.uniks.stpmon.k.controller.mondex;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.TrainerService;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.views.DexCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.Optional;

@Singleton
public class MonDexController extends Controller {

    @FXML
    public AnchorPane monDexPain;
    @FXML
    public ListView<MonsterTypeDto> dexList;
    @FXML
    public Text discoverdText;

    @Inject
    Provider<IngameController> ingameControllerProvider;
    @Inject
    PresetService presetService;
    @Inject
    Provider<ResourceService> resourceServiceProvider;
    @Inject
    Provider<TrainerService> trainerServiceProvider;

    private final ObservableList<MonsterTypeDto> allMonsters = FXCollections.observableArrayList();

    private MonsterTypeDto activeDetail;

    @Inject
    public MonDexController() {

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        loadBgImage(monDexPain, getResourcePath() + "monDexBox.png");

        int encountered;
        Optional<Trainer> trainer = trainerServiceProvider.get().onTrainer().blockingFirst();
        encountered = trainer.map(value -> value.encounteredMonsterTypes().size()).orElse(0);

        subscribe(presetService.getMonsters(), (monList) -> {
            if (!monList.isEmpty()) {
                int all = monList.size();
                int percentage = (encountered * 100) / all;
                if (percentage == 0 && encountered > 0) {
                    percentage = 1;
                }
                String discoverd = translateString("discovered")
                        + " " + encountered + " / " + all + " (" + percentage + "%)";
                discoverdText.setText(discoverd);
                allMonsters.addAll(monList);
                dexList.setCellFactory(param -> new DexCell(this, trainerServiceProvider));

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

    public void setMonDexImage(MonsterTypeDto monster, boolean isEncountered, ImageView monImage) {
        if (effectContext != null && effectContext.shouldSkipLoadImages()) {
            return;
        }
        subscribe(resourceServiceProvider.get().getMonsterImage(String.valueOf(monster.id())), bufferedImage -> {
            BufferedImage bufImg = bufferedImage;
            if (!isEncountered) {
                bufImg = ImageUtils.blackOutImage(bufferedImage);
            }
            Image image = ImageUtils.scaledImageFX(bufImg, 1.0);
            monImage.setImage(image);
        });
    }

    @Override
    public String getResourcePath() {
        return "mondex/";
    }
}
