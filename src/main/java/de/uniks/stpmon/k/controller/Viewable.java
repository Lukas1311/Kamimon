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
import javafx.scene.Group;
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
import java.util.List;
import java.util.Objects;

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
            
            // process all the elements inside the svg
            for (int i = 0; i < elements.getLength(); i++) {
                Node node = elements.item(i);
                Element pathElement = (Element) node;

                // extract relevant information from the SVG element (we only need the paths from the 'd'-node)
                String elementName = node.getNodeName();
                if (elementName.equals("path")) {
                    // 'd' contains one/the svg path
                    String pathData = pathElement.getAttribute("d");

                    SVGPath path = new SVGPath();
                    path.setContent(pathData);
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

    protected void setVectorImage(ImageView imageView, String filename) {
        SVGData svgData = loadVectorImage(filename);
        List<SVGPath> svgPaths = svgData.getSVGPaths();

        Group vectorGroup = new Group(svgPaths.toArray(new SVGPath[0]));
        WritableImage image = new WritableImage(svgData.getWidth(), svgData.getHeight());
        // render the group onto our writeableImage
        vectorGroup.snapshot(null, image);
        imageView.setImage(image);
    }
}
