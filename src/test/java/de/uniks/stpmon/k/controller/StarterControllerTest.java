package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.MonsterTypeDto;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StarterControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);
    @Mock
    IResourceService resourceService;
    @Mock
    PresetService presetService;

    @InjectMocks
    StarterController starterController;

    @Override
    public void start(Stage stage) throws Exception {
        // show app
        app.start(stage);
        app.show(starterController);
        stage.requestFocus();
    }

    @Test
    void testUI() {
        MonsterTypeDto monsterTypeDto = new MonsterTypeDto(1, "monster", "image.png", null, "description");
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);

        when(presetService.getMonster(anyString())).thenReturn(Observable.just(monsterTypeDto));
        when(resourceService.getMonsterImage(anyString())).thenReturn(Observable.just(image));

        starterController.setStarter("1");

        assertEquals("monster", starterController.monsterNameLabel.getText());
        assertEquals("description", starterController.descriptionText.getText());
    }

}
