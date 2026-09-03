package axiom.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import axiom.Axiom;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Axiom axiom;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image axiomImage = new Image(this.getClass().getResourceAsStream("/images/DaAxiom.png"));

    /**
     * Initializes the main window after FXML fields are injected.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the AXIOM instance and shows the welcome message.
     *
     * @param axiom Chatbot instance that generates replies.
     */
    public void setAxiom(Axiom axiom) {
        this.axiom = axiom;
        dialogContainer.getChildren().add(DialogBox.getAxiomDialog(axiom.getWelcomeMessage(), axiomImage));
        if (axiom.getLoadError() != null) {
            dialogContainer.getChildren().add(DialogBox.getAxiomDialog(axiom.getLoadError(), axiomImage));
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing AXIOM's reply,
     * then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = axiom.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAxiomDialog(response, axiomImage)
        );
        userInput.clear();

        if (axiom.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
