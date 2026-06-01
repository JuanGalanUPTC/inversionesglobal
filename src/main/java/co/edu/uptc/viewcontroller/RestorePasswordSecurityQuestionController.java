package co.edu.uptc.viewcontroller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import co.edu.uptc.app.App;
import co.edu.uptc.model.User;
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

public class RestorePasswordSecurityQuestionController implements Initializable {
    
    UserService userService = new UserService();

    // 🛡️ Conservamos ÚNICAMENTE los elementos nativos de esta vista
    @FXML private TextField securityAnswerField;
    @FXML private ComboBox<String> idiomaComboBox;
    @FXML private VBox warningBox;
    @FXML private Label warningMessage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Agregar los identificadores de los idiomas
        idiomaComboBox.getItems().addAll("es", "en");

        // 2. Definir cómo se verán los elementos en la lista desplegable
        idiomaComboBox.setCellFactory(param -> createCustomCell());

        // 3. Definir cómo se verá el elemento SELECCIONADO en el botón principal
        idiomaComboBox.setButtonCell(createCustomCell());

        // Seleccionar el primero por defecto
        idiomaComboBox.getSelectionModel().selectFirst();

        if (warningBox != null) {
            warningBox.setVisible(false);
            warningBox.setManaged(false);
        }
    }

    /**
     * Acción del botón "Aceptar" en el formulario de la pregunta de seguridad
     */
    @FXML
    private void handleAcceptSecurityQuestionForm() throws IOException {
        String respuestaIngresada = securityAnswerField.getText().trim();

        if (respuestaIngresada.isEmpty()) {
            mostrarAlerta("Por favor, escribe tu respuesta de seguridad.");
            return;
        }

        try {
            Optional<User> userOpt = userService.findByEmail(App.emailARestablecer);
            
            if (userOpt.isPresent()) {
                // Evaluamos si la respuesta guardada en el JSON coincide con la ingresada
                if (userService.verifySecurityAnswer(userOpt.get(), respuestaIngresada)) {
                    System.out.println("✅ Respuesta correcta. Avanzando al cambio de contraseña...");
                    App.setRoot("restore_passwordFinal"); // Avanza al paso 3
                } else {
                    mostrarAlerta("La respuesta de seguridad es incorrecta.");
                }
            } else {
                mostrarAlerta("Error al recuperar los datos del usuario actual.");
            }
        } catch (RuntimeException e) {
            mostrarAlerta("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    /**
     * Muestra la alerta visual en el warningBox modificando el texto internamente
     */
    @FXML
    private void mostrarAlerta(String mensaje) {
        if (warningBox == null) {
            System.out.println("[Alerta en consola por falta de binding]: " + mensaje);
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

    // Método auxiliar que construye la fila con Imagen + Texto Código + Texto Idioma
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
        App.emailARestablecer = null; // Limpieza preventiva de memoria
        App.setRoot("login"); 
    }
}