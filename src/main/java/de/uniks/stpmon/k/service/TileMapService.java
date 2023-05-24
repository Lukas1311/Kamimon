package de.uniks.stpmon.k.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.dto.map.TileMapData;
import de.uniks.stpmon.k.dto.map.TilesetData;
import de.uniks.stpmon.k.dto.map.TilesetSource;
import de.uniks.stpmon.k.images.TileMap;
import de.uniks.stpmon.k.images.Tileset;
import de.uniks.stpmon.k.rest.PresetApiService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class TileMapService {

    @Inject
    ObjectMapper mapper;

    @Inject
    PresetApiService presetApi;

    @Inject
    public TileMapService() {
    }

    private Observable<BufferedImage> fetchImage(String fileName) {
        return presetApi.getFile(fileName)
                .observeOn(Schedulers.io())
                .map((body) -> ImageIO.read(new BufferedInputStream(body.byteStream())));
    }

    private Observable<TilesetData> fetchTileset(String fileName) {
        return presetApi.getFile(fileName)
                .observeOn(Schedulers.io())
                .map((body) -> mapper.readValue(new BufferedInputStream(body.byteStream()), TilesetData.class));
    }

    public TileMap loadMap(IMapProvider mapProvider) throws IOException {
        TileMapData mapData = mapProvider.map();
        Observable<Tileset> imageObservable = Observable.empty();
        for (TilesetSource source : mapData.tilesets()) {
            Tileset.Builder builder = Tileset.builder().setSource(source);
            String filename = source.source()
                    .replace("../", "").replace("tilesets/", "");
            System.out.println(filename);
            imageObservable = imageObservable.concatWith(fetchTileset(filename).flatMap((pair) -> {
                builder.setData(pair);
                return fetchImage(pair.image().replace("../", "").replace("tilesets/", ""));
            }).map((image) -> {
                builder.setImage(image);
                return builder.build();
            }));
        }
        return new TileMap(mapData, imageObservable.blockingStream()
                .collect(Collectors.toMap(Tileset::source, Function.identity())));
    }

    public BufferedImage loadImage(IMapProvider provider) throws IOException {
//        Area caveArea = mapper.readValue(
//                Main.class.getResource("map_data/cave.json"), Area.class);
        TileMap map = loadMap(provider);
        return map.renderMap();
//        TileMap image = loadTileset(caveArea.map());
//        TileMapData tileMap = caveArea.map();
//        BufferedImage mergedImage = image.renderMap(tileMap);
//
//        ImageIO.write(mergedImage, "png", new File("test.png"));
    }

//    public TileMap loadTileset(TileMapData tileMap) throws IOException {
//        Map<TilesetSource, TilesetData> tileSets = new HashMap<>();
//        Map<TilesetSource, BufferedImage> images = new HashMap<>();
//        for (TilesetSource source : tileMap.tilesets()) {
//            TilesetData tileset = mapper.readValue(
//                    Main.class.getResource("map_data/" + source.source().replace("../", "")), TilesetData.class);
//            BufferedImage image = ImageIO.read(
//                    Objects.requireNonNull(Main.class.getResource("map_data/" + tileset.image())));
//            images.put(source, image);
//            tileSets.put(source, tileset);
//        }
//        return new TileMap(tileSets, images);
//    }
}
