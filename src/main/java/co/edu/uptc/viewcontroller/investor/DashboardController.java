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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import co.edu.uptc.app.App;
import co.edu.uptc.model.Investor;
import co.edu.uptc.model.User;
import co.edu.uptc.service.InvestorService;

public class DashboardController implements Initializable {
    private static DashboardController instanciaGlobal;

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
    private Label perfilRiesgoLabel;
    @FXML
    private Label rolLabel;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private StackPane contentArea;

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
        cambiarCentro("/co/edu/uptc/view/investor/activos.fxml");
    }

    @FXML
    public void handleCustomizeButton() throws IOException {
        cambiarCentro("/co/edu/uptc/view/investor/customize.fxml");
    }

    public static DashboardController getInstancia() {
        return instanciaGlobal;
    }

    @FXML
    public void handleCerrarSesion(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Cerrar Sesión");
        alerta.setHeaderText("¿Estás seguro de que deseas salir?");
        alerta.setContentText("Cualquier cambio no guardado en la sesión actual podría perderse.");

        ButtonType botonSi = new ButtonType("Sí, salir");
        ButtonType botonNo = new ButtonType("Cancelar");
        alerta.getButtonTypes().setAll(botonSi, botonNo);

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == botonSi) {
            try {
                // 🎯 PASO 1: Limpiar el usuario de la sesión global
                App.setUsuarioLogueado(null);

                // 🎯 PASO 2: ROMPER EL SINGLETON (Limpiar la instancia vieja de la memoria)
                // Esto le avisa a JavaFX que el panel viejo ya no existe y debe crear uno nuevo
                // al re-entrar
                instanciaGlobal = null;

                // PASO 3: Redireccionar al Login de forma normal usando tu método setRoot
                App.setRoot("auth/login");

            } catch (IOException e) {
                System.err.println("❌ Error al redireccionar al login tras cerrar sesión:");
                e.printStackTrace();
            }
        }
    }

    private void cambiarCentro(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent nuevaVista = loader.load();
            mainBorderPane.setCenter(nuevaVista);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la sub-vista: " + rutaFxml);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instanciaGlobal = this;

        // 1. Configurar ComboBox de idiomas
        if (idiomaComboBox.getItems().isEmpty()) {
            idiomaComboBox.getItems().addAll("es", "en");
        }
        idiomaComboBox.setCellFactory(param -> createCustomCell());
        idiomaComboBox.setButtonCell(createCustomCell());
        idiomaComboBox.getSelectionModel().selectFirst();

        if (warningBox != null) {
            warningBox.setVisible(false);
            warningBox.setManaged(false);
        }

        // 2. OBTENER USUARIO Y CONFIGURAR LA BARRA LATERAL CON LOS DATOS DEL
        // INVERSIONISTA
        User usuarioLogueado = App.getUsuarioLogueado();

        if (usuarioLogueado != null) {
            if (rolLabel != null) {
                rolLabel.setText("Inversionista");
            }

            String emailLimpio = usuarioLogueado.getEmail().trim().toLowerCase();
            InvestorService investorService = new InvestorService();
            Investor inversionista = investorService.findByEmail(emailLimpio);

            // 🚀 CREACIÓN CONTROLADA Y AJUSTADA PARA EVITAR DUPLICACIONES AL VOLVER A
            // INICIAR SESIÓN
            if (inversionista == null) {
                System.out.println(
                        "⚠️ El inversionista no existía en el JSON financiero. Registrando a través del servicio...");
                try {
                    // Usamos el método de creación oficial de tu lógica de negocio
                    investorService.createInvestor(
                            "Ashe",
                            emailLimpio,
                            0.0,
                            co.edu.uptc.model.enums.RiskProfile.CONSERVATIVE);

                    // Volvemos a solicitar el inversionista para que ahora sí cargue el objeto
                    // persistido
                    inversionista = investorService.findByEmail(emailLimpio);
                } catch (Exception e) {
                    System.out.println("ℹ️ Nota: El registro financiero ya existía físicamente. Recuperando datos...");
                    inversionista = investorService.findByEmail(emailLimpio);
                }
            }

            // 🎯 2. ASIGNAR EL NOMBRE REAL Y AJUSTAR EL TAMAÑO DINÁMICAMENTE
            if (inversionista != null && nombreLabel != null && inversionista.getName() != null) {
                String nombreReal = inversionista.getName();
                nombreLabel.setText(nombreReal);

                if (nombreReal.length() > 20) {
                    nombreLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
                } else if (nombreReal.length() > 14) {
                    nombreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
                } else {
                    nombreLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
                }
            }

            // 🎯 3. Asignar el perfil de riesgo de forma segura
            if (inversionista != null && perfilRiesgoLabel != null) {
                if (inversionista.getRiskProfile() != null) {
                    perfilRiesgoLabel.setText(inversionista.getRiskProfile().name());
                } else {
                    perfilRiesgoLabel.setText("Sin asignar");
                }
            }

            // 4. Renderizar la imagen del avatar de manera segura (Soporta resources
            // internos y URIs externas)
            if (usuarioLogueado.getProfileImagePath() != null && avatarImageView != null) {
                try {
                    String rutaImagen = usuarioLogueado.getProfileImagePath();
                    Image avatar;

                    if (rutaImagen.startsWith("/")) {
                        java.io.InputStream stream = getClass().getResourceAsStream(rutaImagen);
                        if (stream == null) {
                            throw new java.io.FileNotFoundException("No se encontró recurso: " + rutaImagen);
                        }
                        avatar = new Image(stream);
                    } else if (rutaImagen.startsWith("file:") || rutaImagen.startsWith("http")) {
                        // 🎯 MEJORA CRÍTICA: Si ya viene formateado como URI desde la persistencia, se
                        // carga directo
                        avatar = new Image(rutaImagen);
                    } else {
                        java.io.File file = new java.io.File(rutaImagen);
                        if (file.exists()) {
                            avatar = new Image(file.toURI().toString());
                        } else {
                            avatar = new Image(
                                    getClass().getResourceAsStream("/co/edu/uptc/images/userIconDefault.jpg"));
                        }
                    }

                    avatarImageView.setImage(avatar);

                    double ancho = 80;
                    double alto = 80;
                    avatarImageView.setFitWidth(ancho);
                    avatarImageView.setFitHeight(alto);
                    avatarImageView.setPreserveRatio(false);

                    javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(ancho / 2, alto / 2, ancho / 2);
                    avatarImageView.setClip(clip);

                } catch (Exception e) {
                    System.err.println("❌ Error al renderizar el avatar: " + e.getMessage());
                    avatarImageView.setImage(
                            new Image(getClass().getResourceAsStream("/co/edu/uptc/images/userIconDefault.jpg")));
                }
            }
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

    public void setVistaCentral(Parent nodoVista) {
        if (mainBorderPane != null) {
            mainBorderPane.setCenter(nodoVista);
        } else {
            System.err.println("❌ Error: mainBorderPane no está inyectado correctamente desde el FXML.");
        }
    }
}