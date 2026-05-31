package co.edu.uptc.viewcontroller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uptc.app.App;
import co.edu.uptc.service.UserService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class RestorePasswordController implements Initializable {
    
    UserService userService = new UserService();

    // 🛡️ En esta pantalla SOLO existen estos componentes básicos
    @FXML private TextField emailField;
    @FXML private ComboBox<String> idiomaComboBox;
    @FXML private VBox warningBox;
    @FXML private Label warningMessage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización del combobox de idioma
        idiomaComboBox.getItems().addAll("es", "en");
        idiomaComboBox.setCellFactory(param -> createCustomCell());
        idiomaComboBox.setButtonCell(createCustomCell());
        idiomaComboBox.getSelectionModel().selectFirst();

        if (warningBox != null) {
            warningBox.setVisible(false);
            warningBox.setManaged(false);
        }
    }

    /**
     * Acción del botón "Siguiente" o "Aceptar" en el formulario del correo
     */
    @FXML
    private void handleAcceptEmailForm() throws IOException {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            mostrarAlerta("Por favor, escribe tu correo electrónico.");
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarAlerta("El formato del correo electrónico no es válido.");
            return;
        }

        try {
            boolean emailExiste = userService.verifyEmailExists(email);

            if (emailExiste) {
                System.out.println("✅ El correo existe en user.json. Guardando puente y avanzando...");
                
                // 🔑 Guardamos el correo en la variable estática global de App
                App.emailARestablecer = email;

                // Saltamos a la segunda interfaz (Asegúrate de que este FXML use el controlador "RestorePasswordSecurityQuestionController")
                App.setRoot("restore_passwordSecurityQuestion");
            } else {
                mostrarAlerta("El correo electrónico ingresado no está registrado.");
            }

        } catch (RuntimeException e) {
            mostrarAlerta("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarAlerta(String mensaje) {
        if (warningBox == null) {
            System.out.println("[Alerta en consola]: " + mensaje);
            return;
        }

        warningMessage = (Label) warningBox.getChildren().stream()
                .filter(node -> node instanceof Label)
                .findFirst()
                .orElse(null);

        if (warningMessage != null) {
            warningMessage.setText(mensaje);
        }

        warningBox.setVisible(true);
        warningBox.setManaged(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(event -> {
            warningBox.setVisible(false);
            warningBox.setManaged(false);
        });
        delay.play();
    }

    private ListCell<String> createCustomCell() {
        return new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);

                    ImageView flagView = new ImageView();
                    flagView.setFitWidth(24);
                    flagView.setFitHeight(16);
                    flagView.setPreserveRatio(true);

                    Label lblName = new Label(item.equals("es") ? "Español" : "English");
                    lblName.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

                    String imagePath = item.equals("es") ? "/co/edu/uptc/images/colflag.png"
                            : "/co/edu/uptc/images/usaflag.jpg";

                    try {
                        java.io.InputStream stream = getClass().getResourceAsStream(imagePath);
                        if (stream != null) {
                            flagView.setImage(new Image(stream));
                            container.getChildren().addAll(flagView, lblName);
                        } else {
                            container.getChildren().add(lblName);
                        }
                    } catch (Exception e) {
                        container.getChildren().add(lblName);
                    }
                    setGraphic(container);
                }
            }
        };
    }
    

    @FXML
    public void backToLogin() throws IOException {
        App.setRoot("login");
    }
}