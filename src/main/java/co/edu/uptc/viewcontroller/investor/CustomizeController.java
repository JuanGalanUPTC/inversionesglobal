package co.edu.uptc.viewcontroller.investor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import co.edu.uptc.app.App;
import co.edu.uptc.model.User;

public class CustomizeController {

    @FXML private ImageView imgPerfilPersonalizacion;
    @FXML private TextField txtUsername;
    @FXML private Button btnEditarUsername;
    @FXML private Button btnGuardarCambios;

    @FXML private ToggleGroup grupoPerfilRiesgo;
    @FXML private RadioButton radioConservador;
    @FXML private RadioButton radioModerado;
    @FXML private RadioButton radioAgresivo;

    private boolean editandoUsername = false;
    private String rutaImagenTemporal = null;

    @FXML
    public void initialize() {
        User usuarioLogueado = App.getUsuarioLogueado();
        if (usuarioLogueado != null) {
            // 1. Cargar el nombre de usuario actual
            txtUsername.setText(usuarioLogueado.getEmail()); // Ajusta a .getUsername() si aplica

            // 2. Cargar la foto de perfil actual con máscara circular
            cargarFotoPerfil(usuarioLogueado.getProfileImagePath());

            // 3. Opcional: Si tu usuario ya guarda el perfil de riesgo, selecciónalo aquí
            // Ejemplo:
            // if ("CONSERVADOR".equals(usuarioLogueado.getPerfilRiesgo())) radioConservador.setSelected(true);
        }
    }

    /**
     * Procesa la carga y renderizado de la imagen circular
     */
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
            
            // Aplicar máscara circular idéntica al Dashboard
            double ancho = 140; // Ajustado al tamaño de tu FXML
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

    /**
     * Selecciona una nueva imagen desde el computador usando FileChooser
     */
    @FXML
    private void handleCargarFoto() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Perfil");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) imgPerfilPersonalizacion.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            // Guardamos la ruta absoluta elegida por el usuario
            rutaImagenTemporal = file.getAbsolutePath();
            // La previsualizamos inmediatamente en la pantalla con el círculo
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
    private void handleGuardarCambios() {
        String nuevoUsername = txtUsername.getText().trim();

        if (nuevoUsername.isEmpty()) {
            System.err.println("❌ El nombre de usuario no puede estar vacío.");
            return;
        }

        RadioButton radioSeleccionado = (RadioButton) grupoPerfilRiesgo.getSelectedToggle();
        String perfilRiesgoSeleccionado = (radioSeleccionado != null) ? radioSeleccionado.getText() : "NO SELECCIONADO";

        User usuarioLogueado = App.getUsuarioLogueado();
        if (usuarioLogueado != null) {
            
            // Guardar datos de texto
            usuarioLogueado.setEmail(nuevoUsername); 
            
            // Guardar foto de perfil si se cambió una
            if (rutaImagenTemporal != null) {
                usuarioLogueado.setProfileImagePath(rutaImagenTemporal);
            }

            System.out.println("💾 Guardando datos en el usuario activo...");
            
            // 🚀 PERSISTENCIA ACTUAL: Aquí invocas tu persistencia JSON o de texto
            // Ejemplo: ArchivoJsonUtil.guardarUsuarios(App.getListaUsuarios());

            // Dejar la interfaz limpia
            txtUsername.setEditable(false);
            btnEditarUsername.setText("Editar");
            editandoUsername = false;
            
            // 🔄 TRUCO DE REFRESCO: Sincroniza la foto del menú lateral del Dashboard al instante
            if (DashboardController.getInstancia() != null) {
                // Forzamos al Dashboard a recargar el initialize para actualizar el avatar lateral
                DashboardController.getInstancia().initialize(null, null);
            }

            System.out.println("✅ ¡Cambios guardados y reflejados en el Dashboard!");
        } else {
            System.err.println("❌ Error: No se encontró una sesión de usuario activa.");
        }
    }
}