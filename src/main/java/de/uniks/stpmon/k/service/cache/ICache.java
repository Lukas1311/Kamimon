package de.uniks.stpmon.k.service.cache;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;
import java.util.Optional;

/**
 * A cache storage for a specific data type.
 * Stores the values in a list and provides a subject to listen to.
 * At the start, the values are retrieved from the server.
 * When the server sends an event, the values are updated.
 * Possible events are: create, update, delete.
 *
 * @param <T> The data type to store.
 */
public interface ICache<T> {
    /**
     * Destroy the cache and all its values.
     * <p>
     * Also removes it from its parent.
     */
    void destroy();

    /**
     * Initialize the cache and return the initial values.
     * The initial values are retrieved from the server.
     * <p>
     * The provided onDestroy runnable is executed when the cache is destroyed.
     * And can be used to clean up other dependencies.
     *
     * @param onDestroy The runnable to execute when the cache is destroyed.
     */
    void init(Runnable onDestroy);

    /**
     * Retrieves a completable that completes when the cache is initialized
     * with the initial values from the server.
     *
     * @return A completable that completes when the cache is initialized.
     */
    Completable onInitialized();

    /**
     * Retrieve a value by its id.
     *
     * @param id The id of the value to retrieve.
     * @return The value with the given id as an optional.
     */
    Optional<T> getValue(String id);

    /**
     * Retrieve all values in the cache.
     * If the values are updated the observable will emit a new list.
     *
     * @return An observable of all values in the cache.
     */
    Observable<List<T>> getValues();
}
