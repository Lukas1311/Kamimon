package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.List;

@Singleton
public class WorldRepository {

    private final SingleCache<BufferedImage> floorImage = new SingleCache<>();
    private final SingleCache<BufferedImage> minimapImage = new SingleCache<>();
    private final SingleCache<BufferedImage> regionMap = new SingleCache<>();
    private final SingleCache<List<TileProp>> props = new SingleCache<>();

    @Inject
    public WorldRepository() {
    }

    public SingleCache<BufferedImage> floorImage() {
        return floorImage;
    }

    public SingleCache<BufferedImage> minimapImage() {
        return minimapImage;
    }

    public SingleCache<BufferedImage> regionMap() {
        return regionMap;
    }

    public SingleCache<List<TileProp>> props() {
        return props;
    }

    public void reset(boolean resetRegion) {
        if (resetRegion) {
            regionMap.reset();
        }

        floorImage.reset();
        minimapImage.reset();
        props.reset();
    }

    public boolean isEmpty() {
        return floorImage.isEmpty()
                && minimapImage.isEmpty()
                && props.isEmpty()
                && regionMap.isEmpty();
    }

}
