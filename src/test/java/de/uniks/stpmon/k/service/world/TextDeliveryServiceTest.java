package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.builder.TileMapBuilder;
import de.uniks.stpmon.k.models.map.TileMapData;
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
    public void getMonCenterPos() {

    }
}
