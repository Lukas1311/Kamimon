package de.uniks.stpmon.k.controller.interaction;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.models.dialogue.DialogueOption;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class DialogueOptionController extends Controller {

    @FXML
    public Text indicator;
    @FXML
    public HBox container;
    @FXML
    public Text text;

    public void apply(DialogueOption dialogueOption) {
        text.setText(dialogueOption.getText());
        indicator.setVisible(false);
    }

    @Override
    public String getResourcePath() {
        return "interaction/";
    }

    public void onSelected() {
        indicator.setVisible(true);
    }

    public void onDeselected() {
        indicator.setVisible(false);
    }
}
