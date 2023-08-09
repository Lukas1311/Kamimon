package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.IngameController;
import de.uniks.stpmon.k.controller.encounter.EncounterOverviewController;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.ItemUse;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.*;
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
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

@ExtendWith(MockitoExtension.class)
public class ItemInformationControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Mock
    ResourceService resourceService;
    @Mock
    PresetService presetService;
    @Mock
    ItemService itemService;

    @Mock
    Provider<IngameController> ingameControllerProvider;
    @Mock
    IngameController ingameController;

    @Mock
    Provider<EncounterOverviewController> encounterOverviewControllerProvider;
    @Mock
    EncounterOverviewController encounterOverviewController;


    @InjectMocks
    ItemInformationController itemInformationController;

    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    final Trainer dummytrainer = TrainerBuilder.builder().setId("1").create();
    private Item item;
    private ItemTypeDto itemTypeDto;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(resourceService.getItemImage(anyString())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));

        item = new Item("1", dummytrainer._id(), 1, 10);
        itemInformationController.setItem(item);

        itemTypeDto = new ItemTypeDto(1, "image", "item", 1, "description", ItemUse.EFFECT);

        when(presetService.getItem(anyInt())).thenReturn(Observable.just(itemTypeDto));


        app.show(itemInformationController);

        stage.requestFocus();

    }

    @Test
    void testRender() {
        AnchorPane fullBox = lookup("#itemInformationNode").queryAs(AnchorPane.class);
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

        verifyThat(useButton, hasText("Use"));
        verifyThat(useButton, Button::isVisible);
        verifyThat(nameLabel, hasText("item"));
        verifyThat(itemInformation, hasText("description"));
        verifyThat(amountText, hasText("Amount: 10"));
    }

    @Test
    void TestUseButton() {
        doNothing().when(itemService).setActiveItem(anyInt());
        doNothing().when(ingameController).removeChildren(anyInt());
        doNothing().when(ingameController).openMonsterInventory();
        doNothing().when(encounterOverviewController).openController(anyString(), any(Item.class));
        when(ingameControllerProvider.get()).thenReturn(ingameController);
        when(encounterOverviewControllerProvider.get()).thenReturn(encounterOverviewController);

        Button useButton = lookup("#useButton").queryAs(Button.class);

        itemInformationController.setInEncounter(false);
        clickOn(useButton);
        verify(itemService).setActiveItem(itemTypeDto.id());
        verify(ingameControllerProvider.get()).removeChildren(2);
        verify(ingameControllerProvider.get()).openMonsterInventory();

        itemInformationController.setInEncounter(true);
        clickOn(useButton);
        verify(encounterOverviewControllerProvider.get()).openController("monsterSelection", item);

    }

}
