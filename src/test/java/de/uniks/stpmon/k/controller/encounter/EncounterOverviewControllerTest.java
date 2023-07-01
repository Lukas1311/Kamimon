package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.Monster;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.MonsterBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.MonsterService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EncounterOverviewControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock
    IResourceService resourceService;
    @Mock
    MonsterService monsterService;
    @Mock
    Provider<StatusController> statusControllerProvider;
    // @Mock
    // TrainerStorage trainerStorage;

    @InjectMocks
    EncounterOverviewController encounterOverviewController;

    Trainer dummytrainer = TrainerBuilder.builder().setId("1").create();
    List<Monster> userMonsterList = new ArrayList<>();
    List<Monster> opponentMonsterList = new ArrayList<>();
    BufferedImage monsterImage = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);

        Monster userMonster1 = MonsterBuilder.builder().setId("2").setTrainer(dummytrainer._id()).setType(1).create();
        Monster userMonster2 = MonsterBuilder.builder().setId("3").setTrainer(dummytrainer._id()).setType(2).create();
        userMonsterList.add(userMonster1);
        userMonsterList.add(userMonster2);

        when(resourceService.getMonsterImage(any())).thenReturn(Observable.just(monsterImage));
        // when(monsterService.getTeam()).thenReturn(Observable.just(userMonsterList));

        when(statusControllerProvider.get()).thenAnswer(invocation -> {
            VBox statusBox = new VBox();
            statusBox.setStyle("-fx-background-color: black;");
            statusBox.setPrefWidth(50);
            statusBox.setPrefHeight(20);
            StatusController statusController = mock(StatusController.class);
            when(statusController.render()).thenReturn(statusBox);
            return statusController;
        });

        encounterOverviewController.userMonstersList = userMonsterList;

        opponentMonsterList.addAll(List.of(
                MonsterBuilder.builder().setId("2").setType(1).create(),
                MonsterBuilder.builder().setId("3").setType(2).create()
        ));

        app.show(encounterOverviewController);
        stage.requestFocus();
    }

    @Test
    void testRender() {
        VBox userMonstersBox = lookup("#userMonsters").queryAs(VBox.class);
        VBox opponentMonstersBox = lookup("#opponentMonsters").queryAs(VBox.class);
        assertNotNull(userMonstersBox);
        assertNotNull(opponentMonstersBox);
        assertNotNull(encounterOverviewController);
        sleep(4000);
    }

    // @Test
    // void testLoadMonsterImage() {
    //     VBox userMonsters = mock(VBox.class);
    //     ImageView userMonster0 = mock(ImageView.class);
    //     MonsterTypeDto monsterTypeDto = new MonsterTypeDto(1, "monster", "image", null, "description");
    //     BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);

    //     encounterOverviewController.userMonsters = userMonsters;
    //     encounterOverviewController.userMonster0 = userMonster0;

    //     when(resourceService.getMonsterImage(anyString())).thenReturn(Observable.just(image));

    //     encounterOverviewController.loadMonsterImage(monsterTypeDto.id().toString(), userMonster0, 0);
    //     waitForFxEvents();

    //     assertEquals(2, encounterOverviewController.userMonsters.getChildren().size());
    // }
}
