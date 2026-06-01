package co.edu.uptc.viewcontroller.auth;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Pos;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import co.edu.uptc.app.App;
import co.edu.uptc.model.User;
import co.edu.uptc.model.enums.UserRole;
import co.edu.uptc.service.UserService;

public class LoginController implements Initializable {
    UserService userService = new UserService();

    @FXML
    private ComboBox<String> idiomaComboBox;

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;

    @FXML
    private VBox warningBox;

    @FXML
    private Hyperlink hyperlinkOlvidastePassword;
    @FXML
    private Label warningMessage;

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

    @FXML
    public void goToRegister() throws IOException {
        App.setRoot("auth/register");
    }

    @FXML
    private TextField txtPasswordMascarado;

    @FXML
    private CheckBox mostrarPasswordCheckBox;

    @FXML
    public void togglePassword() {
        if (mostrarPasswordCheckBox.isSelected()) {
            // 1. Pasar el texto oculto al campo visible
            txtPasswordMascarado.setText(passwordField.getText());

            // 2. Intercambiar visibilidad en pantalla
            txtPasswordMascarado.setVisible(true);
            txtPasswordMascarado.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);

            // 3. Mantener el foco del teclado para que el usuario siga escribiendo sin
            // interrupciones
            txtPasswordMascarado.requestFocus();
            txtPasswordMascarado.selectEnd(); // Pone el cursor al final del texto
        } else {
            // 1. Retornar el texto al campo de máscara segura
            passwordField.setText(txtPasswordMascarado.getText());

            // 2. Intercambiar visibilidad de vuelta
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            txtPasswordMascarado.setVisible(false);
            txtPasswordMascarado.setManaged(false);

            // 3. Devolver el foco
            passwordField.requestFocus();
            passwordField.selectEnd();
        }
    }

    @FXML
    private void handleLogin() throws IOException {
        String email = emailField.getText().trim(); // El correo sí lleva .trim()

        // 🔐 Respetamos los espacios exactos de la contraseña para que BCrypt no falle
        String password;
        if (mostrarPasswordCheckBox != null && mostrarPasswordCheckBox.isSelected()) {
            password = txtPasswordMascarado.getText(); // 👈 Sin .trim()
        } else {
            password = passwordField.getText(); // 👈 Sin .trim()
        }

        // 1. Validación básica de campos vacíos en la interfaz
        if (email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Por favor, ingresa tu correo y contraseña.");
            return;
        }

        // 2. Intentar autenticar consumiendo tu UserService original
        Optional<User> usuarioAutenticado = userService.authenticate(email, password);

        if (usuarioAutenticado.isPresent()) {
            User user = usuarioAutenticado.get();
            System.out.println("✅ ¡Bienvenido! Sesión iniciada para: " + user.getEmail());

            // 1. Guardamos el usuario de forma global en la App
            App.setUsuarioLogueado(user);

            // 2. 🔀 Enrutamiento inteligente según el Rol
            if (user.getUserRole() == UserRole.ADMIN) {
                System.out.println("🛡️ Accediendo al Panel de Administración...");
                App.setRoot("admin/admin_dashboard");
            } else if (user.getUserRole() == UserRole.INVESTOR) {
                System.out.println("💼 Accediendo al Panel de Inversionista...");
                App.setRoot("investor/dashboard");
            } 
        } else {
            mostrarAlerta("Correo electrónico o contraseña incorrectos.");
            if (passwordField != null)
                passwordField.clear();
            if (txtPasswordMascarado != null)
                txtPasswordMascarado.clear();
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
        Label lblMensaje = (Label) warningBox.getChildren().stream()
                .filter(node -> node instanceof Label)
                .findFirst()
                .orElse(null);

        if (lblMensaje != null) {
            lblMensaje.setText(mensaje);
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

    @FXML
    private void handleHyperLinkOlvidastePassword() throws IOException {
        App.setRoot("auth/restore_password");
    }

    @FXML
    private void handleExit() {
        // Cerramos el programa de forma limpia liberando los recursos de JavaFX
        javafx.application.Platform.exit();

        // Forzamos la finalización del proceso por si queda algún hilo en segundo plano
        System.exit(0);
    }

}
