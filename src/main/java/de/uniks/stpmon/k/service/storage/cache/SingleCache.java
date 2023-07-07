package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.service.DestructibleElement;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.Optional;

/**
 * A cache that can only hold a single value. The optional value can be observed for changes.
 * Optional is used to allow the cache to be empty. Nullable values are not supported by RxJava.
 * The {@link #onValue()} method can be used to observe the value for changes. If the cache is empty,
 * the supplied optional will be empty.
 *
 * @param <V> The type of the contained value.
 */
public class SingleCache<V> extends DestructibleElement {

    protected final BehaviorSubject<Optional<V>> value = BehaviorSubject.createDefault(Optional.empty());

    public void setValue(V value) {
        this.value.onNext(Optional.ofNullable(value));
    }

    /**
     * Returns the value as nullable. If the cache is empty, null will be returned.
     *
     * @return The value as nullable.
     */
    public V asNullable() {
        return asOptional().orElse(null);
    }

    /**
     * Returns the value as optional.
     * If the cache is empty, an empty optional will be returned.
     */
    public Optional<V> asOptional() {
        return value.getValue();
    }

    /**
     * Returns an observable that emits the value whenever it changes.
     * If the cache is empty, the optional will be empty.
     */
    public Observable<Optional<V>> onValue() {
        return value;
    }

    /**
     * Returns true if the cache is empty.
     */
    public boolean isEmpty() {
        return asOptional().isEmpty();
    }

    /**
     * Resets the cache to empty.
     */
    public void reset() {
        value.onNext(Optional.empty());
    }

}
