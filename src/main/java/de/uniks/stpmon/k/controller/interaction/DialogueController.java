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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.inject.Inject;

public class DialogueController extends ToastController {
    public static final int DIALOGUE_HEIGHT = 120;
    private final Image[] stageImages = new Image[3];
    @FXML
    public ImageView background;
    @FXML
    public Text textContainer;
    @FXML
    public ImageView cursor;
    @FXML
    public GridPane dialoguePane;
    @Inject
    EffectContext effectContext;
    @Inject
    InputHandler inputHandler;
    @Inject
    InteractionStorage interactionStorage;

    private TranslateTransition transition;

    private boolean isPressed;
    private boolean isHovered;

    private Dialogue dialogue;
    private DialogueOption option;
    private int index;

    @Inject
    public DialogueController() {
    }

    @Override
    public String getResourcePath() {
        return "interaction/";
    }

    @Override
    public void init() {
        super.init();
        index = 0;
        State[] values = State.values();
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            State state = values[i];
            loadImage(stageImages, i, state.getTexture());
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
                openDialogue(interactionStorage.getDialogue());
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

    private void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
        if (dialogue == null) {
            return;
        }
        setIndex(0);
    }

    public void openDialogue(Dialogue dialogue) {
        setDialogue(dialogue);
        playAnimation(false);
    }

    public void closeDialogue() {
        playAnimation(true);
        setDialogue(null);
    }

    public void playAnimation(boolean closingAnimation) {
        if (!closingAnimation) {
            dialoguePane.setVisible(true);
        }
        TranslateTransition oldTransition = transition;
        Duration currentTime = Duration.ZERO;
        if (oldTransition != null) {
            currentTime = oldTransition.getCurrentTime();
            oldTransition.stop();
        }
        transition = new TranslateTransition();
        transition.setNode(dialoguePane);
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
                dialoguePane.setVisible(false);
            }
            transition = null;
        });
    }

    private boolean performAction() {
        if (option == null) {
            return false;
        }
        // Consume even if still in animation to prevent skipping an option
        if (transition != null) {
            return true;
        }
        Runnable action = option.getAction();
        if (action != null) {
            action.run();
        }
        // Close if there is no next option
        if (!doNext()) {
            closeDialogue();
        }
        return true;
    }

    private void setState(State state) {
        if (stageImages[state.ordinal()] == null) {
            return;
        }
        cursor.setImage(stageImages[state.ordinal()]);
    }

    private void setIndex(int index) {
        this.index = index % dialogue.options().length;
        option = dialogue.options()[index];
        textContainer.setText(option.getText());
    }

    private boolean hasNext() {
        return option.getNext() != null;
    }

    public boolean doNext() {
        if (!hasNext()) {
            return false;
        }
        setIndex(index + 1);
        return true;
    }

    private void onActionPressed(InputEvent event) {
        setState(State.PRESSED);
        isPressed = true;
        event.consume();
    }

    private void onActionReleased(InputEvent event) {
        setState(isHovered ? State.HOVERED : State.DEFAULT);
        if (isPressed && performAction()) {
            event.consume();
        }
        isPressed = false;
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        parent.setVisible(false);
        loadImage(background, "interaction/dialogue_background.png");
        setState(State.DEFAULT);
        parent.setOnMouseEntered(event -> {
            isHovered = true;
            setState(State.HOVERED);
        });
        parent.setOnMouseExited(event -> {
            isHovered = false;
            setState(State.DEFAULT);
        });
        parent.setOnMousePressed(this::onActionPressed);
        parent.setOnMouseReleased(this::onActionReleased);
        return parent;
    }

    private enum State {
        DEFAULT("interaction/cursor_default.png"),
        HOVERED("interaction/cursor_hovered.png"),
        PRESSED("interaction/cursor_active.png");

        private final String texture;

        State(String texture) {
            this.texture = texture;
        }

        public String getTexture() {
            return texture;
        }
    }
}
