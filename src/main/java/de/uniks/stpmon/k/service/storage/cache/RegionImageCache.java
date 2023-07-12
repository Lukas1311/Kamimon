package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.dto.IMapProvider;
import de.uniks.stpmon.k.models.map.RegionImage;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.List;

public class RegionImageCache extends LazyCache<RegionImage, String> {

    @Inject
    TextureSetService textureSetService;
    @Inject
    RegionApiService regionApiService;

    @Inject
    public RegionImageCache() {
    }

    @Override
    protected Observable<RegionImage> requestValue(String id) {
        return regionApiService.getRegion(id)
                .flatMap((region) -> textureSetService.createMap(region))
                .map((tileMap) -> {
                    IMapProvider mapProvider = tileMap.getProvider();
                    BufferedImage image = tileMap.renderMap();
                    BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(),
                            BufferedImage.TYPE_BYTE_GRAY);
                    Graphics g = grayscaleImage.getGraphics();
                    g.drawImage(image, 0, 0, null);
                    g.dispose();
                    RescaleOp op = new RescaleOp(.5f, -15, null);
                    grayscaleImage = op.filter(grayscaleImage, null);
                    return new RegionImage(mapProvider._id(), image, grayscaleImage);
                });
    }

    @Override
    protected Observable<List<RegionImage>> getInitialValues() {
        return Observable.empty();
    }

    @Override
    public String getId(RegionImage value) {
        return value.id();
    }
}
