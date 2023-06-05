package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.SVGPath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;

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
    protected Image loadImage(String image) {
        return new Image(Objects.requireNonNull(Viewable.class.getResource(image)).toString());
    }

    private class SVGData {
        private List<SVGPath> svgPaths;
        private Double width;
        private Double height;

        private SVGData(List<SVGPath> svgPaths, Double width, Double height) {
            this.svgPaths = svgPaths;
            this.width = width;
            this.height = height;
        }

        private List<SVGPath> getSVGPaths() {
            return this.svgPaths;
        }

        private Double getWidth() {
            return this.width;
        }

        private Double getHeight() {
            return this.height;
        }
    }

    private SVGData loadVectorImage(String filename) {
        URL svgUrl = Objects.requireNonNull(Viewable.class.getResource(filename));
        List<SVGPath> svgPaths = new ArrayList<>();
        Double svgWidth = 0.0, svgHeight = 0.0;
        try {
            DocumentBuilderFactory  factory = DocumentBuilderFactory.newInstance();
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
                fillColors.forEach((pClass, color) -> System.out.println(pClass + " " + color));
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
     * @param filename takes the filename of the vector image e.g. kamimonLetterling.svg
     */
    protected void setVectorImage(ImageView imageView, String filename) {
        SVGData svgData = loadVectorImage(filename);
        List<SVGPath> svgPaths = svgData.getSVGPaths();

        Group vectorGroup = new Group(svgPaths.toArray(new SVGPath[0]));
        SnapshotParameters snapshotParams = new SnapshotParameters();
        snapshotParams.setFill(Color.TRANSPARENT);
        
        WritableImage image = new WritableImage(svgData.getWidth().intValue(), svgData.getHeight().intValue());
        // render the group onto our writeableImage
        vectorGroup.snapshot(snapshotParams, image);
        imageView.setImage(image);
    }
}
