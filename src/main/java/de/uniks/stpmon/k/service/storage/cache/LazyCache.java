package de.uniks.stpmon.k.service.storage.cache;

import io.reactivex.rxjava3.core.Observable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class LazyCache<T, K> extends SimpleCache<T, K> {

    protected abstract Observable<T> requestValue(K id);

    /**
     * Loads all values from the cache or requests them from the server if they are not cached yet.
     *
     * @param ids All values to be loaded
     * @return A list of all values that were loaded
     */
    public Observable<List<T>> getLazyValues(Collection<K> ids) {
        return Observable.just(ids.stream().map(this::getLazyValue).toList())
                .flatMap(Observable::merge)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList()
                .toObservable();
    }

    public Observable<Optional<T>> getLazyValue(K id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (status == Status.DESTROYED) {
            throw new IllegalStateException("Cache already destroyed");
        }
        Optional<T> optional = getValue(id);
        if (optional.isPresent()) {
            return Observable.just(optional);
        }
        // wait for initialization to complete if not initialized yet
        return onInitialized()
                .andThen(listenValue(id).take(1).flatMap((t) -> {
                    if (t.isPresent()) {
                        return Observable.just(t);
                    }
                    return requestValue(id).map(
                            value -> {
                                addValue(value);
                                return Optional.of(value);
                            }
                    );
                }));
    }

    public Observable<T> singleLazyValue(K id) {
        return getLazyValue(id).flatMap(op ->
                op.map(Observable::just).orElse(Observable.empty()));
    }

}
