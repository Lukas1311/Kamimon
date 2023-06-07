package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.service.EffectContext;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.util.Objects;

public abstract class Viewable {

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

    /**
     * Loads an image from the resource folder.
     *
     * @param image Path to the image relative to "resources/de/uniks/stpmon/k/controller"
     * @return The loaded image
     */
    private Image loadImage(String image) {
        return new Image(Objects.requireNonNull(Viewable.class.getResource(image)).toString());
    }

    /**
     * Loads an image from the resource folder and sets it to the given image array at the specified index.
     * If loadImages is false, this method does nothing.
     * This flag is used to disable image loading for tests.
     *
     * @param image Path to the image relative to "resources/de/uniks/stpmon/k/controller"
     */
    protected void loadImage(Image[] images, int index, String image) {
        if (effectContext != null &&
                effectContext.shouldSkipLoadImages()) {
            return;
        }
        images[index] = loadImage(image);
    }

    /**
     * Loads an image from the resource folder and sets it to the given ImageView.
     * If loadImages is false, this method does nothing.
     * This flag is used to disable image loading for tests.
     *
     * @param image Path to the image relative to "resources/de/uniks/stpmon/k/controller"
     */
    protected void loadImage(ImageView view, String image) {
        if (effectContext != null &&
                effectContext.shouldSkipLoadImages()) {
            return;
        }
        view.setImage(loadImage(image));
    }
}
