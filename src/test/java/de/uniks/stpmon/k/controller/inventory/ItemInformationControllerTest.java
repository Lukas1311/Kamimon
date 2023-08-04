package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemInformationControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);

    @Mock
    ResourceService resourceService;
    @Mock
    PresetService presetService;

    @InjectMocks
    ItemInformationController itemInformationController;

    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    final Trainer dummytrainer = TrainerBuilder.builder().setId("1").create();

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);

        when(resourceService.getItemImage(anyString())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));

        itemInformationController.item = new Item("1", dummytrainer._id(), 1, 1);

        ItemTypeDto itemTypeDto = new ItemTypeDto(
                1,
                "image",
                "item",
                1,
                "description",
                null);
        when(presetService.getItem(anyInt())).thenReturn(Observable.just(itemTypeDto));

        app.show(itemInformationController);
        stage.requestFocus();
    }

    @Test
    void testRender() {
        AnchorPane fullBox = lookup("#fullBox").queryAs(AnchorPane.class);
        ImageView itemView = lookup("#itemView").queryAs(ImageView.class);
        Text itemInformation = lookup("#itemInformation").queryAs(Text.class);
        Text amountText = lookup("#amountText").queryAs(Text.class);
        Label nameLabel = lookup("#nameLabel").queryAs(Label.class);
        Button useButton = lookup("#useButton").queryAs(Button.class);

        assertNotNull(fullBox);
        assertNotNull(itemView);
        assertNotNull(itemInformation);
        assertNotNull(amountText);
        assertNotNull(nameLabel);
        assertNotNull(useButton);
    }
}
