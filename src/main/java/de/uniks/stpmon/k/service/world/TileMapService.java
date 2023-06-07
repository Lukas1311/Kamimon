package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.service.PresetService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedInputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class TileMapService {

    @Inject
    PresetService presetService;
    @Inject
    RegionStorage regionStorage;

    @Inject
    public TileMapService() {
    }

    public Observable<Map<String, TrainerSet>> getAllTrainers() {
        return presetService.getCharacters()
                .map((characters) -> characters.stream()
                        .map((character) -> presetService.getCharacterFile(character)
                                .observeOn(Schedulers.io())
                                .map((body) -> new TrainerSet(character, ImageIO.read(new BufferedInputStream(body.byteStream())))))
                        .collect(Collectors.toList())).flatMap(Observable::merge)
                .collect(Collectors.toMap(TrainerSet::getName, Function.identity()))
                .toObservable();
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
