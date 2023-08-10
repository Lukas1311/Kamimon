package de.uniks.stpmon.k.utils;

import de.uniks.stpmon.k.controller.Viewable;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import java.lang.reflect.Field;

public class NodeUtils {

    public static void removeNodes(Object value) {
        Field[] fields = value.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.trySetAccessible();
            Inject injectedDependency = field.getAnnotation(Inject.class);
            FXML injectedElement = field.getAnnotation(FXML.class);
            if (injectedElement != null) {
                try {
                    Object obj = field.get(value);
                    if (obj instanceof Pane pane) {
                        pane.getChildren().clear();
                    }
                    field.set(value, null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            if (injectedDependency != null) {
                try {
                    Object obj = field.get(value);
                    if (!(obj instanceof Viewable viewable)) {
                        continue;
                    }
                    viewable.destroy();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
