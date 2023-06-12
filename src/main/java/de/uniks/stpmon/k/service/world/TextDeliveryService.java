package de.uniks.stpmon.k.service.world;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.service.PresetService;
import io.reactivex.rxjava3.core.Observable;

@Singleton
public class TextDeliveryService {
    
    @Inject
    PresetService presetService;


    @Inject
    public TextDeliveryService() {

    }


    public Observable<TileLayerData> getTileMapData(IMapProvider mapProvider) {
        TileMapData mapData = mapProvider.map();
        System.out.println(mapData.tilesets());
        TileLayerData layerData = (mapData.layers().isEmpty() ? null : mapData.layers().get(2));
        System.out.println(layerData);

        Observable<TileLayerData> layerObservable = Observable.empty();
        return layerObservable;
    }


    public String getRouteText() {
        System.out.println("test");
        System.out.println("test");
        return "";
    }
}
