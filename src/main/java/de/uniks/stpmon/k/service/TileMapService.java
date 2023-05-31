package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.utils.TileMap;
import de.uniks.stpmon.k.utils.Tileset;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class TileMapService {

    @Inject
    PresetService presetService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    WorldStorage tileMapStorage;

    @Inject
    public TileMapService() {
    }

    public Observable<TileMap> loadTilemap() {
        if (regionStorage.isEmpty()) {
            return Observable.empty();
        }
        Area area = regionStorage.getArea();
        if (area == null || area.map() == null) {
            return Observable.empty();
        }
        return createMap(area).map((tileMap)->{
            tileMapStorage.setTileMap(tileMap);
            return tileMap;
        });
    }

    public Observable<TileMap> createMap(IMapProvider mapProvider) {
        TileMapData mapData = mapProvider.map();
        Observable<Tileset> imageObservable = Observable.empty();
        for (TilesetSource source : mapData.tilesets()) {
            Tileset.Builder builder = Tileset.builder().setSource(source);
            String filename = source.source()
                    .replace("../", "").replace("tilesets/", "");
            imageObservable = imageObservable.concatWith(presetService.getTileset(filename).flatMap((pair) -> {
                builder.setData(pair);
                return presetService.getImage(pair.image().replace("../", "").replace("tilesets/", ""));
            }).map((image) -> {
                builder.setImage(image);
                return builder.build();
            }));
        }
        return imageObservable
                .observeOn(Schedulers.io())
                .collect(Collectors.toMap(Tileset::source, Function.identity()))
                .map((images) -> new TileMap(mapProvider, images))
                .toObservable();
    }

}
