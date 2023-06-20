package de.uniks.stpmon.k.controller.interaction;

import de.uniks.stpmon.k.controller.ToastController;
import de.uniks.stpmon.k.models.Dialogue;
import de.uniks.stpmon.k.models.DialogueOption;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.inject.Inject;

public class DialogueController extends ToastController {
    public static final int DIALOGUE_HEIGHT = 120;
    @FXML
    public ImageView background;
    @FXML
    public Text textContainer;
    @FXML
    public ImageView cursor;
    private Parent dialogBox;
    @Inject
    EffectContext effectContext;
    @Inject
    InputHandler inputHandler;
    @Inject
    InteractionStorage interactionStorage;

    private TranslateTransition transition;
    /**
     * True if the dialog is closing, false if it is opening
     * Used instead of the visibility of the dialog box because visibility is changed differently in the animation
     * itself and not after the animation is done.
     */
    private boolean isClosing;
    private boolean isPressed;

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

    public void openDialog(Dialogue dialogue) {
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

    public void playAnimation(boolean closingAnimation) {
        if (!closingAnimation) {
            dialogBox.setVisible(true);
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
            if (closingAnimation) {
                dialogBox.setVisible(false);
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
        onDestroy(inputHandler.addPressedKeyFilter(event -> {
            if (event.getCode() != KeyCode.ENTER) {
                return;
            }

            if (dialogue == null) {
                Dialogue currentDialogue = interactionStorage.getDialogue();
                if (currentDialogue == null || currentDialogue.isEmpty()) {
                    return;
                }
                openDialog(interactionStorage.getDialogue());
                event.consume();
                return;
            }
            onActionPressed(event);
        }));
        onDestroy(inputHandler.addReleasedKeyFilter(event -> {
            if (event.getCode() != KeyCode.ENTER || dialogue == null) {
                return;
            }

            onActionReleased(event);
        }));
    }

    private boolean performAction() {
        if (option == null) {
            return false;
        }
        Runnable action = option.getAction();
        if (action != null) {
            action.run();
        }
        // Close if there is no next option
        if (!hasNext()) {
            openDialog(dialogue);
        } else {
            setIndex(index + 1);
        }
        return true;
    }

    public void setIndex(int index) {
        this.index = index % dialogue.options().length;
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

    public void onActionPressed(InputEvent event) {
        loadImage(cursor, "interaction/cursor_active.png");
        isPressed = true;
    }

    public void onActionReleased(InputEvent event) {
        if (background.isHover()) {
            loadImage(cursor, "interaction/cursor_hovered.png");
        } else {
            loadImage(cursor, "interaction/cursor_default.png");
        }
        if (isPressed && performAction()) {
            event.consume();
        }
        isPressed = false;
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        dialogBox = parent;
        loadImage(background, "interaction/dialogue_background.png");
        loadImage(cursor, "interaction/cursor_default.png");
        parent.setOnMouseEntered(event -> {
            loadImage(cursor, "interaction/cursor_hovered.png");
        });
        parent.setOnMouseExited(event -> {
            loadImage(cursor, "interaction/cursor_default.png");
        });
        parent.setOnMousePressed(this::onActionPressed);
        parent.setOnMouseReleased(this::onActionReleased);
        return parent;
    }
}
