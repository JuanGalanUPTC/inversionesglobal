package co.edu.uptc.viewcontroller.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.HBox;
import co.edu.uptc.model.Asset;
import co.edu.uptc.model.Investor;
import co.edu.uptc.service.InvestorService;
import co.edu.uptc.service.AssetService;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

public class GestionarInversionistasController implements Initializable {

    @FXML private TextField txtBusqueda;
    @FXML private ComboBox<String> comboFiltroActivo;
    @FXML private TableView<Investor> tablaInversionistas;
    @FXML private TableColumn<Investor, String> colNombre;
    @FXML private TableColumn<Investor, String> colGmail;
    @FXML private TableColumn<Investor, Double> colCapital;
    @FXML private TableColumn<Investor, Void> colAcciones;

    private final InvestorService investorService = new InvestorService();
    private final AssetService assetService = new AssetService();
    private FilteredList<Investor> listaFiltrada;
    private ResourceBundle rb;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        configurarTabla();
        cargarDatos();
        configurarFiltros();
    }

    private void configurarTabla() {
        // Traducir encabezados de columna
        colNombre.setText(rb.getString("table.name"));
        colGmail.setText(rb.getString("table.email"));
        colCapital.setText(rb.getString("table.capital"));
        colAcciones.setText(rb.getString("table.actions"));

        // Extraemos un "Nombre" a partir del correo si no hay campo de nombre real en el modelo
        colNombre.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getEmail();
            String name = (email != null && email.contains("@")) ? email.split("@")[0] : "Usuario";
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        colGmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCapital.setCellValueFactory(new PropertyValueFactory<>("balance"));

        // Formatear la columna de capital como moneda local ($0,00)
        colCapital.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                setText(empty || balance == null ? null : String.format("$%,.2f", balance));
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
                
                // Estilo manual para asegurar el contraste en el tema Cyber/Dark
                btnDelete.setStyle("-fx-border-color: #EF4444; -fx-text-fill: white;");
                
                btnEdit.setOnAction(e -> {
                    Investor investor = getTableView().getItems().get(getIndex());
                    editarEmail(investor);
                });
                
                btnDelete.setOnAction(e -> {
                    Investor investor = getTableView().getItems().get(getIndex());
                    eliminarInversionista(investor);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    pane.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(pane);
                }
            }
        });
    }

    private void cargarDatos() {
        // Cargamos la lista de inversionistas y permitimos el filtrado dinámico reactivo
        listaFiltrada = new FilteredList<>(
            FXCollections.observableArrayList(investorService.listInversionists()), 
            p -> true
        );
        tablaInversionistas.setItems(listaFiltrada);
        
        // Poblar el combo de activos para filtrar por tipo de inversión
        comboFiltroActivo.getItems().add(rb.getString("filter.all_assets"));
        try {
            assetService.findAll().forEach(a -> comboFiltroActivo.getItems().add(a.getName()));
        } catch (Exception e) {
            System.out.println("Error al cargar la lista de activos: " + e.getMessage());
        }
        comboFiltroActivo.getSelectionModel().selectFirst();
    }

    private void configurarFiltros() {
        // Escuchar cambios tanto en el texto de búsqueda como en el selector de activos
        txtBusqueda.textProperty().addListener((obs, old, newValue) -> aplicarFiltros());
        comboFiltroActivo.valueProperty().addListener((obs, old, newValue) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        String busqueda = (txtBusqueda.getText() == null) ? "" : txtBusqueda.getText().toLowerCase().trim();
        String activoFiltro = comboFiltroActivo.getValue();

        listaFiltrada.setPredicate(investor -> {
            // Filtro por nombre (vía email) o email directo
            boolean matchBusqueda = investor.getEmail() != null && investor.getEmail().toLowerCase().contains(busqueda);
            
            // Filtro por tipo de activo en el que ha invertido (Busca en su lista de inversiones)
            boolean matchActivo = (activoFiltro == null || activoFiltro.equals(rb.getString("filter.all_assets"))) ||
                (investor.getInvestments() != null && investor.getInvestments().stream().anyMatch(inv -> {
                    // Necesitamos buscar el Asset por su ID para obtener el nombre
                    Asset asset = assetService.findById(inv.getAssetId());
                    return asset != null && asset.getName().equalsIgnoreCase(activoFiltro);
                }));

            return matchBusqueda && matchActivo;
        });
    }

    private void editarEmail(Investor investor) {
        TextInputDialog dialog = new TextInputDialog(investor.getEmail());
        dialog.setTitle(rb.getString("dialog.edit_user.title"));
        dialog.setHeaderText(rb.getString("dialog.edit_user.header"));
        dialog.setContentText(rb.getString("dialog.edit_user.content"));

        // Aplicar el estilo del dashboard al diálogo
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/co/edu/uptc/css/dashboard.css").toExternalForm());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newEmail -> {
            if (newEmail.contains("@") && !newEmail.trim().isEmpty()) {
                investor.setEmail(newEmail.trim());
                investorService.updateInvestor(investor);
                tablaInversionistas.refresh();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR, rb.getString("error.invalid_email"));
                error.show();
            }
        });
    }

    private void eliminarInversionista(Investor investor) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("dialog.delete_user.title"));
        alert.setHeaderText(rb.getString("dialog.delete_user.header"));
        alert.setContentText(investor.getEmail() + "\n\n" + rb.getString("dialog.delete_user.content"));

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (investorService.deleteInvestor(investor.getEmail())) {
                tablaInversionistas.getItems().remove(investor);
            }
        }
    }
}