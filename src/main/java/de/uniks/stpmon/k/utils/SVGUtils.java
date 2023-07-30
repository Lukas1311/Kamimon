package de.uniks.stpmon.k.utils;

import de.uniks.stpmon.k.controller.Viewable;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SVGUtils {

    private static SVGData loadVectorImage(String filename) {
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
                    Pattern regex = Pattern.compile("\\.(\\w+)\\{fill:(#[\\da-fA-F]{6});}");
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
                        // sets the correct path color dependent on the path class
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
    public static void setVectorImage(ImageView imageView, String filename) {
        SVGUtils.SVGData svgData = SVGUtils.loadVectorImage(filename);
        List<SVGPath> svgPaths = svgData.getSVGPaths();

        Group vectorGroup = new Group(svgPaths.toArray(new SVGPath[0]));
        SnapshotParameters snapshotParams = new SnapshotParameters();
        snapshotParams.setFill(Color.TRANSPARENT);

        WritableImage image = new WritableImage((int) svgData.width(), (int) svgData.height());
        // render the group onto our writeableImage
        vectorGroup.snapshot(snapshotParams, image);
        imageView.setImage(image);
    }


    private record SVGData(List<SVGPath> svgPaths, double width, double height) {

        private List<SVGPath> getSVGPaths() {
            return this.svgPaths;
        }

    }

}
