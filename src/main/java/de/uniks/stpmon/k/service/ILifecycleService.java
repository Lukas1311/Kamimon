package de.uniks.stpmon.k.service;

/**
 * A service that provides a lifecycle method which is called when the application is closed.
 */
public interface ILifecycleService {

    /**
     * Called when the application is closed.
     * It can be used to clean up resources.
     */
    default void destroy() {
    }

}
