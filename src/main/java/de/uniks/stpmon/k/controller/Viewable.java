package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.utils.SVGUtils;
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
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import okhttp3.ResponseBody;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
     * @param completable the completable to subscribe to
     * @param onComplete  the consumer to call on each event
     * @param <T>         the type of the items emitted by the Observable
     */
    protected void subscribe(Completable completable, Action onComplete) {
        disposables.add(completable.observeOn(FX_SCHEDULER).subscribe(onComplete));
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

    /**
     * Method to load vector files (.svg) created with Adobe Illustrator and put them into an ImageView object.
     *
     * @param imageView takes the ImageView object where you want to put the vector graphic inside
     * @param filename  takes the filename of the vector image e.g. kamimonLetterling.svg
     */
    protected void setVectorImage(ImageView imageView, String filename) {
        if (effectContext != null &&
                effectContext.shouldSkipLoadImages()) {
            return;
        }
        SVGUtils.setVectorImage(imageView, filename);
    }

    /**
     * Processes the ResponseBody containing the image data for a trainer sprite
     *
     * @param spriteContainer Container were the sprite should be added to (preferably StackPane)
     * @param sprite          is the ImageView of the fxml where you want the image data to be loaded in
     * @param tileRow         is the row of the tile you want to extract (most cases one of 0,1,2)
     * @param tileIndex       the index of the sprite you want to extract (4th sprite => 3 because indexing starts at 0)
     * @param responseBody    is the reponse body from the api call to the preset-service that contains the direct link to the image
     */
    public void setSpriteImage(StackPane spriteContainer, ImageView sprite, int tileRow, int tileIndex, ResponseBody responseBody) {
        setSpriteImage(spriteContainer, sprite, tileRow, tileIndex, responseBody, 150, 155);
    }


    /**
     * Processes the ResponseBody containing the image data for a trainer sprite
     *
     * @param spriteContainer Container were the sprite should be added to (preferably StackPane)
     * @param sprite          is the ImageView of the fxml where you want the image data to be loaded in
     * @param tileRow         is the row of the tile you want to extract (most cases one of 0,1,2)
     * @param tileIndex       the index of the sprite you want to extract (4th sprite => 3 because indexing starts at 0)
     * @param responseBody    is the reponse body from the api call to the preset-service that contains the direct link to the image
     * @param viewWidth       is the fitWidth property of the imageview
     * @param viewHeight      is the fitHeight property of the imageview
     */
    public void setSpriteImage(StackPane spriteContainer, ImageView sprite, int tileRow, int tileIndex, ResponseBody responseBody, int viewWidth, int viewHeight) {
        if (effectContext != null && effectContext.shouldSkipLoadImages()) {
            return;
        }

        final double SCALE = 4.0; // this is a good scale for sharp images
        final int SPRITE_WIDTH = 16; // width of sprite, you could say X-value
        final int SPRITE_HEIGHT = 32; // height of sprite, you could say Y-value
        int spriteOffsetX = tileIndex * SPRITE_WIDTH;
        int spriteOffsetY = tileRow * SPRITE_HEIGHT;
        if (responseBody != null) {
            try (responseBody) {
                // Read the image data from the response body and create a BufferedImage
                ByteArrayInputStream inputStream = new ByteArrayInputStream(responseBody.bytes());
                BufferedImage bufferedImage = ImageIO.read(inputStream);

                // Extract the sprite from the original tiled image set
                BufferedImage image = bufferedImage.getSubimage(spriteOffsetX, spriteOffsetY, SPRITE_WIDTH, SPRITE_HEIGHT);

                // Scale the image
                BufferedImage scaledImage = ImageUtils.scaledImage(image, SCALE);

                // Convert the BufferedImage to JavaFX Image
                Image fxImage = SwingFXUtils.toFXImage(scaledImage, null);

                // Set the image
                sprite.setImage(fxImage);
                sprite.setFitHeight(viewHeight);
                sprite.setFitWidth(viewWidth);


                spriteContainer.setPrefSize(sprite.getFitWidth(), sprite.getFitHeight());
                // sprite center: set bottom margins so the sprite goes a little bit up
                StackPane.setMargin(sprite, new Insets(0, 0, 35, 0));
                spriteContainer.getChildren().add(sprite);
            } catch (IOException e) {
                System.err.println("Error: I/O Exception");
            }
        }
    }

}
