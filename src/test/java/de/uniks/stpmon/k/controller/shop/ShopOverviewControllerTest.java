package de.uniks.stpmon.k.controller.shop;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.NPCInfo;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.NPCInfoBuilder;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.ItemService;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;

@ExtendWith(MockitoExtension.class)
public class ShopOverviewControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);

    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);
    @Mock
    PresetService presetService;
    @Mock
    IResourceService resourceService;
    @SuppressWarnings("unused")
    @Mock
    ItemService itemService;
    @SuppressWarnings("unused")
    @Mock
    ShopOptionController shopOptionController;
    @InjectMocks
    ShopOverviewController shopOverviewController;

    final List<Item> items = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        app.start(stage);

        NPCInfo info = NPCInfoBuilder.builder().addSells(1).addSells(2).create();

        final Trainer dummytrainer = TrainerBuilder
                .builder()
                .setId("1")
                .setNpc(info)
                .create();

        Item item1 = new Item("1", dummytrainer._id(), 1, 1);
        Item item2 = new Item("2", dummytrainer._id(), 2, 2);

        items.add(item1);
        items.add(item2);

        when(resourceService.getItemImage(anyString())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));

        ItemTypeDto item1_dto = new ItemTypeDto(
                1,
                "image",
                "item1",
                1,
                "description",
                null);

        ItemTypeDto item2_dto = new ItemTypeDto(
                2,
                "image",
                "item2",
                1,
                "description",
                null);
        when(presetService.getItem(1)).thenReturn(Observable.just(item1_dto));
        when(presetService.getItem(2)).thenReturn(Observable.just(item2_dto));

        shopOverviewController.setTrainer(dummytrainer);
        shopOverviewController.initSelection();

        app.show(shopOverviewController);
        stage.requestFocus();
    }

    @Test
    void testList() {
        ListView<Item> itemView = lookup("#itemListView").queryListView();
        AnchorPane anchorPane = lookup("#shopOverview").queryAs(AnchorPane.class);

        assertNotNull(itemView);
        assertNotNull(anchorPane);

        // verify, that the items are in the list
        assertNotNull(itemView.lookup("#item_item1"));
        assertNotNull(itemView.lookup("#item_item2"));
        verifyThat("#itemListView", hasItems(2));
        verify(shopOptionController).setItem(any(Item.class));

        clickOn("#item_item2");
        verify(shopOptionController, times(2)).setItem(any(Item.class));
    }

}
