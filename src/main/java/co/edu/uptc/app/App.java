package co.edu.uptc.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.Investment;
import co.edu.uptc.model.Investor;
import co.edu.uptc.model.User;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.service.AssetService;
import co.edu.uptc.service.InvestmentService;
import co.edu.uptc.service.InvestorService;
import co.edu.uptc.service.PortfolioService;
import co.edu.uptc.service.UserService;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/co/edu/uptc/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void mainv(String[] args) {
        launch();
    }

    public static void main(String[] args) {
        // 1. INICIALIZACIÓN DE SERVICIOS
        AssetService assetService = new AssetService();
        InvestorService investorService = new InvestorService();
        UserService userService = new UserService();
        InvestmentService investmentService = new InvestmentService(assetService, investorService);
        PortfolioService portfolioService = new PortfolioService(investmentService, assetService, investorService);

        Scanner scanner = new Scanner(System.in);
        System.out.println("====================================================");
        System.out.println("   SIMULADOR FINANCIERO UPTC - MÓDULO DE CONSOLA    ");
        System.out.println("====================================================\n");

        try {
            // STEP 1: Inicializando / Buscando el Activo en el Mercado
            System.out.println("--- 1. Inicializando Activos en el Mercado ---");

            String idActivoParaCompra = "";
            List<Asset> activosEnMercado = assetService.listAssets();

            if (activosEnMercado.isEmpty()) {
                // Generamos un activo compatible con las pruebas de rendimiento (STOCK / ACCION)
                assetService.createAsset("AAPL", AssetType.STOCK, 150.0, 0.15);

                Asset activoCreado = assetService.listAssets().get(0);
                idActivoParaCompra = activoCreado.getId(); 
                System.out.println("[OK] Activo 'AAPL' creado. El UUID interno asignado fue: " + idActivoParaCompra);
            } else {
                Asset activoExistente = activosEnMercado.stream()
                        .filter(a -> a.getName().equalsIgnoreCase("AAPL"))
                        .findFirst()
                        .orElse(activosEnMercado.get(0));

                idActivoParaCompra = activoExistente.getId();
                System.out.println("[INFO] El activo 'AAPL' ya existe en JSON con el UUID: " + idActivoParaCompra);
            }
            System.out.println();

            // STEP 2: Registro o Sincronización Completa del Inversionista
            System.out.println("--- 2. Registro de Nuevo Usuario e Inversionista ---");
            String usernamePrueba = "juan_investor";
            String passwordPrueba = "clave123";
            String emailPrueba = "juan@uptc.edu.co";

            Optional<User> usuarioExistente = userService.authenticate(usernamePrueba, passwordPrueba);
            String investorId;

            if (usuarioExistente.isEmpty()) {
                // Creación desde cero con perfil AGGRESSIVE para saltar restricciones
                investorService.createInvestor("Juan Perez", emailPrueba, 1000.0, RiskProfile.AGGRESSIVE);

                Investor juan = investorService.listInversionists().stream()
                        .filter(i -> i.getEmail().equalsIgnoreCase(emailPrueba))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Error al recuperar el inversionista creado."));

                investorId = juan.getId();
                userService.registerUser(investorId, usernamePrueba, passwordPrueba, "INVESTOR");
                
                System.out.println("[OK] Inversionista y Usuario registrados con éxito.");
                System.out.println("     Investor ID: " + investorId + " | Perfil: AGGRESSIVE");
            } else {
                investorId = usuarioExistente.get().getInvestorId();
                System.out.println("[INFO] El usuario '" + usernamePrueba + "' ya existe. Reutilizando ID: " + investorId);
                
                // CONTROL DE SEGURIDAD EN CALIENTE:
                // Forzamos a que el Juan ya registrado en el JSON pase a AGGRESSIVE para evitar la excepción de riesgo
                Investor juanExistente = investorService.findById(investorId);
                if (juanExistente != null && juanExistente.getRiskProfile() != RiskProfile.AGGRESSIVE) {
                    juanExistente.setRiskProfile(RiskProfile.AGGRESSIVE);
                    // Forzar el guardado en el JSON actualizando el inversor modificado si tu service lo permite
                    System.out.println("[MODIFICACIÓN] Perfil de riesgo de Juan actualizado a AGGRESSIVE en memoria.");
                }
            }
            System.out.println();

            // STEP 3: Autenticación (Login)
            System.out.println("--- 3. Simulando Login al Sistema ---");
            Optional<User> sesion = userService.authenticate(usernamePrueba, passwordPrueba);
            if (sesion.isPresent()) {
                System.out.println("[OK] Autenticación Exitosa. Bienvenido, " + sesion.get().getUsername());
            } else {
                System.out.println("[FAIL] Error de autenticación.");
                return;
            }
            System.out.println();

            // STEP 4: Simulación de Compra de Activos
            System.out.println("--- 4. Ejecutando Operación de Compra ---");
            System.out.println("Comprando 2 unidades de AAPL mediante su UUID...");

            Investment miInversion = investmentService.createInvestment(investorId, idActivoParaCompra, 2.0);

            System.out.println("[OK] Inversión registrada. ID Transacción: " + miInversion.getId());

            Investor estadoJuan = investorService.findById(investorId);
            System.out.println(">> Nuevo saldo en efectivo (Billetera): $" + estadoJuan.getAvailableCapital());
            System.out.println(">> Valor de la inversión inicial: $" + miInversion.getPurchasePrice());
            System.out.println();

            // STEP 5: Simulación de Fluctuación de Mercado
            System.out.println("--- 5. El Mercado Fluctúa (Cambio de Precios) ---");
            System.out.println("¡Buenas noticias! Apple reporta excelentes ganancias. El precio sube a $200.0");

            investmentService.updateAssetPriceProcess(idActivoParaCompra, 200.0);

            double valorActualPortafolio = portfolioService.calculateCurrentPortfolioValue(estadoJuan);
            double rendimiento = portfolioService.calculateYieldPercentage(estadoJuan);

            System.out.println(">> Precio de AAPL en mercado actual: $" + assetService.findById(idActivoParaCompra).getActualPrice());
            System.out.println(">> Nuevo valor de tus 2 acciones en Portafolio: $" + valorActualPortafolio);
            System.out.println(">> Rendimiento porcentual del inversionista: " + String.format("%.2f", rendimiento) + "%");
            System.out.println();

            // STEP 6: Ejecución de Venta
            System.out.println("--- 6. Ejecutando Operación de Venta (Tomar Ganancias) ---");
            System.out.print("Presiona ENTER para vender la posición y retirar el dinero a la billetera...");
            scanner.nextLine();

            investmentService.sellInvestment(investorId, miInversion.getId());
            System.out.println("[OK] Posición liquidada exitosamente.");

            Investor juanLiquidado = investorService.findById(investorId);
            System.out.println("=================== BALANCE FINAL ===================");
            System.out.println("Efectivo disponible final: $" + juanLiquidado.getAvailableCapital());
            System.out.println("Cantidad de inversiones activas: " + juanLiquidado.getInvestments().size());
            System.out.println("=====================================================");

        } catch (Exception e) {
            System.err.println("\n[ERROR EN MATRIZ LÓGICA]: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}