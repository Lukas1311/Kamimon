package de.uniks.stpmon.k.service.world;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.world.RouteData;
import de.uniks.stpmon.k.world.RouteText;
import io.reactivex.rxjava3.core.Observable;

@Singleton
public class TextDeliveryService {
    
    @Inject
    PresetService presetService;


    @Inject
    public TextDeliveryService() {

    }

    public Observable<RouteData> getRouteData(IMapProvider mapProvider) {
        TileMapData mapData = mapProvider.map();
        TileLayerData routeLayerData = (mapData.layers().isEmpty() ? null : mapData.layers().get(2));
        
        return Observable.fromIterable(routeLayerData.objects())
            .map(obj -> {
                RouteData.Builder routeDataBuilder = RouteData.builder().setData(obj);
                return routeDataBuilder.build();
            });
    }

    public String getRouteText() {
        // TODO: dummy yet
        return "";
    }
}
