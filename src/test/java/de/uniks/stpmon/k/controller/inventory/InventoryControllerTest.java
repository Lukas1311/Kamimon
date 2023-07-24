package de.uniks.stpmon.k.controller.inventory;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.dto.ItemTypeDto;
import de.uniks.stpmon.k.models.Item;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.ItemService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.ResourceService;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
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

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest extends ApplicationTest {
    @Spy
    final App app = new App(null);

    @Mock
    TrainerStorage trainerStorage;
    @Mock
    PresetService presetService;
    @Mock
    ResourceService resourceService;
    @Mock
    Provider<ResourceService> resourceServiceProvider;
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
    public void start(Stage stage) throws Exception {
        app.start(stage);

        when(trainerStorage.onTrainer()).thenReturn(Observable.just(Optional.of(dummytrainer)));

        Item item1 = new Item("1", dummytrainer._id(), 1, 1);
        Item item2 = new Item("2", dummytrainer._id(), 2, 2);
        items.add(item1);
        items.add(item2);

        when(itemService.getItems()).thenReturn(Observable.just(items));
        when(resourceServiceProvider.get()).thenReturn(resourceService);
        when(resourceServiceProvider.get().getItemImage(anyString())).thenReturn(Observable.just(DummyConstants.EMPTY_IMAGE));

        ItemTypeDto item = new ItemTypeDto(
                1,
                "image",
                "item",
                1,
                "description",
                null);
        when(presetService.getItem(anyString())).thenReturn(Observable.just(item));

        app.show(inventoryController);
        stage.requestFocus();
    }

    @Test
    void testGUI() {
        ListView<Item> itemView = lookup("#itemListView").queryListView();
        AnchorPane anchorPane = lookup("#fullPane").queryAs(AnchorPane.class);
        ImageView coinView = lookup("#coinView").queryAs(ImageView.class);

        assertNotNull(itemView);
        assertNotNull(anchorPane);
        assertNotNull(coinView);
    }
}
