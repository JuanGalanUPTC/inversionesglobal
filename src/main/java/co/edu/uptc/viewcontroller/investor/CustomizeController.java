package co.edu.uptc.viewcontroller.investor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import co.edu.uptc.app.App;
import co.edu.uptc.model.User;
import co.edu.uptc.model.Investor;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.service.InvestorService;
import co.edu.uptc.service.UserService;

public class CustomizeController {

    UserService userService=new UserService();
    @FXML
    private Button btnGuardarUsername;
    @FXML
    private Button btnGuardarRiesgo;
    @FXML
    private ImageView imgPerfilPersonalizacion;
    @FXML
    private TextField txtUsername;
    @FXML
    private Button btnEditarUsername;
    @FXML
    private Button btnGuardarFoto;
    @FXML
    private Button btnCargarFoto;
    @FXML
    private ToggleGroup grupoPerfilRiesgo;
    @FXML
    private RadioButton radioConservador;
    @FXML
    private RadioButton radioModerado;
    @FXML
    private RadioButton radioAgresivo;

    private boolean editandoUsername = false;
    private String rutaImagenTemporal = null;

    @FXML
    public void initialize() {
        User usuarioLogueado = App.getUsuarioLogueado();
        if (usuarioLogueado != null) {

            // 1. CARGAR NOMBRE REAL DEL INVERSIONISTA (Buscándolo por su email en el JSON)
            InvestorService investorService = new InvestorService();
            Investor inversionista = investorService.findByEmail(usuarioLogueado.getEmail());

            if (inversionista != null && inversionista.getName() != null) {
                txtUsername.setText(inversionista.getName());
            } else {
                txtUsername.setText(usuarioLogueado.getEmail()); // Fallback por si no tiene entidad física aún
            }

            // 2. Cargar la foto de perfil actual con máscara circular
            cargarFotoPerfil(usuarioLogueado.getProfileImagePath());

            // 3. CARGAR EL PERFIL DE RIESGO ACTUAL EN LOS RADIO BUTTONS
            if (inversionista != null && inversionista.getRiskProfile() != null) {
                switch (inversionista.getRiskProfile()) {
                    case CONSERVATIVE -> radioConservador.setSelected(true);
                    case MODERATE -> radioModerado.setSelected(true);
                    case AGGRESSIVE -> radioAgresivo.setSelected(true);
                }
            }
        }
    }

    private void cargarFotoPerfil(String path) {
        try {
            Image avatar;
            if (path != null && path.startsWith("/")) {
                avatar = new Image(getClass().getResourceAsStream(path));
            } else if (path != null && new File(path).exists()) {
                avatar = new Image(new File(path).toURI().toString());
            } else {
                avatar = new Image(getClass().getResourceAsStream("/co/edu/uptc/images/userIconDefault.jpg"));
            }

            imgPerfilPersonalizacion.setImage(avatar);

            double ancho = 140;
            double alto = 140;
            imgPerfilPersonalizacion.setFitWidth(ancho);
            imgPerfilPersonalizacion.setFitHeight(alto);
            imgPerfilPersonalizacion.setPreserveRatio(false);

            Circle clip = new Circle(ancho / 2, alto / 2, ancho / 2);
            imgPerfilPersonalizacion.setClip(clip);
        } catch (Exception e) {
            System.err.println("Error al cargar foto en personalización: " + e.getMessage());
        }
    }

