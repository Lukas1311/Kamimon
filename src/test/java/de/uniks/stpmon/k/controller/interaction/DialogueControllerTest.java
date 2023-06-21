package de.uniks.stpmon.k.controller.interaction;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.InteractionService;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static java.util.function.Predicate.not;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
class DialogueControllerTest extends ApplicationTest {

    @Spy
    InputHandler inputHandler;
    @Spy
    InteractionStorage interactionStorage;
    @Mock
    InteractionService interactionService;
    @InjectMocks
    DialogueController controller;
    @Spy
    App app = new App(null);
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext()
            .setDialogAnimationSpeed(1)
            .setSkipLoadImages(true);

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(controller);
        app.addInputHandler(inputHandler);
        stage.requestFocus();
    }

    @Test
    void checkAnimation() {
        // Precondition: Dialog invisible at start
        verifyThat("#dialoguePane", not(Node::isVisible));

        interactionStorage.setDialogue(Dialogue.builder().addItem("First").create());

        // Open dialog
        type(KeyCode.ENTER);

        // Dialog should still be visible because animation is running
        verifyThat("#dialoguePane", Node::isVisible);

        // Set low animation speed
        effectContext.setDialogAnimationSpeed(1000);

        // Close dialog slowly
        type(KeyCode.ENTER);
        waitForFxEvents();

        // Dialog should still be visible because animation is not finished
        verifyThat("#dialoguePane", Node::isVisible);

        // Press enter again to reopen the dialog
        type(KeyCode.ENTER);
        waitForFxEvents();

        // Dialog should still be visible because animation should be reset
        verifyThat("#dialoguePane", Node::isVisible);
    }

    @Test
    void openEmpty() {
        // Precondition: Dialog invisible at start
        verifyThat("#dialoguePane", not(Node::isVisible));

        type(KeyCode.ENTER);
        waitForFxEvents();

        // Dialog should still be invisible
        verifyThat("#dialoguePane", not(Node::isVisible));
    }

    @Test
    void openDialogue() {
        // Dialog invisible at start
        verifyThat("#dialoguePane", not(Node::isVisible));

        Runnable action = Mockito.mock(Runnable.class);
        interactionStorage.setDialogue(
                Dialogue.builder()
                        .addItem("First")
                        .addItem().setText("Second").addAction(action).endItem()
                        .create()
        );
        // Open dialog with enter
        type(KeyCode.ENTER);
        waitForFxEvents();

        // Dialog should be visible now
        verifyThat("#dialoguePane", Node::isVisible);
        // Shout display first text
        verifyThat("#textContainer", hasText("First"));
        // Try invalid input
        type(KeyCode.CONTROL);
        waitForFxEvents();

        // Nothing should have changed
        verify(action, never()).run();
        // Text should not have changed
        verifyThat("#textContainer", hasText("First"));
        type(KeyCode.ENTER);
        waitForFxEvents();

        // Shout display second text
        verifyThat("#textContainer", hasText("Second"));

        // Close dialog
        type(KeyCode.ENTER);
        waitForFxEvents();

        // Action should be performed after second text is displayed
        verify(action).run();

        // Dialog be closed again
        verifyThat("#dialoguePane", not(Node::isVisible));
    }
}