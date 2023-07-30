package de.uniks.stpmon.k.controller.shop;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.ItemDtoBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class ShopOptionControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);

    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);
    @Mock
    PresetService presetService;
    @Mock
    ResourceService resourceService;
    @Mock
    TrainerService trainerService;
    @Mock
    ItemService itemService;
    @InjectMocks
    ShopOptionController shopOptionController;

    final List<Item> items = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        when(resourceService.getItemImage(anyString())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));

        final Trainer dummytrainer = TrainerBuilder
                .builder()
                .setId("1")
                .setCoins(30)
                .create();

        final Trainer npc = TrainerBuilder
                .builder()
                .setId("1")
                .create();

        //buy and sell
        Item item1 = new Item("1", dummytrainer._id(), 1, 1);
        ItemTypeDto item1_dto = ItemDtoBuilder.builder()
                .setName("item1")
                .setId(1)
                .setPrice(10)
                .setDescription("desc1")
                .create();

        //buy only
        Item item2 = new Item("2", dummytrainer._id(), 2, 0);
        ItemTypeDto item2_dto = ItemDtoBuilder.builder()
                .setName("item2")
                .setId(2)
                .setPrice(10)
                .setDescription("desc2")
                .create();

        //sell only
        Item item3 = new Item("3", dummytrainer._id(), 3, 1);
        ItemTypeDto item3_dto = ItemDtoBuilder.builder()
                .setName("item3")
                .setId(3)
                .setPrice(100)
                .setDescription("desc3")
                .create();

        //no trade
        Item item4 = new Item("4", dummytrainer._id(), 4, 2);
        ItemTypeDto item4_dto = ItemDtoBuilder.builder()
                .setName("item4")
                .setId(4)
                .setPrice(0)
                .setDescription("desc4")
                .create();

        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);

        when(trainerService.onTrainer()).thenReturn(Observable.just(Optional.of(dummytrainer)));

        when(itemService.tradeItem(anyInt(), anyInt(), anyString(), anyBoolean())).thenReturn(Observable.just(item1));
        when(itemService.getItems()).thenReturn(Observable.just(items));

        when(presetService.getItem(1)).thenReturn(Observable.just(item1_dto));
        when(presetService.getItem(2)).thenReturn(Observable.just(item2_dto));
        when(presetService.getItem(3)).thenReturn(Observable.just(item3_dto));
        when(presetService.getItem(4)).thenReturn(Observable.just(item4_dto));

        shopOptionController.setTrainer(npc);

        app.show(shopOptionController);
        stage.requestFocus();
    }

    @Test
    void testOptions() {
        Button buyButton = lookup("#buyButton").queryButton();
        Button sellButton = lookup("#sellButton").queryButton();

        verifyThat("#coinsLabel", hasText("30 Coins"));
        verifyThat("#coinsDifferenceLabel", hasText(""));

        //set first Item
        shopOptionController.setItem(items.get(0));
        waitForFxEvents();
        verifyThat("#itemNameLabel", hasText("item1"));
        verifyThat("#itemDescriptionLabel", hasText("desc1"));
        verifyThat("#amountLabel", hasText("Amount: 1"));
        verifyThat("#buyPriceLabel", hasText("Buy price: 10"));
        verifyThat("#sellPriceLabel", hasText("Sell price: 5"));

        //check weather buttons are enabled
        assertFalse(buyButton.isDisabled());
        assertFalse(sellButton.isDisabled());

        //click on both buttons
        clickOn(buyButton);
        waitForFxEvents();
        verifyThat("#coinsDifferenceLabel", hasText("-10"));
        clickOn(sellButton);
        waitForFxEvents();
        verifyThat("#coinsDifferenceLabel", hasText("+5"));
        waitForFxEvents();
        
        //set second item
        //only buy option
        shopOptionController.setItem(items.get(1));
        waitForFxEvents();

        verifyThat("#coinsDifferenceLabel", hasText(""));

        verifyThat("#itemNameLabel", hasText("item2"));
        verifyThat("#itemDescriptionLabel", hasText("desc2"));
        verifyThat("#amountLabel", hasText("Amount: 0"));
        verifyThat("#buyPriceLabel", hasText("Buy price: 10"));
        verifyThat("#sellPriceLabel", hasText("Sell price: 5"));

        // check buttons
        assertFalse(buyButton.isDisabled());
        assertTrue(sellButton.isDisabled());

        //set third item
        //only buy option
        shopOptionController.setItem(items.get(2));
        waitForFxEvents();

        verifyThat("#itemNameLabel", hasText("item3"));
        verifyThat("#amountLabel", hasText("Amount: 1"));

        // check buttons
        assertTrue(buyButton.isDisabled());
        assertFalse(sellButton.isDisabled());

        //set fourth item
        //not trade options
        shopOptionController.setItem(items.get(3));
        waitForFxEvents();

        verifyThat("#itemNameLabel", hasText("item4"));
        verifyThat("#amountLabel", hasText("Amount: 2"));
        verifyThat("#buyPriceLabel", hasText("Buy price: 0"));
        verifyThat("#sellPriceLabel", hasText("Sell price: 0"));

        //check buttons are disabled
        assertTrue(buyButton.isDisabled());
        assertTrue(sellButton.isDisabled());

    }

}
