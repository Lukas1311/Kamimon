package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.Main;
import de.uniks.stpmon.k.service.world.TextureSetService;
import de.uniks.stpmon.k.utils.Direction;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.utils.SVGUtils;
import de.uniks.stpmon.k.world.CharacterSet;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;


public abstract class Controller extends Viewable {

    public static final Scheduler FX_SCHEDULER = Viewable.FX_SCHEDULER;

    @Inject
    protected Provider<ResourceBundle> resources;

    public Parent render() {
        return load(getClass().getSimpleName().replace("Controller", ""));
    }

    protected Parent load(String view) {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/" + getResourcePath() + view + ".fxml"));
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

    /**
     * Returns the path relative to "resources/views/" where the view is located.
     *
     * @return Returns the path relative to "resources/views/".
     */
    public String getResourcePath() {
        return "";
    }

    /**
     * Translates a key-string from a 'lang.property-file' to the corresponding value string.
     *
     * @param word the string that represents the key in the property-file that is corresponding to a string value of the specific language.
     * @param args argument values that are passed optionally.
     *             In your property file, use a scheme like '{0}' for the first parameter value, '{1}' for the second, and so on...
     *             {0} will then contain your first parameter that you provided in 'args' and so on...
     *             E.g. 'userGotDeleted=User {0} got deleted!' You would pass it to the function like this: translateString(userGotDeleted, "Bob")
     * @return the value-string (translated key-string).
     */
    public String translateString(String word, String... args) {
        String translation = resources.get().getString(word);
        for (int i = 0; i < args.length; i++) {
            translation = translation.replace("{" + i + "}", args[i]);
        }
        return translation;
    }


    /**
     * Loads an image from the resource folder.
     *
     * @param image Path to the image relative to
     *              "resources/de/uniks/stpmon/k/controller"
     * @return The loaded image
     */
    private Image loadImage(String image) {
        return new Image(Objects.requireNonNull(Viewable.class.getResource(image)).toString());
    }

    /**
     * Loads an image from the resource folder and sets it to the given image array
     * at the specified index.
     * If loadImages is false, this method does nothing.
     * This flag is used to disable image loading for tests.
     *
     * @param image Path to the image relative to
     *              "resources/de/uniks/stpmon/k/controller"
     */
    protected void loadImage(Image[] images, int index, String image) {
        if (effectContext != null && effectContext.shouldSkipLoadImages()) {
            return;
        }
        images[index] = loadImage(image);
    }

    /**
     * Loads an image from the resource folder and sets it to the given ImageView.
     * If loadImages is false, this method does nothing.
     * This flag is used to disable image loading for tests.
     *
     * @param image Path to the image relative to
     *              "resources/de/uniks/stpmon/k/controller"
     */
    protected void loadImage(ImageView view, String image) {
        if (effectContext != null && effectContext.shouldSkipLoadImages()) {
            return;
        }
        view.setImage(loadImage(image));
    }

    private BackgroundImage createBackgroundImage(Image image) {
        if (image != null) {
            return new BackgroundImage(
                    image,
                    BackgroundRepeat.SPACE,
                    BackgroundRepeat.SPACE,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            1.0,
                            1.0,
                            true,
                            true,
                            false,
                            true
                    )
            );
        } else {
            return null;
        }
    }

    /**
     * Loads an image from the resource folder and sets it to the given Region.
     * Every Region allows the placement of a background.
     * If loadImages is false, this method does nothing.
     * This flag is used to disable image loading for tests.
     *
     * @param imagePath Path to the image relative to
     *                  "resources/de/uniks/stpmon/k/controller"
     * @param element   Any element, that extends region class
     */
    protected void loadBgImage(Region element, String imagePath) {
        if (effectContext != null && effectContext.shouldSkipLoadImages()) {
            return;
        }
        Image image = loadImage(imagePath);
        BackgroundImage bg = createBackgroundImage(image);
        element.setBackground(new Background(bg));
    }

    /**
     * Method to load vector files (.svg) created with Adobe Illustrator and put
     * them into an ImageView object.
     *
     * @param imageView takes the ImageView object where you want to put the vector
     *                  graphic inside
     * @param filename  takes the filename of the vector image e.g.
     *                  kamimonLetterling.svg
     */
    protected void setVectorImage(ImageView imageView, String filename) {
        if (effectContext != null && effectContext.shouldSkipLoadImages()) {
            return;
        }
        SVGUtils.setVectorImage(imageView, filename);
    }

    /**
     * Processes the ResponseBody containing the image data for a trainer sprite
     *
     * @param spriteContainer Container were the sprite should be added to
     *                        (preferably StackPane)
     * @param sprite          is the ImageView of the fxml where you want the image
     *                        data to be loaded in
     * @param direction       viewing direction of the sprite
     * @param id              id of the used trainer
     * @param service         the service used to retrieve the texture sets
     */
    public Completable setSpriteImage(StackPane spriteContainer, ImageView sprite, Direction direction, String id, TextureSetService service) {
        return setSpriteImage(spriteContainer, sprite, direction, id, service, 150, 155);
    }

    /**
     * Processes the ResponseBody containing the image data for a trainer sprite
     *
     * @param spriteContainer Container were the sprite should be added to
     *                        (preferably StackPane)
     * @param sprite          is the ImageView of the fxml where you want the image
     *                        data to be loaded in
     * @param direction       viewing direction of the sprite
     * @param id              id of the used trainer
     * @param service         the service used to retrieve the texture sets
     * @param viewWidth       is the fitWidth property of the imageview
     * @param viewHeight      is the fitHeight property of the imageview
     */
    public Completable setSpriteImage(StackPane spriteContainer, ImageView sprite, Direction direction, String id, TextureSetService service, int viewWidth, int viewHeight) {
        if (effectContext != null && effectContext.shouldSkipLoadImages()) {
            return Completable.complete();
        }

        return service.getCharacterLazy(id).observeOn(FX_SCHEDULER).map((Optional<CharacterSet> maybeSet) -> {
            if (maybeSet.isEmpty()) {
                return Completable.complete();
            }
            CharacterSet characterSet = maybeSet.get();

            // Extract the sprite from the original tiled image set
            BufferedImage image = characterSet.getPreview(direction);

            // Scale the image
            BufferedImage scaledImage = ImageUtils.scaledImage(image, TEXTURE_SCALE);

            // Convert the BufferedImage to JavaFX Image
            Image fxImage = ImageUtils.toFXImage(scaledImage);

            // Set the image
            sprite.setImage(fxImage);
            sprite.setFitHeight(viewHeight);
            sprite.setFitWidth(viewWidth);

            spriteContainer.setPrefSize(sprite.getFitWidth(), sprite.getFitHeight());
            // sprite center: set bottom margins so the sprite goes a little bit up
            StackPane.setMargin(sprite, new Insets(0, 0, 35, 0));
            spriteContainer.getChildren().clear();
            spriteContainer.getChildren().add(sprite);
            return Completable.complete();
        }).ignoreElements();
    }
}
