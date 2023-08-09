package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.service.storage.cache.SingleCache;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.List;

@Singleton
public class WorldRepository {

    private final SingleCache<BufferedImage> minimapImage = new SingleCache<>();
    private final SingleCache<BufferedImage> regionMap = new SingleCache<>();
    private final SingleCache<List<TileProp>> props = new SingleCache<>();
    private BufferedImage[][] chunks = new BufferedImage[0][];
    private boolean isIndoor = false;

    @Inject
    public WorldRepository() {
    }

    public void setIndoor(boolean indoor) {
        isIndoor = indoor;
    }

    public boolean isIndoor() {
        return isIndoor;
    }

    public BufferedImage[][] getChunks() {
        return chunks;
    }

    public void setChunks(BufferedImage[][] chunks) {
        this.chunks = chunks;
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
            flushIfNotNull(regionMap);
        }

        flushIfNotNull(minimapImage);
        for (TileProp prop : props.asOptional().orElse(List.of())) {
            BufferedImage image = prop.image();
            if (image != null) {
                image.flush();
            }
        }
        props.reset();
        chunks = new BufferedImage[0][];
        isIndoor = false;
    }

    private void flushIfNotNull(SingleCache<BufferedImage> cache) {
        BufferedImage image = cache.asNullable();
        if (image != null) {
            image.flush();
        }
        cache.reset();
    }

    public boolean isEmpty() {
        return minimapImage.isEmpty()
                && props.isEmpty()
                && regionMap.isEmpty();
    }
}
