package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.world.RouteData;
import de.uniks.stpmon.k.world.RouteText;
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
        ObjectData object = new ObjectData(0, "Route 101", List.of(), List.of(new Property("Route 101", "Route", "text")), "Route", false, 0, 0, 0, 0, 0);
        TileLayerData layer = new TileLayerData(1, "Ground", List.of(), List.of(), List.of(object),
                0, 0,
                2, 2,
                0, 0, "objectgroup", true, List.of());
        return new TileMapData(
                2, 2,
                false, List.of(layer),
                1, 1,
                List.of(),
                "map");
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
}
