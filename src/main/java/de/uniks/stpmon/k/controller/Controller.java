package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.Main;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ResourceBundle;


public abstract class Controller {

    @Inject
    protected App app;
    @Inject
    protected Provider<ResourceBundle> resources;

    public static final Scheduler FX_SCHEDULER = Schedulers.from(Platform::runLater);

    protected CompositeDisposable disposables = new CompositeDisposable();

    public void init() {

    }

    public void destroy() {
        disposables.dispose();
        disposables = new CompositeDisposable();
    }

    public void onDestroy(Runnable action) {
        disposables.add(Disposable.fromRunnable(action));
    }

    /**
     * Subscribes to an observable on the FX thread.
     * This method is only a utility method to avoid boilerplate code.
     *
     * @param observable the observable to subscribe to
     * @param onNext     the consumer to call on each event
     * @param <T>        the type of the items emitted by the Observable
     */
    protected <@NonNull T> void subscribe(Observable<T> observable, Consumer<T> onNext) {
        disposables.add(observable.observeOn(FX_SCHEDULER).subscribe(onNext));
    }

    /**
     * Subscribes to an observable on the FX thread.
     * This method is only a utility method to avoid boilerplate code.
     *
     * @param observable the observable to subscribe to
     * @param onNext     the consumer to call on each event
     * @param onError    the consumer to call on an error
     * @param <T>        the type of the items emitted by the Observable
     */
    protected <@NonNull T> void subscribe(Observable<T> observable,
                                          Consumer<T> onNext,
                                          @NonNull Consumer<? super Throwable> onError) {
        disposables.add(observable.observeOn(FX_SCHEDULER).subscribe(onNext, onError));
    }

    public Parent render() {
        return load(getClass().getSimpleName().replace("Controller", ""));
    }

    protected Parent load(String view) {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/" + view + ".fxml"));
        loader.setControllerFactory(c -> this);
        if (resources != null) {
            loader.setResources(resources.get());
        }
        try {
            return loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected String translateString(String word) {
        return resources.get().getString(word);
    }
}
