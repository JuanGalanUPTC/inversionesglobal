package co.edu.uptc.viewcontroller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uptc.app.App;

public class LoginController implements Initializable {

    @FXML
    private ComboBox<String> idiomaComboBox;

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;

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
        App.setRoot("register");
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
}
