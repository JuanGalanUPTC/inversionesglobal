package co.edu.uptc.viewcontroller.investor;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import co.edu.uptc.app.App;
import co.edu.uptc.model.User;

public class DashboardController implements Initializable {

    // Inyectamos el contenedor principal
    @FXML
    private VBox warningBox;
    @FXML
    private ComboBox<String> idiomaComboBox;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label nombreLabel;

    @FXML
    private Label rolLabel;
    @FXML
    private ImageView avatarImageView;

    @FXML
    private void mostrarMisInversiones() {
        cambiarCentro("/co/edu/uptc/view/investor/misInversiones.fxml");
    }

    @FXML
    private void mostrarReportes() {
        cambiarCentro("/co/edu/uptc/view/investor/reportes.fxml");
    }

    @FXML
    private void mostrarActivos() {
        cambiarCentro("/co/edu/uptc/view/investor/activosView.fxml");
    }

    @FXML
    public void handleCerrarSesion(ActionEvent event) {
        // 1. Crear la alerta de confirmación
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Cerrar Sesión");
        alerta.setHeaderText("¿Estás seguro de que deseas salir?");
        alerta.setContentText("Cualquier cambio no guardado en la sesión actual podría perderse.");

        // Personalizar los botones en español
        ButtonType botonSi = new ButtonType("Sí, salir");
        ButtonType botonNo = new ButtonType("Cancelar");
        alerta.getButtonTypes().setAll(botonSi, botonNo);

        // 2. Mostrar la alerta en pantalla y esperar la respuesta del usuario
        Optional<ButtonType> resultado = alerta.showAndWait();

        // 3. Si el usuario hace clic en "Sí, salir", procedemos con el cierre
        if (resultado.isPresent() && resultado.get() == botonSi) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uptc/view/auth/login.fxml"));
                Parent loginRoot = loader.load();

                // Obtener la ventana actual y guardar su estado
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                boolean estabaMaximizada = stage.isMaximized();

                Scene loginScene = new Scene(loginRoot);
                stage.setScene(loginScene);

                // 4. Restauramos el tamaño (CON LA CORRECCIÓN APLICADA)
                if (estabaMaximizada) {
                    stage.setMaximized(false); // 1. Apagamos un instante
                    stage.setMaximized(true); // 2. Encendemos para forzar pantalla completa
                } else {
                    stage.centerOnScreen();
                }
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método genérico encargado de limpiar el centro del BorderPane
     * y cargar el nuevo FXML de forma limpia.
     */
    private void cambiarCentro(String rutaFxml) {
        try {
            // 1. Cargamos el archivo FXML secundario
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent nuevaVista = loader.load();

            // 2. Reemplazamos el nodo central del BorderPane con la nueva vista
            mainBorderPane.setCenter(nuevaVista);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la sub-vista: " + rutaFxml);
        }
    }

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

        User usuarioLogueado = App.getUsuarioLogueado();

        if (usuarioLogueado.getProfileImagePath() != null) {
            try {
                String rutaImagen = usuarioLogueado.getProfileImagePath();
                Image avatar;

                if (rutaImagen.startsWith("/")) {
                    // 📦 Caso A: Es la foto por defecto
                    java.io.InputStream stream = getClass().getResourceAsStream(rutaImagen);
                    if (stream == null) {
                        throw new java.io.FileNotFoundException("No se encontró el recurso interno: " + rutaImagen);
                    }
                    avatar = new Image(stream);
                } else {
                    // 📁 Caso B: Es una imagen personalizada en el disco
                    java.io.File file = new java.io.File(rutaImagen);
                    if (file.exists()) {
                        avatar = new Image(file.toURI().toString());
                    } else {
                        avatar = new Image(getClass().getResourceAsStream("/co/edu/uptc/images/userIconDefault.jpg"));
                    }
                }

                avatarImageView.setImage(avatar);

                // 🎯 ¡AQUÍ ESTÁ EL TRUCO DEL CÍRCULO!
                // 1. Forzamos un tamaño fijo al ImageView si no lo tiene en el FXML (ej: 80x80)
                double ancho = 80;
                double alto = 80;
                avatarImageView.setFitWidth(ancho);
                avatarImageView.setFitHeight(alto);
                avatarImageView.setPreserveRatio(false); // Para que se adapte obligatoriamente al círculo

                // 2. Creamos una máscara geométrica circular posicionada en el centro del
                // ImageView
                javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle();
                clip.setCenterX(ancho / 2); // Centro X (40)
                clip.setCenterY(alto / 2); // Centro Y (40)
                clip.setRadius(ancho / 2); // Radio del círculo (40)

                // 3. Le aplicamos la máscara al visor de imágenes
                avatarImageView.setClip(clip);

            } catch (Exception e) {
                System.err.println("❌ Error al renderizar el avatar: " + e.getMessage());
                avatarImageView
                        .setImage(new Image(getClass().getResourceAsStream("/co/edu/uptc/images/userIconDefault.jpg")));
            }
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
}