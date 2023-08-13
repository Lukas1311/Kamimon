package de.uniks.stpmon.k.service.storage.cache;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Provider;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

// suppress unused warning because this is a utility class
@SuppressWarnings("unused")
public class CacheProxy<C extends ICache<V, K>, V, K> implements ICache<V, K> {
    private final Supplier<Provider<C>> provider;
    private final Consumer<C> setupCallback;
    protected C cache;

    public CacheProxy(Supplier<Provider<C>> provider) {
        this(provider, null);
    }

    public CacheProxy(Supplier<Provider<C>> provider, Consumer<C> setupCallback) {
        this.provider = provider;
        this.setupCallback = setupCallback;
    }

    public Observable<C> ensureInit() {
        return Observable.just(ensureCreation())
                .flatMap((c) -> onInitialized()
                        .andThen(Observable.just(c)));
    }


    public C ensureCreation() {
        if (cache == null) {
            cache = provider.get().get();
            if (setupCallback != null) {
                setupCallback.accept(cache);
            }
            cache.init();
        }
        return cache;
    }

    private void applyIfExists(Consumer<C> callback) {
        if (cache == null) {
            return;
        }
        callback.accept(cache);
    }

    public void invalidateCache(){
        if (cache != null) {
            cache.destroy();
            cache = null;
        }
    }

    @Override
    public void destroy() {
        applyIfExists(ICache::destroy);
    }

    @Override
    public ICache<V, K> init() {
        return ensureCreation();
    }

    @Override
    public void addOnDestroy(Runnable onDestroy) {
        ensureCreation().addOnDestroy(onDestroy);
    }

    @Override
    public Completable onInitialized() {
        return ensureCreation().onInitialized();
    }

    @Override
    public K getId(V value) {
        return ensureCreation().getId(value);
    }

    @Override
    public boolean hasValue(K id) {
        return ensureCreation().hasValue(id);
    }

    @Override
    public Optional<V> getValue(K id) {
        return ensureCreation().getValue(id);
    }

    @Override
    public Observable<Optional<V>> listenValue(K id) {
        return ensureCreation().listenValue(id);
    }

    @Override
    public Observable<V> onCreation() {
        return ensureCreation().onCreation();
    }

    @Override
    public Observable<V> onUpdate() {
        return ensureCreation().onUpdate();
    }

    @Override
    public Observable<V> onDeletion() {
        return ensureCreation().onDeletion();
    }

    @Override
    public Observable<List<V>> getValues() {
        return ensureCreation().getValues();
    }

    @Override
    public Status getStatus() {
        if (cache == null) {
            return Status.UNINITIALIZED;
        }
        return cache.getStatus();
    }

    @Override
    public Collection<K> getIds() {
        return ensureCreation().getIds();
    }

    @Override
    public void addValue(V value) {
        ensureCreation().addValue(value);
    }

    @Override
    public void updateValue(V value) {
        ensureCreation().updateValue(value);
    }

    @Override
    public void removeValue(V value) {
        ensureCreation().removeValue(value);
    }
}
