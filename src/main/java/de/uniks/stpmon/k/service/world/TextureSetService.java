package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.CharacterSetCache;
import de.uniks.stpmon.k.service.storage.cache.ICache;
import de.uniks.stpmon.k.world.CharacterSet;
import de.uniks.stpmon.k.world.TileMap;
import de.uniks.stpmon.k.world.Tileset;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class TextureSetService {

    @Inject
    PresetService presetService;
    @Inject
    RegionStorage regionStorage;
    @Inject
    IResourceService resourceService;
    @Inject
    CacheManager cacheManager;

    @Inject
    public TextureSetService() {
    }

    public Optional<CharacterSet> getCharacter(String id) {
        ICache<CharacterSet, String> cache = cacheManager.characterSetCache();
        return cache.getValue(id);
    }

    public Observable<Optional<CharacterSet>> getCharacterLazy(String id) {
        CharacterSetCache cache = cacheManager.characterSetCache();
        return cache.getLazyValue(id);
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
            imageObservable = imageObservable.concatWith(resourceService
                    .getTilesetData(filename).flatMap((pair) -> {
                        builder.setData(pair);
                        return resourceService.getTilesetImage(escapeTileUrl(pair.image()));
                    }).map((image) -> {
                        builder.setImage(image);
                        return builder.build();
                    }));
        }
        return imageObservable
                .collect(Collectors.toMap(Tileset::source, Function.identity()))
                .map((images) -> new TileMap(mapProvider, images))
                .toObservable();
    }

}
