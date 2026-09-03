package axiom;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import axiom.ui.MainWindow;

/**
 * A GUI for AXIOM using FXML.
 */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "data/axiom.txt";

    private final Axiom axiom = new Axiom(DEFAULT_FILE_PATH);

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Axiom");
            stage.setMinHeight(600.0);
            stage.setMinWidth(400.0);
            fxmlLoader.<MainWindow>getController().setAxiom(axiom);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MainWindow.fxml", e);
        }
    }
}
