package de.uniks.stpmon.k.service.cache;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.Optional;

/**
 * Proxy for cache storage.
 * Used to create cache storage on demand and destroy it when it is no longer needed.
 * <p>
 * In contrast to {@link CacheStorage} this can be destroyed and reinitialized multiple times.
 * Before first initialization it is not possible to get values from cache.
 * The cache has to be destroyed before it can be initialized again.
 *
 * @param <P> Type of cache storage to create.
 * @param <T> Type of data to store.
 */
public class CacheProxy<P extends ICache<T>, T> implements ICache<T> {
    @Inject
    protected Provider<P> cacheProvider;
    private P currentCache;

    @Inject
    public CacheProxy() {
    }

    @Override
    public void destroy() {
        // do nothing if cache is already destroyed or not initialized
        if (currentCache == null) {
            return;
        }
        currentCache.destroy();
        currentCache = null;
    }

    @Override
    public void init(Runnable onDestroy) {
        // Fail if cache is already initialized
        if (currentCache != null) {
            throw new IllegalStateException("Cache already initialized");
        }
        currentCache = cacheProvider.get();
        currentCache.init(onDestroy);
    }

    @Override
    public Completable onInitialized() {
        if (currentCache == null) {
            return Completable.error(new IllegalStateException("Cache not initialized"));
        }
        return currentCache.onInitialized();
    }

    @Override
    public Optional<T> getValue(String id) {
        if (currentCache == null) {
            throw new IllegalStateException("Cache not initialized");
        }
        return currentCache.getValue(id);
    }

    @Override
    public Observable<List<T>> getValues() {
        if (currentCache == null) {
            return Observable.error(new IllegalStateException("Cache not initialized"));
        }
        return currentCache.getValues();
    }
}