    @FXML
    private void handleCargarFoto() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) imgPerfilPersonalizacion.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            rutaImagenTemporal = file.getAbsolutePath();
            cargarFotoPerfil(rutaImagenTemporal);
            System.out.println("📸 Imagen seleccionada temporalmente: " + rutaImagenTemporal);
        }
    }

    @FXML
    private void handleEditarUsername() {
        if (!editandoUsername) {
            txtUsername.setEditable(true);
            txtUsername.requestFocus();
            txtUsername.selectAll();
            btnEditarUsername.setText("Bloquear");
            editandoUsername = true;
        } else {
            txtUsername.setEditable(false);
            btnEditarUsername.setText("Editar");
            editandoUsername = false;
        }
    }

    @FXML
    private void handleGuardarFoto() {
        if (rutaImagenTemporal == null) {
            mostrarAlerta("Información", "No has seleccionado ninguna imagen nueva para guardar.");
            return;
        }

        User usuarioLogueado = App.getUsuarioLogueado();
        if (usuarioLogueado != null) {
            // 🎯 PASO CRÍTICO: Guardar la ruta temporal dentro del objeto del usuario en
            // sesión
            usuarioLogueado.setProfileImagePath(rutaImagenTemporal);

            // Opcional: Aquí llamas al servicio que guarde la sesión del User en su archivo
            // json
            userService.updateUserInPersistence(usuarioLogueado);

            System.out.println("✅ Foto de perfil guardada con éxito: " + rutaImagenTemporal);
            mostrarAlerta("Éxito", "Foto de perfil actualizada correctamente.");

            // 🔄 Forzar refresco visual del panel lateral izquierdo del Dashboard
            if (DashboardController.getInstancia() != null) {
                DashboardController.getInstancia().initialize(null, null);
            }
        }
    }

    @FXML
    private void handleGuardarUsername() {
        String nuevoNombre = txtUsername.getText();
        User usuarioLogueado = App.getUsuarioLogueado();

        if (usuarioLogueado != null && !nuevoNombre.trim().isEmpty()) {
            InvestorService investorService = new InvestorService();
            Investor inversionista = investorService.findByEmail(usuarioLogueado.getEmail());

            if (inversionista != null) {
                // Actualizar el nombre en el modelo de persistencia financiero
                inversionista.setName(nuevoNombre);
                investorService.updateInvestor(inversionista);// Guarda los cambios en tu JSON

                System.out.println("✅ Nombre del inversionista guardado: " + nuevoNombre);
                mostrarAlerta("Éxito", "Nombre del inversionista actualizado correctamente.");

                txtUsername.setEditable(false);
                btnEditarUsername.setText("Editar");
                editandoUsername = false;

                // 🔄 Forzar refresco visual de la barra lateral (Cambiará el label del nombre)
                if (DashboardController.getInstancia() != null) {
                    DashboardController.getInstancia().initialize(null, null);
                }
            }
        }
    }

    @FXML
    private void handleGuardarRiesgo() {
        User usuarioLogueado = App.getUsuarioLogueado();
        RadioButton seleccionado = (RadioButton) grupoPerfilRiesgo.getSelectedToggle();

        if (usuarioLogueado != null && seleccionado != null) {
            InvestorService investorService = new InvestorService();
            Investor inversionista = investorService.findByEmail(usuarioLogueado.getEmail());

            if (inversionista != null) {
                // 1. Obtener el texto del RadioButton seleccionado en mayúsculas (ej:
                // "MODERADO")
                String riesgoTexto = seleccionado.getText().toUpperCase();

                // 2. Mapeo seguro: Traducimos de Español (UI) a Inglés (Enum)
                RiskProfile perfilReal;
                if (riesgoTexto.contains("MODERA")) {
                    perfilReal = RiskProfile.MODERATE;
                } else if (riesgoTexto.contains("AGRES")) {
                    perfilReal = RiskProfile.AGGRESSIVE;
                } else {
                    perfilReal = RiskProfile.CONSERVATIVE;
                }

                // 3. Asignar el perfil correcto ya traducido
                inversionista.setRiskProfile(perfilReal);

                // Persistencia de datos en la capa de servicios
                investorService.updateInvestor(inversionista);

                System.out.println("✅ Perfil de riesgo guardado en JSON: " + perfilReal.name());
                mostrarAlerta("Éxito", "Perfil de riesgo actualizado correctamente.");

                // 🔄 Forzar refresco visual del Dashboard para cambiar el label inferior
                if (DashboardController.getInstancia() != null) {
                    DashboardController.getInstancia().initialize(null, null);
                }
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}