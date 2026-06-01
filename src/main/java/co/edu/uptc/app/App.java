package co.edu.uptc.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

import co.edu.uptc.model.User;

public class App extends Application {

    private static Scene scene;
    public static String emailARestablecer;
    private static User usuarioLogueado;

    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("/co/edu/uptc/fonts/Roboto-Regular.ttf"), 12);
        scene = new Scene(loadFXML("login"), 1200, 720);
        stage.setTitle("GLOBAL");
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

    // 🔓 Métodos globales para controlar la sesión desde cualquier controlador
    public static void setUsuarioLogueado(User user) {
        usuarioLogueado = user;
    }

    public static User getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public static void main(String[] args) {
        launch();
    }
}