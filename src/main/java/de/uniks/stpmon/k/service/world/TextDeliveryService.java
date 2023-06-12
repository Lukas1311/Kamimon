package de.uniks.stpmon.k.service.world;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.world.RouteText;
import io.reactivex.rxjava3.core.Observable;

@Singleton
public class TextDeliveryService {
    
    @Inject
    PresetService presetService;


    @Inject
    public TextDeliveryService() {

    }


    public Observable<RouteText> getRouteTextData(IMapProvider mapProvider) {
        TileMapData mapData = mapProvider.map();
        Observable<RouteText> routeTextObservable = Observable.empty();
        System.out.println(mapData.tilesets());
        TileLayerData routeLayerData = (mapData.layers().isEmpty() ? null : mapData.layers().get(2));
        
        for (ObjectData obj : routeLayerData.objects()) {
            RouteText.Builder builder = RouteText.builder().setData(obj);
            RouteText routeText = builder.build();
            routeTextObservable = routeTextObservable.concatWith(Observable.just(routeText));
        }
        return routeTextObservable;
    }


    public String getRouteText() {
        System.out.println("test");
        System.out.println("test");
        return "";
    }
}
