package de.uniks.stpmon.k.service.storage.cache;

@SuppressWarnings("unused")
public interface ICacheListener<T> {

    default void beforeAdd(T value) {
    }

    /**
     * Add a value to the cache.
     *
     * @param value The value to add.
     */
    void addValue(T value);

    /**
     * Update a value in the cache.
     *
     * @param value The value to update.
     */
    void updateValue(T value);

    default void beforeUpdate(T value) {
    }

    /**
     * Remove a value from the cache.
     *
     * @param value The value to remove.
     */
    void removeValue(T value);

    default void beforeRemove(T value) {
    }

}
