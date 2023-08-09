package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.constants.TileMapConstants;
import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.world.RouteData;
import io.reactivex.rxjava3.core.Observable;
import javafx.geometry.Point2D;

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
        TileMapData mapData = mapProvider.map();
        if (mapData == null) {
            return Observable.just(Collections.emptyList());
        }
        List<RouteData> routeDataList = new ArrayList<>();
        for (TileLayerData routeLayerData : mapData.layers()) {
            if (!routeLayerData.type().equals(TileMapConstants.OBJECT_LAYER)) {
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

    public Observable<Point2D> getNextMonCenter(IMapProvider mapProvider) {
        TileMapData mapData = mapProvider.map();
        if (mapData == null) {
            return Observable.just(Point2D.ZERO);
        }
        for (TileLayerData layerData : mapData.layers()) {
            if (!layerData.type().equals(TileMapConstants.OBJECT_LAYER)) {
                continue;
            }
            for (ObjectData obj : layerData.objects()) {
                if (obj.type() == null || !obj.type().equals("Portal")) {
                    continue;
                }
                if (obj.name() != null && obj.name().contains("Moncenter")) {
                    return Observable.just(new Point2D(obj.x() + obj.width() / 2f
                            , obj.y() + obj.height() / 2f));
                }
                for (Property prop : obj.properties()) {
                    if (prop.name().equals("Map") && prop.value().contains("Moncenter")) {
                        return Observable.just(new Point2D(obj.x() + obj.width() / 2f,
                                obj.y() + obj.height() / 2f));
                    }
                }
            }
        }
        return Observable.just(Point2D.ZERO);
    }


}
