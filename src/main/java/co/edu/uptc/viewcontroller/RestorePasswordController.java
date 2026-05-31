package co.edu.uptc.viewcontroller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uptc.app.App;
import co.edu.uptc.service.UserService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
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

public class RestorePasswordController {
    UserService userService=new UserService();

    @FXML TextField emailField;
    @FXML
    VBox warningBox;
    @FXML
    Label warningMessage;


    @FXML
    private ComboBox<String> idiomaComboBox;

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
     * Muestra la alerta visual en el warningBox modificando el texto internamente
     */
    @FXML
    private void mostrarAlerta(String mensaje) {
        if (warningBox == null) {
            System.out.println("[Alerta en consola por falta de binding]: " + mensaje);
            return;
        }

        // Buscamos el Label interno de tu VBox de manera dinámica para cambiarle el
        // texto
        warningMessage = (Label) warningBox.getChildren().stream()
                .filter(node -> node instanceof Label)
                .findFirst()
                .orElse(null);

        if (warningMessage != null) {
            warningMessage.setText(mensaje);
        }

        // Hacer aparecer la caja en la interfaz usando tus propiedades CSS
        warningBox.setVisible(true);
        warningBox.setManaged(true);

        // Desaparece automáticamente después de 4 segundos de forma limpia
        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(event -> {
            warningBox.setVisible(false);
            warningBox.setManaged(false);
        });
        delay.play();
    }

    // Método auxiliar que construye la fila con Imagen + Texto Código + Texto
    // Idioma
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

                    // Ajustamos las rutas exactamente a tu paquete personalizado
                    String imagePath = item.equals("es") ? "/co/edu/uptc/images/colflag.png"
                            : "/co/edu/uptc/images/usaflag.jpg";

                    try {
                        java.io.InputStream stream = getClass().getResourceAsStream(imagePath);
                        if (stream != null) {
                            flagView.setImage(new Image(stream));
                            container.getChildren().addAll(flagView, lblName);
                        } else {
                            System.out.println("⚠️ No se encontró la imagen en: " + imagePath);
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

    /**
     * Acción del botón "Aceptar" en el formulario de restablecer contraseña
     */
    @FXML
    private void handleAcceptEmailForm() throws IOException {
        String email = emailField.getText().trim();

        // 1. Validar que el campo no esté vacío
        if (email.isEmpty()) {
            mostrarAlerta("Por favor, escribe tu correo electrónico.");
            return;
        }

        // 2. Validar el formato del correo electrónico (Regex estándar)
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarAlerta("El formato del correo electrónico no es válido.");
            return;
        }

        try {
            // 3. 🛠️ Consumir tu nuevo método del servicio para verificar si el cliente
            // existe
            boolean usuarioExiste = userService.verifyEmailExists(email);

            if (usuarioExiste) {
                System.out.println("✅ El correo existe en user.json. Avanzando a la pregunta de seguridad...");

                // TIP: Aquí puedes guardar temporalmente el correo que se va a recuperar
                // en una variable estática de tu clase App por si la necesitas en la siguiente
                // ventana:
                // App.emailARestablecer = email;

                // 4. Redirigir a la pantalla donde le preguntas la Ciudad de Nacimiento
                // App.setRoot("securityQuestion");

                mostrarAlerta("¡Usuario encontrado! Cargando pregunta de seguridad..."); // Borra esta línea cuando
                                                                                         // tengas la otra vista
            } else {
                // Si el método devuelve false (no encontró el correo en el JSON)
                mostrarAlerta("El correo electrónico ingresado no está registrado.");
            }

        } catch (RuntimeException e) {
            // Captura el error que relanza el 'try-catch' de tu servicio si falla el
            // archivo físico
            mostrarAlerta("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void backToLogin()throws IOException{
        App.setRoot("login");
    }
}
