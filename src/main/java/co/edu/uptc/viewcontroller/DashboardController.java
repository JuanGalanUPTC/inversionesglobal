package co.edu.uptc.viewcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class DashboardController {

    // Inyectamos el contenedor principal
    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private void mostrarMisInversiones() {
        cambiarCentro("/co/edu/uptc/view/misInversiones.fxml");
    }

    @FXML
    private void mostrarReportes() {
        cambiarCentro("/co/edu/uptc/view/reportes.fxml");
    }

    @FXML
    private void mostrarActivos() {
        cambiarCentro("/co/edu/uptc/view/activosView.fxml"); // El buscador tipo Google que hicimos antes
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
}