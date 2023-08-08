package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.builder.ObjectBuilder;
import de.uniks.stpmon.k.models.builder.TileMapBuilder;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.world.RouteData;
import de.uniks.stpmon.k.world.RouteText;
import io.reactivex.rxjava3.observers.TestObserver;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TextDeliveryServiceTest {

    @InjectMocks
    TextDeliveryService textDeliveryService;

    @Mock
    IMapProvider mapProvider;


    private TileMapData createDummyMap() {
        return TileMapBuilder.builder()
                .startObjectLayer()
                .startObject()
                .setName("Route 101")
                .addProperty("Route 101", "Route", "text")
                .setType("Route")
                .endObject()
                .endLayer()
                .create();
    }

    @Test
    public void testGetRouteData() {
        when(mapProvider.map()).thenReturn(createDummyMap());

        List<RouteData> routeData = textDeliveryService.
                getRouteData(mapProvider).blockingFirst();

        assertEquals(1, routeData.size());
        RouteText routeText = routeData.get(0).routeText();
        assertEquals("Route 101", routeText.name());
        assertEquals("Route", routeText.type());
        assertEquals("N/A", routeText.description());
    }

    @Test
    public void testMonCenterPosition() {
        ObjectData portalName = ObjectBuilder.builder()
                .setType("Portal")
                .setName("Moncenter")
                .setX(2)
                .setY(5)
                .setHeight(1)
                .setWidth(10).create();
        ObjectData portalProperty = ObjectBuilder.builder(portalName)
                .setName("")
                .addProperty("Map", "", "Moncenter")
                .setX(5)
                .setY(2).create();
        when(mapProvider.map()).thenReturn(TileMapBuilder.builder()
                        .startObjectLayer()
                        .addObject(portalName)
                        .endLayer()
                        .create(),
                TileMapBuilder.builder()
                        .startObjectLayer()
                        .addObject(portalProperty)
                        .endLayer()
                        .create(),
                TileMapBuilder.builder().create());

        // Find moncenter without property by name
        TestObserver<Point2D> monPos = textDeliveryService.getNextMonCenter(mapProvider).test();
        monPos.assertValueAt(0, new Point2D(7, 5.5));
        // Find the moncenter with the property
        monPos = textDeliveryService.getNextMonCenter(mapProvider).test();
        monPos.assertValueAt(0, new Point2D(10, 2.5));
        // Check again if there is no moncenter
        monPos = textDeliveryService.getNextMonCenter(mapProvider).test();
        monPos.assertValueAt(0, Point2D.ZERO);
    }
}
