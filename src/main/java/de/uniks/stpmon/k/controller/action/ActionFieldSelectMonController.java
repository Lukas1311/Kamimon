package de.uniks.stpmon.k.controller.action;


import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.service.MonsterService;
import de.uniks.stpmon.k.service.PresetService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ActionFieldSelectMonController extends BaseActionFieldController {
    @FXML
    public HBox backBox;
    @FXML
    public HBox selectMonBox;
    @FXML
    public VBox leftContainer;
    @FXML
    public VBox rightContainer;

    @Inject
    Provider<EncounterOverviewController> encOverviewProvider;
    @Inject
    MonsterService monService;
    @Inject
    PresetService presetService;

    private int count = 0;


    @Inject
    public ActionFieldSelectMonController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        HBox optionContainer = ActionFieldController.getOptionContainer(translateString("back"));
        backBox.getChildren().add(optionContainer);
        optionContainer.setId("backOption");
        optionContainer.setOnMouseClicked(event -> {
            if (getActionField().isMonInfoOpen()) {
                encOverviewProvider.get().removeMonInfo();
                getActionField().setMonInfoOpen(false);
            }
            getActionField().openMainMenu();
        });
        loadTeam();
        return parent;
    }

    public void loadTeam() {
        List<Monster> team = monService.getTeam().blockingFirst();
        for (Monster mon : team) {
            addActionOption(mon);
        }
        count = 0;
    }

    public void addActionOption(Monster mon) {
        String name = presetService.getMonster(mon.type()).blockingFirst().name();

        HBox optionContainer = ActionFieldController.getOptionContainer(name);

        optionContainer.setOnMouseClicked(event -> {
            if (getActionField().isMonInfoOpen()) {
                encOverviewProvider.get().removeMonInfo();
            }
            encOverviewProvider.get().showMonInfo(mon);
        });

        optionContainer.setId("select_mon_" + name);

        if (count % 2 == 0) {
            leftContainer.getChildren().add(optionContainer);
        } else {
            rightContainer.getChildren().add(optionContainer);
        }
        count++;
    }

    @Override
    public String getResourcePath() {
        return "action/";
    }
}
