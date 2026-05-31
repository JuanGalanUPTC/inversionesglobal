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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class RegisterController implements Initializable {

    UserService userService = new UserService();
    @FXML
    private ComboBox<String> idiomaComboBox;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordField1; // 🔍 Vinculado: Confirmar contraseña

    @FXML
    private TextField emailField1; // 🔍 Vinculado: Respuesta ciudad de nacimiento

    @FXML
    private TextField txtPasswordMascarado;

    @FXML
    private CheckBox mostrarPasswordCheckBox;

    @FXML
    private VBox warningBox;

    @FXML
    private Label warningMessage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización del ComboBox de idiomas
        idiomaComboBox.getItems().addAll("es", "en");
        idiomaComboBox.setCellFactory(param -> createCustomCell());
        idiomaComboBox.setButtonCell(createCustomCell());
        idiomaComboBox.getSelectionModel().selectFirst();

        // Ocultar caja de alertas al arrancar
        if (warningBox != null) {
            warningBox.setVisible(false);
            warningBox.setManaged(false);
        }
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
    public void togglePassword() {
        if (mostrarPasswordCheckBox.isSelected()) {
            txtPasswordMascarado.setText(passwordField.getText());
            txtPasswordMascarado.setVisible(true);
            txtPasswordMascarado.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            txtPasswordMascarado.requestFocus();
            txtPasswordMascarado.selectEnd();
        } else {
            passwordField.setText(txtPasswordMascarado.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            txtPasswordMascarado.setVisible(false);
            txtPasswordMascarado.setManaged(false);
            passwordField.requestFocus();
            passwordField.selectEnd();
        }
    }

    /**
     * Lógica de Registro con todas las validaciones de tus campos FXML
     */

    @FXML
    private void handleRegister() throws IOException {
        String email = emailField.getText().trim();

        // Si el password está visible (mascarado), tomamos ese valor
        String password = mostrarPasswordCheckBox.isSelected() ? txtPasswordMascarado.getText().trim()
                : passwordField.getText().trim();
        String confirmPassword = passwordField1.getText().trim();
        String ciudadNacimiento = emailField1.getText().trim();

        // 1. Validar que absolutamente ningún campo esté vacío
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || ciudadNacimiento.isEmpty()) {
            mostrarAlerta("Por favor, completa todos los campos del formulario.");
            return;
        }

        // 2. Validar formato de correo electrónico
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarAlerta("El formato del correo electrónico no es válido.");
            return;
        }

        // 3. Validar longitud mínima de la contraseña (Mínimo 8 caracteres)
        if (password.length() < 8) {
            mostrarAlerta("La contraseña debe tener al menos 8 caracteres.");
            return;
        }

        // 4. Validar complejidad (Una mayúscula, una minúscula, un número y un símbolo
        // específico)
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$";
        if (!password.matches(passwordRegex)) {
            mostrarAlerta("La contraseña debe incluir mayúscula, minúscula, número y un símbolo (@$!%*?&).");
            return;
        }

        // 5. Validar que ambas contraseñas coincidan rigurosamente
        if (!password.equals(confirmPassword)) {
            mostrarAlerta("Las contraseñas ingresadas no coinciden.");
            return;
        }

        try {
            // Enviar al servicio de persistencia
            userService.registerUser(email, password, ciudadNacimiento);

            // --- REGISTRO EXITOSO ---
            System.out.println("✅ ¡Usuario guardado con éxito en user.json!");
            limpiarCampos();
            backToLogin();

        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("USERNAME_ALREADY_TAKEN")) {
                mostrarAlerta("Este correo electrónico ya se encuentra registrado.");
            } else {
                mostrarAlerta("Por favor, completa todos los campos obligatorios.");
            }
        } catch (RuntimeException e) {
            mostrarAlerta("Error crítico al guardar en el archivo JSON.");
            e.printStackTrace();
        }
    }

    /**
     * Despliega la alerta de advertencia y cuenta con protección contra
     * NullPointerException
     */
    private void mostrarAlerta(String mensaje) {
        if (warningMessage == null || warningBox == null) {
            System.out.println("\n[⚠️ ALERTA EN CONSOLA]: " + mensaje + "\n");
            return;
        }

        warningMessage.setText(mensaje);
        warningBox.setVisible(true);
        warningBox.setManaged(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(event -> {
            warningBox.setVisible(false);
            warningBox.setManaged(false);
        });
        delay.play();
    }

    private void limpiarCampos() {
        emailField.clear();
        passwordField.clear();
        passwordField1.clear();
        emailField1.clear();
        txtPasswordMascarado.clear();
        mostrarPasswordCheckBox.setSelected(false);

        passwordField.setVisible(true);
        passwordField.setManaged(true);
        txtPasswordMascarado.setVisible(false);
        txtPasswordMascarado.setManaged(false);
    }

    @FXML
    public void backToLogin() throws IOException {
        App.setRoot("login");
    }
    
    
}