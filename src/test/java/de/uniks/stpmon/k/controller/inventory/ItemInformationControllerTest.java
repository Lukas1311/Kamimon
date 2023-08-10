package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.StarterController;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.ItemUse;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.ItemDtoBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.dummies.EventDummy;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class ItemInformationControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);

    @Mock
    ResourceService resourceService;
    @Mock
    PresetService presetService;
    @Mock
    EventListener eventListener;

    @InjectMocks
    ItemInformationController itemInformationController;

    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    final Trainer dummytrainer = TrainerBuilder.builder().setId("1").create();
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    final EventDummy eventDummy = component.eventDummy();

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);

        when(resourceService.getItemImage(anyString())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));

        when(resourceBundleProvider.get()).thenReturn(resources);

        stage.requestFocus();
    }

    @Test
    void testRender() {
        ItemTypeDto itemTypeDto = ItemDtoBuilder.builder()
                .setId(10)
                .setImage("image")
                .setDescription("description")
                .setPrice(10)
                .create();
        when(presetService.getItem(anyInt())).thenReturn(Observable.just(itemTypeDto));

        //set item for test
        Item item = new Item("10", dummytrainer._id(), 1, 1);
        itemInformationController.setItem(item);

        app.show(itemInformationController);

        Platform.runLater(() -> {
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
        });
        waitForFxEvents();
    }

    @Test
    void openItemBox() {
        ItemTypeDto itemTypeDto = ItemDtoBuilder.builder()
                .setId(10)
                .setName("Item")
                .setImage("image")
                .setDescription("description")
                .setItemUse(ItemUse.ITEM_BOX)
                .setPrice(10)
                .create();
        when(presetService.getItem(anyInt())).thenReturn(Observable.just(itemTypeDto));

        //set item for test
        Item item = new Item("10", dummytrainer._id(), 1, 1);
        itemInformationController.setItem(item);

        Platform.runLater(() -> app.show(itemInformationController));
        waitForFxEvents();

        itemInformationController.useItem();

        StarterController starterController = mock(StarterController.class);
        starterController.monsterNameLabel = new Label();
        starterController.descriptionText = new Text();

        assertNotNull(starterController);

        clickOn("#useButton");

        itemInformationController.useItem();

        eventDummy.sendEvent(new Event<>("trainers.%s.items.%s.%s"
                .formatted(dummytrainer._id(), "1", "updated"),
                ItemDtoBuilder.builder(itemTypeDto)
                        .setName("Item")
                        .create()
        ));
        waitForFxEvents();

        verifyThat(starterController.descriptionText, Node::isVisible);
        verifyThat(starterController.descriptionText, hasText("You got: Item"));
    }

    @Test
    void openMonBox() {
        ItemTypeDto itemTypeDto = ItemDtoBuilder.builder()
                .setId(10)
                .setName("Monster")
                .setImage("image")
                .setDescription("description")
                .setItemUse(ItemUse.MONSTER_BOX)
                .setPrice(10)
                .create();
        when(presetService.getItem(anyInt())).thenReturn(Observable.just(itemTypeDto));

        //set item for test
        Item item = new Item("10", dummytrainer._id(), 1, 1);
        itemInformationController.setItem(item);

        Platform.runLater(() -> app.show(itemInformationController));
        waitForFxEvents();

        itemInformationController.useItem();

        StarterController starterController = mock(StarterController.class);
        starterController.monsterNameLabel = new Label("1");
        starterController.descriptionText = new Text("description");

        assertNotNull(starterController);
        assertEquals("1", starterController.monsterNameLabel.getText());
        assertEquals("description", starterController.descriptionText.getText());

        clickOn("#useButton");

        itemInformationController.useItem();

        waitForFxEvents();
    }

    @AfterEach
    void closeAll() {
        itemInformationController.destroy();
    }
}
