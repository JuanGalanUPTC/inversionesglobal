package co.edu.uptc.viewcontroller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private ComboBox<String> idiomaComboBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Agregar los identificadores de los idiomas
        idiomaComboBox.getItems().addAll("es", "en");

        // 2. Definir cómo se verán los elementos en la lista desplegable
        idiomaComboBox.setCellFactory(param -> createCustomCell());

        // 3. Definir cómo se verá el elemento SELECCIONADO en el botón principal
        idiomaComboBox.setButtonCell(createCustomCell());

        // Seleccionar el primero por defecto
        idiomaComboBox.getSelectionModel().selectFirst();
    }

    // Método auxiliar que construye la fila con Imagen + Texto Código + Texto Idioma
    private ListCell<String> createCustomCell() {
    return new ListCell<String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                HBox container = new HBox(10);
                container.setAlignment(Pos.CENTER_LEFT);

                ImageView flagView = new ImageView();
                flagView.setFitWidth(24);
                flagView.setFitHeight(16);
                flagView.setPreserveRatio(true);

                Label lblName = new Label(item.equals("es") ? "Español" : "English");
                lblName.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

                // Ajustamos las rutas exactamente a tu paquete personalizado
                String imagePath = item.equals("es") ? "/co/edu/uptc/images/colflag.png" : "/co/edu/uptc/images/usaflag.jpg";

                try {
                    java.io.InputStream stream = getClass().getResourceAsStream(imagePath);
                    if (stream != null) {
                        flagView.setImage(new Image(stream));
                        container.getChildren().addAll(flagView, lblName);
                    } else {
                        System.out.println("⚠️ No se encontró la imagen en: " + imagePath);
                        container.getChildren().add(lblName);
                    }
                } catch (Exception e) {
                    container.getChildren().add(lblName);
                }

                setGraphic(container);
            }
        }
    };
}
}