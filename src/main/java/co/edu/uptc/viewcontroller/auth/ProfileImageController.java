package co.edu.uptc.viewcontroller.auth;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

import co.edu.uptc.app.App;
import co.edu.uptc.model.enums.UserRole;
import co.edu.uptc.service.UserService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

// 💡 Añadimos Initializable para preparar el combo de idiomas y la ruta por defecto
public class ProfileImageController implements Initializable {

    // 🚀 Instanciamos el servicio para poder guardar en el JSON
    private final UserService userService = new UserService();

    @FXML
    private ImageView imgPreview;
    @FXML
    private ComboBox<String> idiomaComboBox;
    @FXML
    Button buttonSeleccionarFotoDePerfil;
    @FXML
    private VBox warningBox;
    @FXML
    private Label warningMessage;

    // 📁 Ruta base por defecto (si el usuario decide omitir)
    private final String PATH_DEFAULT = "/co/edu/uptc/images/userIconDefault.jpg";
    private String imagePath = PATH_DEFAULT;

    // 📥 Datos temporales del formulario anterior
    private String temporalEmail;
    private String temporalPassword;
    private String temporalCiudad;

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

    /**
     * 📥 Recibe de forma segura los datos recolectados en el RegisterController
     */
    public void cargarDatosTemporales(String email, String password, String ciudad) {
        this.temporalEmail = email;
        this.temporalPassword = password;
        this.temporalCiudad = ciudad;
    }

    @FXML
    public void selectImage() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(buttonSeleccionarFotoDePerfil.getScene().getWindow());

        if (file != null) {
            imagePath = copyImage(file);
            Image image = new Image(file.toURI().toString());
            imgPreview.setImage(image);
        }
    }

    private String copyImage(File file) throws IOException {
        Path destinationFolder = Paths.get("images");
        if (!Files.exists(destinationFolder)) {
            Files.createDirectories(destinationFolder);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getName();
        Path destination = destinationFolder.resolve(fileName);

        Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }

    /**
     * 🛑 CASO A: El usuario decide Omitir. Forzamos la foto por defecto.
     */
    @FXML
    public void handleOfOmitir() throws IOException {
        completarRegistro(PATH_DEFAULT);
    }

    /**
     * 💾 CASO B: El usuario confirma. Usamos la imagen que cargó en 'imagePath'.
     * Recuerda enlazar este método a tu botón "Finalizar Registro" en el FXML.
     */
    @FXML
    public void handleFinalizar() throws IOException {
        completarRegistro(imagePath);
    }

    private void completarRegistro(String rutaFinalImagen) throws IOException {
        try {
            // 🚀 Guardamos definitivamente en el JSON
            userService.registerUser(temporalEmail, temporalPassword, temporalCiudad, UserRole.INVESTOR,
                    rutaFinalImagen);

            System.out.println("✅ Registro finalizado con éxito para: " + temporalEmail);

            // Saltamos a la pantalla de éxito
            App.setRoot("auth/register_succes");

        } catch (Exception e) {
            mostrarAlerta("No se pudo completar el registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ... tus métodos mostrarAlerta, createCustomCell y backToLogin se quedan
    // exactamente igual ...

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
        App.setRoot("auth/login");
    }
}
