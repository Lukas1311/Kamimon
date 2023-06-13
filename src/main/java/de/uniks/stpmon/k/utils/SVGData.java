package de.uniks.stpmon.k.utils;

import java.util.List;

import javafx.scene.shape.SVGPath;

public class SVGData {

    private List<SVGPath> svgPaths;
    private double width;
    private double height;

    public SVGData(List<SVGPath> svgPaths, double width, double height) {
        this.svgPaths = svgPaths;
        this.width = width;
        this.height = height;
    }

    public List<SVGPath> getSVGPaths() {
        return this.svgPaths;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }
}
