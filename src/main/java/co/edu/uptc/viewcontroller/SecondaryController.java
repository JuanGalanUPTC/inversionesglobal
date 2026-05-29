package co.edu.uptc.viewcontroller;

import java.io.IOException;

import co.edu.uptc.app.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SecondaryController {
    @FXML
    private Button secondaryButton;


    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void initialize() {
        // Hint: initialize() will be called when the associated FXML has been completely loaded.
    }
}