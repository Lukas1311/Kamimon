package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.utils.ImageUtils;
import de.uniks.stpmon.k.utils.SVGData;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import okhttp3.ResponseBody;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private SVGData loadVectorImage(String filename) {
        URL svgUrl = Objects.requireNonNull(Viewable.class.getResource(filename));
        List<SVGPath> svgPaths = new ArrayList<>();
        double svgWidth = 0.0, svgHeight = 0.0;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // parse the svg
            Document document = builder.parse(svgUrl.openStream());

            // get the xml root node (svg is basically made of xml)
            Element root = document.getDocumentElement();
            // extract the width and height from the 'viewBox' node
            String[] viewBoxValues = root.getAttribute("viewBox").split(" ");
            svgWidth = Double.parseDouble(viewBoxValues[2]);
            svgHeight = Double.parseDouble(viewBoxValues[3]);

            NodeList elements = root.getElementsByTagName("*");
            final HashMap<String, String> fillColors = new HashMap<>();

            // process all the elements inside the svg
            for (int i = 0; i < elements.getLength(); i++) {
                Node node = elements.item(i);
                Element pathElement = (Element) node;

                // extract relevant information from the SVG element, we need the fill colors and the paths itself
                String elementName = node.getNodeName();
                // retrieve the fill groups and colors if they exist
                if (elementName.equals("style")) {
                    // retrieves the content of a node e.g. 'style' is the node and 'type' is the attribute and in between is the content
                    String styleContent = pathElement.getTextContent();

                    // regex with two match groups to extract the path group and the path color as HEX
                    Pattern regex = Pattern.compile("\\.(\\w+)\\{fill:(#[\\da-fA-F]{6});\\}");
                    Matcher matcher = regex.matcher(styleContent);

                    while (matcher.find()) {
                        String className = matcher.group(1);
                        String fillColor = matcher.group(2);
                        // populate hashmap with all available colors with mapping: path group -> color
                        fillColors.put(className, fillColor);
                    }
                }
                // retrieve the paths
                if (elementName.equals("path")) {
                    // 'd' contains one/the svg path
                    String pathClass = pathElement.getAttribute("class");
                    String pathData = pathElement.getAttribute("d");

                    // sets the current path and adds it to all paths
                    SVGPath path = new SVGPath();
                    path.setContent(pathData);
                    if (!fillColors.isEmpty()) {
                        // sets the correct path color dependant on the path class
                        path.setFill(Color.web(fillColors.get(pathClass)));
                    }
                    svgPaths.add(path);
                }
            }
        } catch (NullPointerException e) {
            System.err.println("Error: svg vector is null/empty.");
        } catch (Exception e) {
            System.err.println("Error: while loading the svg an error occured.");
        }
        return new SVGData(svgPaths, svgWidth, svgHeight);
    }

    /**
     * Method to load vector files (.svg) created with Adobe Illustrator and put them into an ImageView object.
     *
     * @param imageView takes the ImageView object where you want to put the vector graphic inside
     * @param filename  takes the filename of the vector image e.g. kamimonLetterling.svg
     */
    protected void setVectorImage(ImageView imageView, String filename) {
        if (effectContext != null && effectContext.shouldSkipLoadImages()) {
            return;
        }

        SVGData svgData = loadVectorImage(filename);
        List<SVGPath> svgPaths = svgData.getSVGPaths();

        Group vectorGroup = new Group(svgPaths.toArray(new SVGPath[0]));
        SnapshotParameters snapshotParams = new SnapshotParameters();
        snapshotParams.setFill(Color.TRANSPARENT);

        WritableImage image = new WritableImage((int) svgData.getWidth(), (int) svgData.getHeight());
        // render the group onto our writeableImage
        vectorGroup.snapshot(snapshotParams, image);
        imageView.setImage(image);
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
