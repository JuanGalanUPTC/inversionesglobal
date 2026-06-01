package co.edu.uptc.viewcontroller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import co.edu.uptc.app.App;
import co.edu.uptc.model.User;

public class AdminDashboardController implements Initializable {

    @FXML private BorderPane mainBorderPane;
    @FXML private StackPane contentArea;
    @FXML private ImageView avatarImageView;
    @FXML private Label nombreLabel;
    @FXML private Label rolLabel;
    @FXML private ComboBox<String> idiomaComboBox;
    @FXML private Button btnCerrarSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configurar ComboBox de idiomas (Lógica interna)
        idiomaComboBox.getItems().addAll("es", "en");
        idiomaComboBox.setCellFactory(param -> createLanguageCell());
        idiomaComboBox.setButtonCell(createLanguageCell());
        idiomaComboBox.getSelectionModel().selectFirst();

        // 2. Cargar datos del usuario logueado
        User admin = App.getUsuarioLogueado();
        if (admin != null) {
            nombreLabel.setText(admin.getEmail().split("@")[0]); // O el nombre si estuviera disponible
            rolLabel.setText("Administrador");
            configurarAvatar(admin.getProfileImagePath());
        }
    }

    private void configurarAvatar(String path) {
        try {
            Image image;
            if (path != null && path.startsWith("/")) {
                image = new Image(getClass().getResourceAsStream(path));
            } else if (path != null) {
                java.io.File file = new java.io.File(path);
                image = file.exists() ? new Image(file.toURI().toString()) : getDefaultImage();
            } else {
                image = getDefaultImage();
            }
            avatarImageView.setImage(image);

            // Aplicar recorte circular
            double size = 80; 
            avatarImageView.setFitWidth(size);
            avatarImageView.setFitHeight(size);
            Circle clip = new Circle(size / 2, size / 2, size / 2);
            avatarImageView.setClip(clip);
        } catch (Exception e) {
            avatarImageView.setImage(getDefaultImage());
        }
    }

    private Image getDefaultImage() {
        return new Image(getClass().getResourceAsStream("/co/edu/uptc/images/userIconDefault.jpg"));
    }

    private ListCell<String> createLanguageCell() {
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
                    ImageView flag = new ImageView();
                    flag.setFitWidth(20);
                    flag.setPreserveRatio(true);
                    
                    String flagPath = item.equals("es") ? "/co/edu/uptc/images/colflag.png" : "/co/edu/uptc/images/usaflag.jpg";
                    flag.setImage(new Image(getClass().getResourceAsStream(flagPath)));
                    
                    Label name = new Label(item.equals("es") ? "Español" : "English");
                    name.setStyle("-fx-text-fill: white;");
                    
                    container.getChildren().addAll(flag, name);
                    setGraphic(container);
                }
            }
        };
    }

    @FXML
    private void mostrarGestionarInversionistas() {
        cambiarCentro("/co/edu/uptc/view/admin/gestionar_inversionistas.fxml");
    }

    @FXML
    private void mostrarReportes() {
        System.out.println("Cargando Reportes...");
    }

    @FXML
    private void mostrarGestionarActivos() {
        System.out.println("Cargando Gestión de Activos...");
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText("¿Seguro que desea salir?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            App.setRoot("auth/login");
        }
    }

    private void cambiarCentro(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent node = loader.load();
            mainBorderPane.setCenter(node);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista central: " + e.getMessage());
        }
    }
}