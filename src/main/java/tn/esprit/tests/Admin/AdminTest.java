package tn.esprit.tests.Admin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class AdminTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chemin relatif depuis le dossier resources
        URL fxmlUrl = getClass().getResource("/interfaces/Admin/Admin.fxml");

        if (fxmlUrl == null) {
            System.err.println("Erreur: Fichier FXML introuvable");
            System.err.println("Le programme cherchait à: /interfaces/Admin/Admin.fxml");
            System.err.println("Vérifiez que le fichier existe dans:");
            System.err.println("src/main/resources/interfaces/Admin/Admin.fxml");
            throw new RuntimeException("Fichier FXML introuvable");
        }

        System.out.println("Fichier FXML trouvé à: " + fxmlUrl);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Chargement du CSS (adaptez le chemin si nécessaire)
        URL cssUrl = getClass().getResource("/css/Admin.css");
        if (cssUrl != null) {
            root.getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("CSS chargé depuis: " + cssUrl);
        } else {
            System.err.println("Attention: Fichier CSS non trouvé");
        }

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Backoffice Admin");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Démarrer l'application JavaFX
        launch(args);
    }
}