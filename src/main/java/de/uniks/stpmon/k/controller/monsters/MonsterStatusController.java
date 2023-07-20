package de.uniks.stpmon.k.controller.monsters;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.MonsterStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MonsterStatusController extends Controller {
    @FXML
    public Pane effectBackground;
    @FXML
    public ImageView effect;

    private final MonsterStatus status;

    public MonsterStatusController(MonsterStatus status) {
        this.status = status;
    }

    @Override
    public Parent render() {
        Parent render = super.render();
        ObservableList<String> styleClass = effectBackground.getStyleClass();
        styleClass.clear();
        styleClass.add("monster-status-background");
        styleClass.add("monster-status-" + status.toString());
        effect.setId("effect_" + status);
        loadImage(effect, status.getIconName());
        return render;
    }
}
