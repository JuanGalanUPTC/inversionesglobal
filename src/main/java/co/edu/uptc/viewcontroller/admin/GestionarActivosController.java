package co.edu.uptc.viewcontroller.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import co.edu.uptc.model.Asset;
import co.edu.uptc.service.AssetService;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

public class GestionarActivosController implements Initializable {

    @FXML private TextField txtBusqueda;
    @FXML private TableView<Asset> tablaActivos;
    @FXML private TableColumn<Asset, String> colNombre;
    @FXML private TableColumn<Asset, Double> colPrecio;
    @FXML private TableColumn<Asset, Double> colVolatilidad;
    @FXML private TableColumn<Asset, Void> colAcciones;

    private final AssetService assetService = new AssetService();
    private FilteredList<Asset> listaFiltrada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatos();
        
        txtBusqueda.textProperty().addListener((obs, old, newValue) -> {
            listaFiltrada.setPredicate(asset -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return asset.getName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("actualPrice"));
        colVolatilidad.setCellValueFactory(new PropertyValueFactory<>("volatility"));

        // Formateo de precio ($)
        colPrecio.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : String.format("$%,.2f", price));
            }
        });

        // Formateo de volatilidad (%)
        colVolatilidad.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double vol, boolean empty) {
                super.updateItem(vol, empty);
                setText(empty || vol == null ? null : String.format("%.1f%%", vol * 100));
            }
        });

        configurarColumnaAcciones();
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("btn-customize");
                btnDelete.getStyleClass().add("btn-logout");
                btnEdit.setOnAction(e -> editarActivo(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> eliminarActivo(getTableView().getItems().get(getIndex())));
                pane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void cargarDatos() {
        listaFiltrada = new FilteredList<>(FXCollections.observableArrayList(assetService.findAll()), p -> true);
        tablaActivos.setItems(listaFiltrada);
    }

    private void editarActivo(Asset asset) {
        // Diálogo para editar Precio
        TextInputDialog priceDialog = new TextInputDialog(String.valueOf(asset.getActualPrice()));
        priceDialog.setTitle("Editar Activo");
        priceDialog.setHeaderText("Actualizar datos de " + asset.getName());
        priceDialog.setContentText("Nuevo Precio (USD):");
        
        Optional<String> priceResult = priceDialog.showAndWait();
        priceResult.ifPresent(newPrice -> {
            try {
                double price = Double.parseDouble(newPrice);
                
                // Diálogo para editar Volatilidad
                TextInputDialog volDialog = new TextInputDialog(String.valueOf(asset.getVolatility()));
                volDialog.setHeaderText("Ajustar Volatilidad (0.0 a 1.0)");
                volDialog.setContentText("Nueva Volatilidad:");
                
                Optional<String> volResult = volDialog.showAndWait();
                volResult.ifPresent(newVol -> {
                    double vol = Double.parseDouble(newVol);
                    asset.setActualPrice(price);
                    asset.setVolatility(vol);
                    assetService.updateAsset(asset);
                    tablaActivos.refresh();
                });
            } catch (NumberFormatException e) {
                mostrarError("Valor numérico inválido.");
            }
        });
    }

    private void eliminarActivo(Asset asset) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Activo");
        alert.setHeaderText("¿Estás seguro de eliminar " + asset.getName() + "?");
        alert.setContentText("Esta acción afectará a todos los inversionistas que posean este activo.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            assetService.deleteAsset(asset.getId());
            tablaActivos.getItems().remove(asset);
        }
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.show();
    }
}