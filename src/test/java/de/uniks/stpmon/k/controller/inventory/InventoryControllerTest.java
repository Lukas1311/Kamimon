package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.ItemService;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest extends ApplicationTest {
    @Spy
    final App app = new App(null);

    @Mock
    PresetService presetService;
    @Mock
    IResourceService resourceService;
    @Mock
    ItemService itemService;

    @InjectMocks
    InventoryController inventoryController;

    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);

    final Trainer dummytrainer = TrainerBuilder.builder().setId("1").create();
    final List<Item> items = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        app.start(stage);

        Item item1 = new Item("1", dummytrainer._id(), 1, 1);
        Item item2 = new Item("2", dummytrainer._id(), 2, 2);
        items.add(item1);
        items.add(item2);

        when(itemService.getItems()).thenReturn(Observable.just(items));
        when(resourceService.getItemImage(anyString())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));

        ItemTypeDto item = new ItemTypeDto(
                1,
                "image",
                "item",
                1,
                "description",
                null);
        when(presetService.getItem(anyInt())).thenReturn(Observable.just(item));

        app.show(inventoryController);
        stage.requestFocus();
    }

    @Test
    void testGUI() {
        ListView<Item> itemView = lookup("#itemListView").queryListView();
        AnchorPane anchorPane = lookup("#inventoryPane").queryAs(AnchorPane.class);
        ImageView coinView = lookup("#coinView").queryAs(ImageView.class);

        assertNotNull(itemView);
        assertNotNull(anchorPane);
        assertNotNull(coinView);
    }
}
