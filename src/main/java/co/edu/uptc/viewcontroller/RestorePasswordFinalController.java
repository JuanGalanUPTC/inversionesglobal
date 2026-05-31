package co.edu.uptc.viewcontroller;

import java.io.IOException;
import java.util.Optional;

import co.edu.uptc.app.App;
import co.edu.uptc.model.User;
import co.edu.uptc.security.PasswordEncoder;
import co.edu.uptc.service.UserService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class RestorePasswordFinalController {

    UserService userService = new UserService();

    @FXML private ComboBox<String> idiomaComboBox;
    @FXML private VBox warningBox;
    @FXML private Label warningMessage;

    // 🔒 Primera Contraseña
    @FXML private PasswordField passwordField;
    @FXML private TextField txtPasswordMascarado;
    @FXML private CheckBox mostrarPasswordCheckBox;

    // 🔒 Confirmación de Contraseña
    @FXML private PasswordField passwordField1;
    @FXML private TextField txtConfirmPasswordMascarado;

    @FXML
    public void backToLogin() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void handleUpdatePassword() throws IOException {
        String nuevaPassword = "";
        String confirmacionPassword = "";

        // 1. Validar qué campos leer según el estado del CheckBox
        if (mostrarPasswordCheckBox != null && mostrarPasswordCheckBox.isSelected()) {
            nuevaPassword = (txtPasswordMascarado != null) ? txtPasswordMascarado.getText().trim() : "";
            confirmacionPassword = (txtConfirmPasswordMascarado != null) ? txtConfirmPasswordMascarado.getText().trim() : "";
        } else {
            nuevaPassword = (passwordField != null) ? passwordField.getText().trim() : "";
            confirmacionPassword = (passwordField1 != null) ? passwordField1.getText().trim() : "";
        }

        // 2. Verificar que no haya campos vacíos
        if (nuevaPassword.isEmpty() || confirmacionPassword.isEmpty()) {
            mostrarAlerta("Por favor, completa ambos campos de contraseña.");
            return;
        }

        // 3. Verificar que coincidan exactamente en texto plano
        if (!nuevaPassword.equals(confirmacionPassword)) {
            mostrarAlerta("Las contraseñas ingresadas no coinciden.");
            return;
        }

        // 4. 🛡️ Validaciones de Complejidad y Seguridad (Regex)
        // Exige: Mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial
        String regexSegura = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{8,}$";
        
        if (!nuevaPassword.matches(regexSegura)) {
            mostrarAlerta("La contraseña debe tener mínimo 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&.).");
            return;
        }

        try {
            Optional<User> userOpt = userService.findByEmail(App.emailARestablecer);

            if (userOpt.isPresent()) {
                User usuarioAActualizar = userOpt.get();

                // 🔐 Encriptamos usando tu clase utilitaria PasswordEncoder
                String passwordEncriptada = PasswordEncoder.encode(nuevaPassword);
                usuarioAActualizar.setPassword(passwordEncriptada);

                boolean guardadoExitoso = userService.updateUserInPersistence(usuarioAActualizar);

                if (guardadoExitoso) {
                    System.out.println("✅ Contraseña segura restablecida con éxito para: " + App.emailARestablecer);
                    App.emailARestablecer = null; // Limpiar puente de datos
                    App.setRoot("restore_passwordSuccess");
                } else {
                    mostrarAlerta("No se pudo actualizar la contraseña. Inténtalo de nuevo.");
                }
            } else {
                mostrarAlerta("Error: Sesión de restablecimiento perdida.");
            }
        } catch (RuntimeException e) {
            mostrarAlerta("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void togglePassword() {
        // Alerta de seguridad preventiva si los componentes no están vinculados en el FXML
        if (passwordField1 == null || txtConfirmPasswordMascarado == null) {
            System.err.println("❌ Error: Faltan los fx:id de la confirmación en el FXML.");
            return;
        }

        if (mostrarPasswordCheckBox.isSelected()) {
            // Sincronizar textos de oculto -> visible plano
            txtPasswordMascarado.setText(passwordField.getText());
            txtConfirmPasswordMascarado.setText(passwordField1.getText());

            // Mostrar campos planos / Ocultar campos con asteriscos
            txtPasswordMascarado.setVisible(true);
            txtPasswordMascarado.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);

            txtConfirmPasswordMascarado.setVisible(true);
            txtConfirmPasswordMascarado.setManaged(true);
            passwordField1.setVisible(false);
            passwordField1.setManaged(false);

            txtPasswordMascarado.requestFocus();
            txtPasswordMascarado.selectEnd();
        } else {
            // Sincronizar textos de visible plano -> oculto
            passwordField.setText(txtPasswordMascarado.getText());
            passwordField1.setText(txtConfirmPasswordMascarado.getText());

            // Mostrar campos con asteriscos / Ocultar campos planos
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            txtPasswordMascarado.setVisible(false);
            txtPasswordMascarado.setManaged(false);

            passwordField1.setVisible(true);
            passwordField1.setManaged(true);
            txtConfirmPasswordMascarado.setVisible(false);
            txtConfirmPasswordMascarado.setManaged(false);

            passwordField.requestFocus();
            passwordField.selectEnd();
        }
    }

    @FXML
    private void mostrarAlerta(String mensaje) {
        if (warningBox == null) {
            System.out.println("[Alerta en consola]: " + mensaje);
            return;
        }

        Label lblMensaje = (Label) warningBox.getChildren().stream()
                .filter(node -> node instanceof Label)
                .findFirst()
                .orElse(null);

        if (lblMensaje != null) {
            lblMensaje.setText(mensaje);
        }

        warningBox.setVisible(true);
        warningBox.setManaged(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> {
            warningBox.setVisible(false);
            warningBox.setManaged(false);
        });
        delay.play();
    }
}