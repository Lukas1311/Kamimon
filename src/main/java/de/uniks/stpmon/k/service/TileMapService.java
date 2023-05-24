package de.uniks.stpmon.k.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stpmon.k.Main;
import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.dto.map.Area;
import de.uniks.stpmon.k.dto.map.TileMap;
import de.uniks.stpmon.k.dto.map.Tileset;
import de.uniks.stpmon.k.dto.map.TilesetSource;
import de.uniks.stpmon.k.images.TiledImage;
import de.uniks.stpmon.k.rest.PresetApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
public class TileMapService {

    @Inject
    ObjectMapper mapper;

    @Inject
    PresetApiService presetApi;

    @Inject
    public TileMapService() {
    }

    private <T> Observable<Pair<TilesetSource, T>> readValue(TilesetSource tileset, String fileName) {
        return presetApi.getTileset(fileName)
                .map((body) -> {
                    if (fileName.endsWith(".json")) {
                        Tileset value = mapper.readValue(new BufferedInputStream(body.byteStream()), Tileset.class);
                        return new Pair<>(tileset, (T) value);
                    }
                    return new Pair<>(tileset, (T) ImageIO.read(new BufferedInputStream(body.byteStream())));
                });
    }

    public TiledImage tryLoad(IMapProvider mapProvider) throws IOException {
        TileMap map = mapProvider.map();
        Observable<Pair<TilesetSource, Tileset>> imageObservable = Observable.empty();
        for (TilesetSource source : map.tilesets()) {
            String filename = source.source()
                    .replace("../", "").replace("tilesets/", "");
            System.out.println(filename);
            imageObservable = imageObservable.concatWith(readValue(source, filename));
        }
        Map<TilesetSource, Tileset> tileSets = new HashMap<>();
        for (Pair<TilesetSource, Tileset> sourcePair : imageObservable.blockingIterable()) {
            tileSets.put(sourcePair.getKey(), sourcePair.getValue());
        }
        Map<TilesetSource, BufferedImage> images = tryLoadImages(mapProvider, tileSets);
        return new TiledImage(tileSets, images);
    }

    public Map<TilesetSource, BufferedImage> tryLoadImages(IMapProvider mapProvider, Map<TilesetSource, Tileset> tileSets) throws IOException {
        Observable<Pair<TilesetSource, BufferedImage>> imageObservable = Observable.empty();
        for (Map.Entry<TilesetSource, Tileset> tileset : tileSets.entrySet()) {

            String filename = tileset.getValue().image()
                    .replace("../", "").replace("tilesets/", "");
            System.out.println(filename);
            imageObservable = imageObservable.concatWith(readValue(tileset.getKey(), filename));
        }
        Map<TilesetSource, BufferedImage> imageMap = new HashMap<>();
        for (Pair<TilesetSource, BufferedImage> sourcePair : imageObservable.blockingIterable()) {
            Tileset tileset = tileSets.get(sourcePair.getKey());
            imageMap.put(sourcePair.getKey(), sourcePair.getValue());
            ImageIO.write(sourcePair.getValue(),
                    "png",
                    new File("images/" + tileset.image()

                            .replace("../", "")));
        }
        return imageMap;
    }

    public void load() throws IOException {
        Area caveArea = mapper.readValue(
                Main.class.getResource("map_data/cave.json"), Area.class);
        tryLoad(caveArea);
        TiledImage image = loadTileset(caveArea.map());
        TileMap tileMap = caveArea.map();
        BufferedImage mergedImage = image.renderMap(tileMap);

        ImageIO.write(mergedImage, "png", new File("test.png"));
    }

    public TiledImage loadTileset(TileMap tileMap) throws IOException {
        Map<TilesetSource, Tileset> tileSets = new HashMap<>();
        Map<TilesetSource, BufferedImage> images = new HashMap<>();
        for (TilesetSource source : tileMap.tilesets()) {
            Tileset tileset = mapper.readValue(
                    Main.class.getResource("map_data/" + source.source().replace("../", "")), Tileset.class);
            BufferedImage image = ImageIO.read(
                    Objects.requireNonNull(Main.class.getResource("map_data/" + tileset.image())));
            images.put(source, image);
            tileSets.put(source, tileset);
        }
        return new TiledImage(tileSets, images);
    }
}
