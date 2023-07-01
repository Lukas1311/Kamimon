package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;

public record RouteText(
        String name,
        String description,
        String type
) {

    public static RouteText.Builder builder() {
        return new RouteText.Builder();
    }

    public static class Builder {

        private ObjectData data;


        private Builder() {
        }

        public Builder setData(ObjectData data) {
            this.data = data;
            return this;
        }

        public RouteText build() {
            String name = data.name();
            String type = data.type();
            String description = "N/A";
            if (data.properties() != null) {
                for (Property property : data.properties()) {
                    if (property.name().equals("Description")) {
                        description = property.value();
                    }
                }
            }
            return new RouteText(name, description, type);
        }

    }

}
