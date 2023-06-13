package de.uniks.stpmon.k.service.world;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.world.RouteData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import io.reactivex.rxjava3.core.Observable;

@Singleton
public class TextDeliveryService {

    @Inject
    public TextDeliveryService() {

    }

    public Observable<List<RouteData>> getRouteData(IMapProvider mapProvider) {
        TileMapData mapData = mapProvider.map();
        TileLayerData routeLayerData = (mapData.layers().isEmpty() ? null : mapData.layers().get(2));
        
        List<RouteData> routeDataList = new ArrayList<>();
        for (ObjectData obj : routeLayerData.objects()) {
            RouteData.Builder routeDataBuilder = RouteData.builder().setData(obj);
            RouteData routeData = routeDataBuilder.build();
            routeDataList.add(routeData);
        }
        return Observable.just(routeDataList);
    }

    public String getRouteText() {
        // TODO: dummy yet
        return "";
    }
}
