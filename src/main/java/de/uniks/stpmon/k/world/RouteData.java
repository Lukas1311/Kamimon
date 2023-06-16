package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;

import java.util.List;

public record RouteData(
        int id,
        RouteText routeText,
        int height,
        int width,
        int x,
        int y,
        List<PolygonPoint> polygon
) {

    public static RouteData.Builder builder() {
        return new RouteData.Builder();
    }

    public static class Builder {
        private ObjectData data;
        private final RouteText.Builder routeTextBuilder;

        private Builder() {
            routeTextBuilder = RouteText.builder();
        }

        public Builder setData(ObjectData data) {
            this.data = data;
            return this;
        }

        public RouteData build() {
            // RouteData routeData = new RouteData(data);
            int id = data.id();
            int height = data.height();
            int width = data.width();
            int x = data.x();
            int y = data.y();
            List<PolygonPoint> polygon = data.polygon();
            RouteText routeText = routeTextBuilder.setData(data).build();
            return new RouteData(id, routeText, height, width, x, y, polygon == null ? List.of() : polygon);
        }
    }
}
