package de.uniks.stpmon.k.service.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.world.RouteData;
import de.uniks.stpmon.k.world.RouteText;



@ExtendWith(MockitoExtension.class)
public class TextDeliveryServiceTest {

    @InjectMocks
    TextDeliveryService textDeliveryService;

    @Mock
    IMapProvider mapProvider;


    private TileMapData createDummyMap() {
        ChunkData chunk = new ChunkData(List.of(4, 2, 1, 3),
            2, 2,
            0, 0);
        ObjectData object = new ObjectData(0, "Route 101", List.of(), List.of(new Property("Route 101","Route", "text")), "Route", false, 0, 0, 0, 0, 0);
        TileLayerData layer = new TileLayerData(1, "Ground", List.of(chunk), List.of(object),
            0, 0,
            2, 2,
            0, 0, "tilelayer", true, List.of());
        return new TileMapData(
            2, 2,
            false, List.of(layer, layer, layer),
            1, 1,
            List.of(new TilesetSource(1, "grass.json")),
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