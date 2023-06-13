package de.uniks.stpmon.k.utils;

import java.util.List;

import javafx.scene.shape.SVGPath;

public class SVGData {

    private List<SVGPath> svgPaths;
    private Double width;
    private Double height;

    public SVGData(List<SVGPath> svgPaths, Double width, Double height) {
        this.svgPaths = svgPaths;
        this.width = width;
        this.height = height;
    }

    public List<SVGPath> getSVGPaths() {
        return this.svgPaths;
    }

    public Double getWidth() {
        return this.width;
    }

    public Double getHeight() {
        return this.height;
    }
}
