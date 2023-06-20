package de.uniks.stpmon.k.controller.interaction;

import de.uniks.stpmon.k.controller.ToastController;
import de.uniks.stpmon.k.models.Dialogue;
import de.uniks.stpmon.k.models.DialogueOption;
import de.uniks.stpmon.k.service.EffectContext;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.inject.Inject;

public class DialogueController extends ToastController {
    public static final int DIALOGUE_HEIGHT = 92;
    @FXML
    public ImageView background;
    @FXML
    public Text textContainer;

    @Inject
    EffectContext effectContext;

    private TranslateTransition transition;
    /**
     * True if the dialog is closing, false if it is opening
     * Used instead of the visibility of the dialog box because visibility is changed differently in the animation
     * itself and not after the animation is done.
     */
    private boolean isClosing;

    private Dialogue dialogue;
    private DialogueOption option;
    private int index;

    @Inject
    public DialogueController() {
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
        setIndex(0);
    }

    public void openDialog(Node dialogBox, Dialogue dialogue) {
        setDialogue(dialogue);
        boolean closingAnimation = true;
        if (!dialogBox.isVisible()) {
            dialogBox.setVisible(true);
            isClosing = false;
            closingAnimation = false;
        } else {
            isClosing = !isClosing;
            if (!isClosing) {
                closingAnimation = false;
            }
        }
        TranslateTransition oldTransition = transition;
        Duration currentTime = Duration.ZERO;
        if (oldTransition != null) {
            currentTime = oldTransition.getCurrentTime();
            oldTransition.stop();
        }
        transition = new TranslateTransition();
        transition.setNode(dialogBox);
        transition.setDuration(Duration.millis(effectContext.getDialogAnimationSpeed()));
        transition.setToY(closingAnimation ? DIALOGUE_HEIGHT : 0);
        transition.setFromY(closingAnimation ? 0 : DIALOGUE_HEIGHT);
        if (currentTime.greaterThan(Duration.ZERO)) {
            // Invert the time to get the right time for the new transition
            Duration invertedTime = Duration.millis(effectContext.getDialogAnimationSpeed())
                    .subtract(currentTime);
            transition.playFrom(invertedTime);
        } else {
            transition.playFromStart();
        }
        transition.setOnFinished(event -> {
            if (isClosing) {
                dialogBox.setVisible(false);
                isClosing = false;
            }
            transition = null;
        });
    }

    @Override
    public String getResourcePath() {
        return "interaction/";
    }

    @Override
    public void init() {
        super.init();
        index = 0;
        if (dialogue != null) {
            option = dialogue.options()[index];
            textContainer.setText(option.getText());
        }
    }

    public void setIndex(int index) {
        this.index = index;
        option = dialogue.options()[index];
        textContainer.setText(option.getText());
    }

    public boolean hasNext() {
        return option.getNext() != null;
    }


    public void next() {
        if (!hasNext()) {
            return;
        }
        setIndex(index + 1);
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        loadImage(background, "interaction/dialogue_background.png");
        return parent;
    }

}
