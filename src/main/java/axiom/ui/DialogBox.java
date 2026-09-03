package axiom.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        dialog.setMaxWidth(320.0);
        HBox.setHgrow(dialog, Priority.ALWAYS);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box for a message sent by the user.
     *
     * @param text Message text.
     * @param img User display picture.
     * @return Dialog box aligned to the right.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Returns a dialog box for a reply from AXIOM.
     *
     * @param text Reply text.
     * @param img AXIOM display picture.
     * @return Dialog box aligned to the left.
     */
    public static DialogBox getAxiomDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        // Monospaced font keeps the ASCII banner and separator lines aligned.
        db.dialog.setFont(Font.font("Monospaced", 12));
        return db;
    }
}
