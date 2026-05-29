package co.edu.uptc.viewcontroller;

import java.io.IOException;

import co.edu.uptc.app.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrimaryController {
    @FXML
    private Button primaryButton;


    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
