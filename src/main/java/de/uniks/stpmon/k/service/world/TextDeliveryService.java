package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.world.RouteData;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
public class TextDeliveryService {

    @Inject
    public TextDeliveryService() {

    }

    public Observable<List<RouteData>> getRouteData(IMapProvider mapProvider) {
        if (mapProvider.map() != null) {
            TileMapData mapData = mapProvider.map();
            List<RouteData> routeDataList = new ArrayList<>();
            for(TileLayerData routeLayerData : mapData.layers()) {
                if(!routeLayerData.type().equals("objectgroup")){
                    continue;
                }
                for (ObjectData obj : routeLayerData.objects()) {
                    RouteData.Builder routeDataBuilder = RouteData.builder().setData(obj);
                    RouteData routeData = routeDataBuilder.build();
                    routeDataList.add(routeData);
                }
            }
            return Observable.just(routeDataList);
        }
        return Observable.just(Collections.emptyList());
    }


}
