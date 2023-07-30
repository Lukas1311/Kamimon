package de.uniks.stpmon.k.controller.interaction;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.dialogue.Dialogue;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.InputHandler;
import de.uniks.stpmon.k.service.InteractionService;
import de.uniks.stpmon.k.service.storage.InteractionStorage;
import io.reactivex.rxjava3.core.Completable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static java.util.function.Predicate.not;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.ParentMatchers.hasChildren;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
class DialogueControllerTest extends ApplicationTest {

    @Spy
    InputHandler inputHandler;
    @Spy
    InteractionStorage interactionStorage;
    @Mock
    @SuppressWarnings("unused")
    InteractionService interactionService;
    @InjectMocks
    DialogueController controller;
    @Spy
    final App app = new App(null);
    @Spy
    @SuppressWarnings("unused")
    final EffectContext effectContext = new EffectContext()
            .setDialogAnimationSpeed(1)
            .setSkipLoadImages(true);

    @Override
    public void start(Stage stage) {
        lenient().when(interactionService.tryUpdateDialogue()).thenReturn(Completable.complete());

        app.start(stage);
        app.show(controller);
        app.addInputHandler(inputHandler);
        stage.requestFocus();
    }

    @AfterEach
    void afterEach() {
        // Remove event handlers
        app.removeInputHandler(inputHandler);
    }

