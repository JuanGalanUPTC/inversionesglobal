package co.edu.uptc.viewcontroller.investor;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MisInversionesController {

    // 1. Inyectar los componentes con el mismo fx:id definido en MisInversiones.fxml
    @FXML
    private Label saldoLabel;

    @FXML
    private Label rendimientoLabel;

    @FXML
    private Button btnNuevaInversion;

    @FXML
    private VBox listaInversionesContainer; // Contenedor dinámico por si quieres meter filas con código

    /**
     * El método initialize() se ejecuta automáticamente al cargar el FXML.
     * Aquí preparamos los datos iniciales de las tarjetas del inversionista.
     */
    @FXML
    public void initialize() {
        System.out.println("Cargando la sección de Mis Inversiones...");
        
        // Configurar datos simulados (en un entorno real vendrían de tu base de datos o JSON)
        cargarDatosFinancieros();
    }

    /**
     * Simula la carga de dinero y ganancias en las tarjetas de la interfaz
     */
   private void cargarDatosFinancieros() {
        double saldoDisponible = 20000.00; // Coincide con tu diseño estático

        // 2. Solo actualizamos el saldo que SÍ existe en tu FXML
        if (saldoLabel != null) {
            saldoLabel.setText(String.format("$%,.2f", saldoDisponible));
        }
    }

    /**
     * Acción vinculada al botón principal "Nueva Inversión" en el FXML
     */
    @FXML
    private void manejarNuevaInversion() {
        System.out.println("¡El usuario quiere realizar una nueva inversión!");
        
        // Aquí podrías abrir un modal emergente (Dialog) o redirigir al 
        // listado de Activos para que elija en qué invertir.
        
        /* Ejemplo de alerta rápida:
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nueva Inversión");
        alert.setHeaderText(null);
        alert.setContentText("Cargando catálogo de activos disponibles...");
        alert.showAndWait();
        */
    }
}