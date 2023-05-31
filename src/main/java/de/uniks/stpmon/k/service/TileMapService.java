package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.WorldStorage;
import de.uniks.stpmon.k.utils.PropMap;
import de.uniks.stpmon.k.utils.TileMap;
import de.uniks.stpmon.k.utils.TileProp;
import de.uniks.stpmon.k.utils.Tileset;
import de.uniks.stpmon.k.utils.World;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class TileMapService {

    @Inject
    PresetService presetService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    WorldStorage worldStorage;

    @Inject
    public TileMapService() {
    }

    public Observable<World> loadTilemap() {
        if (!worldStorage.isEmpty()) {
            return Observable
                    .just(worldStorage.getWorld());
        }
        if (regionStorage.isEmpty()) {
            return Observable.empty();
        }
        Area area = regionStorage.getArea();
        if (area == null || area.map() == null) {
            return Observable.empty();
        }
        return createMap(area).map((tileMap) -> {
            BufferedImage image = tileMap.renderMap();
            PropMap propMap = new PropMap(tileMap);
            List<TileProp> props = propMap.createProps();
            World world = new World(tileMap.getLayers().get(0), image, props);
            worldStorage.setWorld(world);
            return world;
        });
    }

    private static String escapeTileUrl(String str) {
        return str.replace("../", "")
                .replace("tilesets/", "");
    }

    public Observable<TileMap> createMap(IMapProvider mapProvider) {
        TileMapData mapData = mapProvider.map();
        Observable<Tileset> imageObservable = Observable.empty();
        for (TilesetSource source : mapData.tilesets()) {
            Tileset.Builder builder = Tileset.builder().setSource(source);
            String filename = escapeTileUrl(source.source());
            imageObservable = imageObservable.concatWith(presetService.getTileset(filename).flatMap((pair) -> {
                builder.setData(pair);
                return presetService.getImage(escapeTileUrl(pair.image()));
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
