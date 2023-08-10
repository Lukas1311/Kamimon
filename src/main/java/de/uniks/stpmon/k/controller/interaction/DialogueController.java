package de.uniks.stpmon.k.controller.interaction;

import de.uniks.stpmon.k.controller.ToastController;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.models.dialogue.DialogueItem;
import de.uniks.stpmon.k.models.dialogue.DialogueOption;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.InteractionService;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import io.reactivex.rxjava3.core.Completable;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.inject.Inject;

public class DialogueController extends ToastController {

    public static final int DIALOGUE_HEIGHT = 126;
    private final Image[] stageImages = new Image[3];
    @FXML
    public ImageView background;
    @FXML
    public Text textContainer;
    @FXML
    public ImageView cursor;
    @FXML
    public GridPane dialoguePane;
    @FXML
    public HBox optionContainer;
    @FXML
    public Label nameLabel;
    private final DialogueOptionController[] optionTexts = new DialogueOptionController[]{
            new DialogueOptionController(),
            new DialogueOptionController(),
            new DialogueOptionController(),
    };
    @Inject
    EffectContext effectContext;
    @Inject
    InputHandler inputHandler;
    @Inject
    InteractionStorage interactionStorage;
    @Inject
    InteractionService interactionService;

    private TranslateTransition transition;

    private boolean isPressed;
    private boolean isHovered;

    private Dialogue dialogue;
    private DialogueItem item;
    private int itemIndex;
    private DialogueOption option;
    private int optionIndex;

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
        itemIndex = 0;
        State[] values = State.values();
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            State state = values[i];
            loadImage(stageImages, i, state.getTexture());
        }
        // use filters to prevent the event from being consumed by the sidebar
        onDestroy(inputHandler.addPressedKeyFilter(event -> {
            if (dialogue != null) {
                switch (event.getCode()) {
                    case A, D, W, S, LEFT, RIGHT, UP, DOWN ->
                        // Block movement
                            event.consume();
                    default -> {
                    }
                }
            }
            if (event.getCode() != KeyCode.E) {
                return;
            }

            if (dialogue == null) {
                subscribe(interactionService != null ? interactionService.tryUpdateDialogue() : Completable.complete(),
                        () -> {
                            Dialogue currentDialogue = interactionStorage.getDialogue();
                            if (currentDialogue == null || currentDialogue.isEmpty()) {
                                return;
                            }
                            openDialogue(interactionStorage.getDialogue());
                        });
                event.consume();
                return;
            }
            onActionPressed(event);
        }));
        onDestroy(inputHandler.addReleasedKeyFilter(event -> {
            if (dialogue != null) {
                switch (event.getCode()) {
                    case A, LEFT -> {
                        setOptionIndex(optionIndex - 1);
                        event.consume();
                    }
                    case D, RIGHT -> {
                        setOptionIndex(optionIndex + 1);
                        event.consume();
                    }
                    case W, S, UP, DOWN ->
                        // Block movement
                            event.consume();
                    default -> {
                    }
                }
            }
            if (event.getCode() != KeyCode.E || dialogue == null) {
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
        if (!dialogue.getTrainerName().isEmpty()) {
            this.nameLabel.setText(dialogue.getTrainerName());
        }
        setItemIndex(0);
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
        if (item == null) {
            return false;
        }
        // Consume even if still in animation to prevent skipping an option
        if (transition != null) {
            return true;
        }
        Runnable action = item.getAction();
        if (action != null) {
            action.run();
        }
        if (option != null && option.getAction() != null) {
            option.getAction().run();
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

    private void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex % dialogue.getItems().length;
        item = dialogue.getItems()[itemIndex];
        textContainer.setText(item.getText());
        optionContainer.setVisible(item.getOptions().length > 0);
        option = null;
        if (item.getOptions().length > 0) {
            applyOptions(item.getOptions());
            setOptionIndex(0);
        } else {
            optionContainer.getChildren().clear();
        }
    }

    private void setOptionIndex(int optionIndex) {
        if (item == null || option == null && item.getOptions().length == 0) {
            return;
        }

        int oldIndex = this.optionIndex;
        // add length to prevent negative values
        optionIndex = (optionIndex + item.getOptions().length) % item.getOptions().length;
        this.optionIndex = optionIndex;
        option = item.getOptions()[optionIndex];
        Runnable onSelection = option.getSelection();
        if (onSelection != null) {
            onSelection.run();
        }
        if (oldIndex != optionIndex) {
            // Reset old option
            optionTexts[oldIndex].onDeselected();
        }
        // Set new option
        optionTexts[optionIndex].onSelected();
    }

    private void applyOptions(DialogueOption[] options) {
        this.optionIndex = 0;
        ObservableList<Node> children = optionContainer.getChildren();
        children.clear();
        // width used for all options
        int optionWidth = 0;
        // Amount of spacings in the row
        int spacings = options.length + 1;
        for (int i = 0; i < options.length; i++) {
            Parent parent = createOption(children, options, i);
            optionWidth += (int) parent.getLayoutBounds().getWidth();
        }
        // Size needed per spacing
        int spacing = ((int) optionContainer.getWidth() - optionWidth) / spacings;
        optionContainer.setSpacing(spacing);
        // Destroy unused option controllers
        for (int j = options.length; j < optionTexts.length; j++) {
            optionTexts[j].destroy();
        }
    }

    private Parent createOption(ObservableList<Node> children, DialogueOption[] options, int i) {
        DialogueOptionController controller = optionTexts[i];
        controller.init();
        Parent parent = controller.render();
        parent.setId("option_" + i);
        parent.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                setOptionIndex(i);
                performAction();
                event.consume();
            }
        });
        children.add(parent);
        controller.apply(options[i]);
        return parent;
    }

    private boolean hasNext() {
        return item.getNext() != null;
    }

    public boolean doNext() {
        if (option != null && option.hasNext()) {
            setDialogue(option.getNext());
            return true;
        }
        if (!hasNext()) {
            return false;
        }
        setItemIndex(itemIndex + 1);
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
