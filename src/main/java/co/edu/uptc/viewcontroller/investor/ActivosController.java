package co.edu.uptc.viewcontroller.investor;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.service.AssetService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ActivosController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private VBox contenedorActivos;

    // Instancia de tu servicio real
    private AssetService assetService;
    private List<Asset> todosLosActivos;
    
    // Usaremos null para representar "Todos", o el Enum específico para los filtros
    private AssetType categoriaActual = null; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializamos tu servicio de persistencia JSON
        assetService = new AssetService();
        
        cargarDatosDesdeJson();
        renderizarLista(todosLosActivos);

        // Configurar el buscador en tiempo real
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltros();
        });
    }

    private void cargarDatosDesdeJson() {
        try {
            // Traemos los activos directamente de tu archivo asset.json
            todosLosActivos = assetService.listAssets();
        } catch (Exception e) {
            System.err.println("Error al cargar los activos del JSON: " + e.getMessage());
        }
    }

    private void renderizarLista(List<Asset> listaActivos) {
        // 1. Limpiar el contenedor antes de renderizar
        contenedorActivos.getChildren().clear();

        // Si la lista está vacía o es nula, evitamos errores
        if (listaActivos == null || listaActivos.isEmpty()) {
            return;
        }

        // 2. Iterar sobre la lista de activos
        for (Asset asset : listaActivos) { 
            
            HBox fila = new HBox();
            fila.setAlignment(Pos.CENTER_LEFT);
            
            // Definimos los estilos en variables para mantener el código limpio
            String estiloNormal = "-fx-padding: 15; -fx-border-color: transparent transparent #1e2330 transparent; -fx-border-width: 1; -fx-background-color: transparent;";
            String estiloHover = "-fx-padding: 15; -fx-border-color: transparent transparent #1e2330 transparent; -fx-border-width: 1; -fx-background-color: #1e2330; -fx-cursor: hand;";
            
            fila.setStyle(estiloNormal);

            // --- INTERACTIVIDAD ---
            
            // Efecto Hover (Iluminar la fila)
            fila.setOnMouseEntered(e -> fila.setStyle(estiloHover));
            fila.setOnMouseExited(e -> fila.setStyle(estiloNormal));

            // Evento Clic
            fila.setOnMouseClicked(e -> {
                System.out.println("Clic registrado en: " + asset.getName());
                // Aquí llamarás al método para abrir la vista de detalles o el modal de simulación
            });

            // --- COLUMNAS ---

            // 1. Columna: Nombre
            Label lblNombre = new Label(asset.getName());
            lblNombre.setPrefWidth(200.0);
            lblNombre.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffffff;");

            Region spacer1 = new Region();
            HBox.setHgrow(spacer1, Priority.ALWAYS);

            // 2. Columna: Precio
            Label lblPrecio = new Label(String.format("$ %,.2f", asset.getActualPrice()));
            lblPrecio.setPrefWidth(120.0);
            lblPrecio.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");

            Region spacer2 = new Region();
            HBox.setHgrow(spacer2, Priority.ALWAYS);

            // 3. Columna: Volatilidad (Tratada como double)
            double valorVolatilidad = asset.getVolatility();
            
            // Formateamos para que muestre 2 decimales y el símbolo de porcentaje
            Label lblVolatilidad = new Label(String.format("%.2f %%", valorVolatilidad));
            lblVolatilidad.setPrefWidth(100.0);
            lblVolatilidad.setAlignment(Pos.CENTER_RIGHT);
            
            // Asignar color dinámico evaluando el valor del double
            // Ajusta estos umbrales (5.0 y 15.0) según la lógica de tu aplicación
            String colorVolatilidad;
            if (valorVolatilidad < 5.0) { 
                colorVolatilidad = "#10b981"; // Verde (Baja)
            } else if (valorVolatilidad < 15.0) { 
                colorVolatilidad = "#f59e0b"; // Naranja (Media)
            } else { 
                colorVolatilidad = "#ef4444"; // Rojo (Alta)
            }
            
            lblVolatilidad.setStyle("-fx-font-size: 14px; -fx-text-fill: " + colorVolatilidad + "; -fx-font-weight: bold;");

            // --- ENSAMBLAJE ---
            
            // Agregar las columnas a la fila
            fila.getChildren().addAll(lblNombre, spacer1, lblPrecio, spacer2, lblVolatilidad);

            // Agregar la fila completada al VBox principal
            contenedorActivos.getChildren().add(fila);
        }
    }

    // --- MÉTODOS DE FILTRADO ---

    @FXML 
    private void filtrarTodos() { 
        categoriaActual = null; 
        aplicarFiltros(); 
    }
    
    @FXML 
    private void filtrarCrypto() { 
        categoriaActual = AssetType.valueOf("CRYPTO"); 
        aplicarFiltros(); 
    }
    
    @FXML 
    private void filtrarAcciones() { 
        categoriaActual = AssetType.valueOf("STOCK"); // Cambia "STOCK" si tu Enum tiene otro nombre
        aplicarFiltros(); 
    }
    
    @FXML 
    private void filtrarForex() { 
        categoriaActual = AssetType.valueOf("FOREX"); 
        aplicarFiltros(); 
    }

    private void aplicarFiltros() {
        String busqueda = txtBuscar.getText().toLowerCase().trim();

        List<Asset> listaFiltrada = todosLosActivos.stream()
                // 1. Filtramos por Enum (si categoriaActual es null, muestra todos)
                .filter(a -> categoriaActual == null || a.getAssetType() == categoriaActual)
                // 2. Filtramos por nombre ingresado en el buscador
                .filter(a -> a.getName().toLowerCase().contains(busqueda))
                .collect(Collectors.toList());

        renderizarLista(listaFiltrada);
    }
}