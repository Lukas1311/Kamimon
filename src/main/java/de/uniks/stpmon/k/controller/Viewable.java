package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.service.EffectContext;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;

import javax.inject.Inject;

public abstract class Viewable {

    // this is a good scale for sharp images
    public static final double TEXTURE_SCALE = 4.0;

    @Inject
    protected App app;
    @Inject
    protected EffectContext effectContext;

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
     * @param completable the completable to subscribe to
     * @param onComplete  the consumer to call on each event
     */
    protected void subscribe(Completable completable, Action onComplete) {
        disposables.add(completable.observeOn(FX_SCHEDULER).subscribe(onComplete));
    }

    /**
     * Subscribes to an observable on the FX thread.
     * This method is only a utility method to avoid boilerplate code.
     *
     * @param completable the completable to subscribe to
     * @param onError     the consumer to call on an error
     */
    protected void subscribe(Completable completable, @NonNull Consumer<? super Throwable> onError) {
        disposables.add(completable.doOnError(onError).observeOn(FX_SCHEDULER).subscribe());
    }

    /**
     * Subscribes to an observable on the FX thread.
     * This method is only a utility method to avoid boilerplate code.
     *
     * @param observable the observable to subscribe to
     * @param onNext     the action to call on completion
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
    protected <@NonNull T> void subscribe(Observable<T> observable, Consumer<T> onNext, @NonNull Consumer<? super Throwable> onError) {
        disposables.add(observable.observeOn(FX_SCHEDULER).subscribe(onNext, onError));
    }


}
