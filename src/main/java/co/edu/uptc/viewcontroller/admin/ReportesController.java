package co.edu.uptc.viewcontroller.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.Investment;
import co.edu.uptc.model.Investor;
import co.edu.uptc.service.AssetService;
import co.edu.uptc.service.InvestmentService;
import co.edu.uptc.service.InvestorService;
import co.edu.uptc.service.PortfolioService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ReportesController implements Initializable {

    // Componentes FXML para Reporte Global
    @FXML private DatePicker dpFechaInicioGlobal;
    @FXML private DatePicker dpFechaFinGlobal;
    @FXML private Label lblGananciaGlobal;

    // Componentes FXML para Reporte por Inversionista
    @FXML private TextField txtIdInversionista;
    @FXML private DatePicker dpFechaInicioInversionista;
    @FXML private DatePicker dpFechaFinInversionista;
    @FXML private Label lblGananciaInversionista;

    // Componentes FXML para Top 5 Inversionistas
    @FXML private TableView<InvestorReportData> tablaTopInversionistas;
    @FXML private TableColumn<InvestorReportData, String> colTopNombre;
    @FXML private TableColumn<InvestorReportData, String> colTopRendimiento;

    // Servicios
    private final AssetService assetService = new AssetService();
    private final InvestorService investorService = new InvestorService();
    private final InvestmentService investmentService = new InvestmentService(assetService, investorService);
    private final PortfolioService portfolioService = new PortfolioService(investmentService, assetService, investorService);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar DatePickers con fechas por defecto (ej. último mes)
        dpFechaFinGlobal.setValue(LocalDate.now());
        dpFechaInicioGlobal.setValue(LocalDate.now().minusMonths(1));
        dpFechaFinInversionista.setValue(LocalDate.now());
        dpFechaInicioInversionista.setValue(LocalDate.now().minusMonths(1));

        // Configurar tabla del Top 5
        colTopNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTopRendimiento.setCellValueFactory(new PropertyValueFactory<>("yieldPercentageFormatted"));
        
        // Cargar el Top 5 inicial
        actualizarTopInversionistas();
    }

    @FXML
    private void generarReporteGlobal() {
        LocalDate fechaInicio = dpFechaInicioGlobal.getValue();
        LocalDate fechaFin = dpFechaFinGlobal.getValue();

        if (fechaInicio == null || fechaFin == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fechas Requeridas", "Por favor, selecciona una fecha de inicio y una fecha de fin para el reporte global.");
            return;
        }
        if (fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fechas Inválidas", "La fecha de inicio no puede ser posterior a la fecha de fin.");
            return;
        }

        try {
            List<Investment> allInvestments = investmentService.listInvestments();
            double gananciasGlobales = portfolioService.calculateEarningsByPeriod(allInvestments, fechaInicio, fechaFin);
            lblGananciaGlobal.setText(String.format("Total: $%,.2f", gananciasGlobales));
            exportarReporteGlobalPDF(gananciasGlobales, fechaInicio, fechaFin);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Generar Reporte", "Ocurrió un error al calcular las ganancias globales: " + e.getMessage());
        }
    }

    private void exportarReporteGlobalPDF(double ganancias, LocalDate inicio, LocalDate fin) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte Global");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Ganancias_Globales_" + inicio + "_a_" + fin + ".pdf");
        
        Window stage = dpFechaFinGlobal.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("--- Reporte de Ganancias Globales ---\n");
                writer.write("Fecha de Inicio: " + inicio + "\n");
                writer.write("Fecha de Fin: " + fin + "\n");
                writer.write("Ganancia Total en el Periodo: $" + String.format("%,.2f", ganancias) + "\n");
                writer.write("------------------------------------\n");
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Reporte global exportado a:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Exportación", "No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void generarReporteInversionista() {
        String investorId = txtIdInversionista.getText();
        LocalDate fechaInicio = dpFechaInicioInversionista.getValue();
        LocalDate fechaFin = dpFechaFinInversionista.getValue();

        if (investorId == null || investorId.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "ID Requerido", "Por favor, ingresa el ID del inversionista.");
            return;
        }
        if (fechaInicio == null || fechaFin == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fechas Requeridas", "Por favor, selecciona una fecha de inicio y una fecha de fin.");
            return;
        }
        if (fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fechas Inválidas", "La fecha de inicio no puede ser posterior a la fecha de fin.");
            return;
        }

        try {
            Investor investor = investorService.findById(investorId);
            if (investor == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Inversionista No Encontrado", "No se encontró un inversionista con el ID proporcionado.");
                return;
            }
            
            List<Investment> investorInvestments = investmentService.getInvestmentsByInvestorId(investorId);
            double gananciasInversionista = portfolioService.calculateEarningsByPeriod(investorInvestments, fechaInicio, fechaFin);
            lblGananciaInversionista.setText(String.format("Total: $%,.2f", gananciasInversionista));
            exportarReporteInversionistaPDF(investor, gananciasInversionista, fechaInicio, fechaFin);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Generar Reporte", "Ocurrió un error al calcular las ganancias del inversionista: " + e.getMessage());
        }
    }

    private void exportarReporteInversionistaPDF(Investor investor, double ganancias, LocalDate inicio, LocalDate fin) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte Inversionista");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Inversionista_" + investor.getId() + "_" + inicio + "_a_" + fin + ".pdf");
        
        Window stage = txtIdInversionista.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("--- Reporte de Ganancias por Inversionista ---\n");
                writer.write("ID Inversionista: " + investor.getId() + "\n");
                writer.write("Email: " + investor.getEmail() + "\n");
                writer.write("Fecha de Inicio: " + inicio + "\n");
                writer.write("Fecha de Fin: " + fin + "\n");
                writer.write("Ganancia Total en el Periodo: $" + String.format("%,.2f", ganancias) + "\n");
                writer.write("---------------------------------------------\n");
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Reporte de inversionista exportado a:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Exportación", "No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void actualizarTopInversionistas() {
        try {
            List<Investor> topInvestors = portfolioService.getTop5InvestorsByYield();
            ObservableList<InvestorReportData> topData = FXCollections.observableArrayList();
            
            for (Investor inv : topInvestors) {
                double yield = portfolioService.calculateYieldPercentage(inv);
                String name = (inv.getName() != null && !inv.getName().isBlank()) ? inv.getName() : inv.getEmail().split("@")[0];
                topData.add(new InvestorReportData(name, yield));
            }
            tablaTopInversionistas.setItems(topData);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Cargar Top 5", "Ocurrió un error al cargar el Top 5 de inversionistas: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/co/edu/uptc/css/dashboard.css").toExternalForm());
        alert.showAndWait();
    }

    /**
     * Clase auxiliar para el TableView del Top 5.
     * Necesaria porque TableView requiere propiedades para PropertyValueFactory.
     */
    public static class InvestorReportData {
        private final String name;
        private final double yieldPercentage;

        public InvestorReportData(String name, double yieldPercentage) {
            this.name = name;
            this.yieldPercentage = yieldPercentage;
        }

        public String getName() {
            return name;
        }

        public double getYieldPercentage() {
            return yieldPercentage;
        }

        public String getYieldPercentageFormatted() {
            return String.format("%.2f%%", yieldPercentage);
        }
    }
}