    @Test
    void checkAnimation() {
        // Precondition: Dialog invisible at start
        verifyThat("#dialoguePane", not(Node::isVisible));

        interactionStorage.setDialogue(Dialogue.builder().addItem("First").create());

        // Open dialog
        type(KeyCode.E);

        // Dialog should still be visible because animation is running
        verifyThat("#dialoguePane", Node::isVisible);

        // Set low animation speed
        effectContext.setDialogAnimationSpeed(1000);

        // Close dialog slowly
        type(KeyCode.E);
        waitForFxEvents();

        // Dialog should still be visible because animation is not finished
        verifyThat("#dialoguePane", Node::isVisible);

        // Press enter again to reopen the dialog
        type(KeyCode.E);
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
    void openOptions() {
        // Dialog invisible at start
        verifyThat("#dialoguePane", not(Node::isVisible));

        Runnable firstSelection = Mockito.mock(Runnable.class);
        Runnable secondSelection = Mockito.mock(Runnable.class);
        Runnable action = Mockito.mock(Runnable.class);
        interactionStorage.setDialogue(
                Dialogue.builder()
                        .addItem().setText("FirstText")

                        .addOption().setText("FirstOption")
                        .addSelection(firstSelection)
                        .addAction(action)
                        .endOption()

                        .addOption()
                        .setText("SecondOption")
                        .addSelection(secondSelection)
                        .endOption()
                        .endItem()


                        .addItem("SecondText")

                        .create()
        );

        // Open dialog with enter
        type(KeyCode.E);
        waitForFxEvents();

        // Dialog should be visible now
        verifyThat("#dialoguePane", Node::isVisible);
        verifyThat("#textContainer", hasText("FirstText"));
        verifyThat("#optionContainer", Node::isVisible);
        verifyThat("#optionContainer", hasChildren(2));
        verifyThat("#optionContainer #option_0 #text", hasText("FirstOption"));
        verifyThat("#optionContainer #option_1 #text", hasText("SecondOption"));

        // First option should be selected
        verifyThat("#optionContainer #option_0 #indicator", Node::isVisible);
        verifyThat("#optionContainer #option_1 #indicator", not(Node::isVisible));
        verify(firstSelection).run();
        verify(secondSelection, never()).run();

        // Select second option selection
        type(KeyCode.D);
        // Check if selection was performed
        verifyThat("#optionContainer #option_0 #indicator", not(Node::isVisible));
        verifyThat("#optionContainer #option_1 #indicator", Node::isVisible);
        verify(firstSelection).run();
        verify(secondSelection).run();

        // Select first option selection again
        type(KeyCode.A);

        verifyThat("#optionContainer #option_0 #indicator", Node::isVisible);
        verifyThat("#optionContainer #option_1 #indicator", not(Node::isVisible));
        verify(firstSelection, times(2)).run();
        verify(secondSelection).run();

        // Select second option selection again (left out of bounds)
        type(KeyCode.A);

        verifyThat("#optionContainer #option_0 #indicator", not(Node::isVisible)
        );
        verifyThat("#optionContainer #option_1 #indicator", Node::isVisible);
        verify(firstSelection, times(2)).run();
        verify(secondSelection, times(2)).run();

        // Select first option selection again (right out of bounds)
        type(KeyCode.D);

        verifyThat("#optionContainer #option_0 #indicator", Node::isVisible);
        verifyThat("#optionContainer #option_1 #indicator", not(Node::isVisible));
        verify(firstSelection, times(3)).run();
        verify(secondSelection, times(2)).run();

        type(KeyCode.E);
        waitForFxEvents();

        // Action should be performed after selection
        verify(action).run();

        // Second text should be displayed
        verifyThat("#textContainer", hasText("SecondText"));
        verifyThat("#dialoguePane", Node::isVisible);

        type(KeyCode.E);
        waitForFxEvents();

        // Action of option should not have been performed again
        verify(action, times(1)).run();

        // Dialog be closed again
        verifyThat("#dialoguePane", not(Node::isVisible));
    }

    @Test
    void openOptionNext() {
        // Dialog invisible at start
        verifyThat("#dialoguePane", not(Node::isVisible));

        Dialogue firstOption = Dialogue.builder().addItem("FirstSelected").create();
        Dialogue secondOption = Dialogue.builder().addItem("SecondSelected").create();
        interactionStorage.setDialogue(
                Dialogue.builder()
                        .addItem().setText("FirstText")

                        .addOption().setText("FirstOption")
                        .setNext(firstOption)
                        .endOption()

                        .addOption()
                        .setText("SecondOption")
                        .setNext(secondOption)
                        .endOption()
                        .endItem()

                        .create()
        );

        // Open dialog with enter
        type(KeyCode.E);
        waitForFxEvents();

        // Dialog should be visible now
        verifyThat("#dialoguePane", Node::isVisible);
        verifyThat("#textContainer", hasText("FirstText"));

        // First option should be selected
        verifyThat("#optionContainer #option_0 #indicator", Node::isVisible);
        verifyThat("#optionContainer #option_1 #indicator", not(Node::isVisible));

        type(KeyCode.E);
        waitForFxEvents();
        // Next dialogue of first option should be displayed
        verifyThat("#textContainer", hasText("FirstSelected"));

        // Close dialog
        type(KeyCode.E);
        waitForFxEvents();
        verifyThat("#dialoguePane", not(Node::isVisible));

        // Dialog should be still in storage, so open again
        type(KeyCode.E);
        verifyThat("#dialoguePane", Node::isVisible);

        // Dialog should be visible again
        verifyThat("#dialoguePane", Node::isVisible);
        verifyThat("#textContainer", hasText("FirstText"));

        // Select second option
        type(KeyCode.A);
        waitForFxEvents();
        // Check if selection was performed
        verifyThat("#optionContainer #option_0 #indicator", not(Node::isVisible));
        verifyThat("#optionContainer #option_1 #indicator", Node::isVisible);

        type(KeyCode.E);
        waitForFxEvents();

        // Next dialogue of second option should be displayed
        verifyThat("#textContainer", hasText("SecondSelected"));
    }

    @Test
    void clickOnOption() {
        // Dialog invisible at start
        verifyThat("#dialoguePane", not(Node::isVisible));

        Runnable selection = Mockito.mock(Runnable.class);
        Runnable action = Mockito.mock(Runnable.class);
        interactionStorage.setDialogue(
                Dialogue.builder()
                        .addItem().setText("FirstText")

                        .addOption().setText("FirstOption")
                        .addSelection(selection)
                        .addAction(action)
                        .endOption()

                        .addOption()
                        .setText("SecondOption")
                        .endOption()
                        .endItem()

                        .create()
        );

        // Open dialog with enter
        type(KeyCode.E);
        waitForFxEvents();

        // Dialog should be visible now
        verifyThat("#dialoguePane", Node::isVisible);
        verifyThat("#textContainer", hasText("FirstText"));
        verifyThat("#optionContainer", Node::isVisible);
        verifyThat("#optionContainer", hasChildren(2));
        verifyThat("#optionContainer #option_0 #text", hasText("FirstOption"));
        verifyThat("#optionContainer #option_1 #text", hasText("SecondOption"));
        // Should be selected by default
        verify(selection, times(1)).run();

        // First option should be pressed
        clickOn("#optionContainer #option_0");
        waitForFxEvents();
        // Check if selection was performed again
        verify(selection, times(2)).run();

        // Action should be performed after selection
        verify(action).run();

        // Dialog be closed again
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
        type(KeyCode.E);
        waitForFxEvents();

        // Dialog should be visible now
        verifyThat("#dialoguePane", Node::isVisible);
        // Should display first text
        verifyThat("#textContainer", hasText("First"));
        // No option should be visible
        verifyThat("#optionContainer", not(Node::isVisible));
        // Try invalid input
        type(KeyCode.CONTROL);
        waitForFxEvents();

        // Nothing should have changed
        verify(action, never()).run();
        // Text should not have changed
        verifyThat("#textContainer", hasText("First"));
        type(KeyCode.E);
        waitForFxEvents();

        // Should display second text
        verifyThat("#textContainer", hasText("Second"));

        // Close dialog
        type(KeyCode.E);
        waitForFxEvents();

        // Action should be performed after second text is displayed
        verify(action).run();

        // Dialog be closed again
        verifyThat("#dialoguePane", not(Node::isVisible));
    }

}