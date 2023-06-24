package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.service.ILifecycleService;
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
public interface ICache<T, K> extends ILifecycleService, ICacheListener<T> {
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
     */
    ICache<T, K> init();

    /**
     * Add a runnable that will be executed when the cache is destroyed.
     *
     * @param onDestroy The runnable to execute.
     */
    void addOnDestroy(Runnable onDestroy);

    /**
     * Retrieves a completable that completes when the cache is initialized
     * with the initial values from the server.
     *
     * @return A completable that completes when the cache is initialized.
     */
    Completable onInitialized();

    /**
     * Retrieve the id of the value.
     *
     * @param value The value to retrieve the id from.
     * @return The id of the value.
     */
    K getId(T value);

    /**
     * Check if the cache has a value with the given id.
     *
     * @param id The id to check.
     * @return True if the cache has a value with the given id.
     */
    boolean hasValue(K id);

    /**
     * Retrieve a value by its id.
     *
     * @param id The id of the value to retrieve.
     * @return The value with the given id as an optional.
     */
    Optional<T> getValue(K id);

    /**
     * Retrieves an observable that emits the value with the given id.
     *
     * @param id The id of the value to retrieve.
     * @return An observable that observes the value.
     */
    Observable<Optional<T>> listenValue(K id);

    /**
     * Retrieve an observable that emits the value when they are first added to the cache.
     *
     * @return The observable.
     */
    Observable<T> onCreation();

    /**
     * Retrieve an observable that emits the value when values are updated in the cache.
     *
     * @return The observable.
     */
    Observable<T> onUpdate();

    /**
     * Retrieve an observable that emits the value when they are removed from the cache.
     *
     * @return The observable.
     */
    Observable<T> onDeletion();

    /**
     * Retrieve all values in the cache.
     * If the values are updated the observable will emit a new list.
     *
     * @return An observable of all values in the cache.
     */
    Observable<List<T>> getValues();

    /**
     * Retrieve the status of the cache.
     *
     * @return The status of the cache.
     */
    Status getStatus();

    enum Status {
        UNINITIALIZED,
        INITIALIZED,
        DESTROYED
    }

}
