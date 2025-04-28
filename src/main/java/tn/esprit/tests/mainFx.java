package tn.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.utils.StripeConfig;

public class mainFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StripeConfig.initializeStripe();
        // Charger AfficherOffre.fxml au démarrage
       FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/frontOffre.fxml"));

     //FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/AfficherOffre.fxml"));
        Parent root = loader.load();

        // Créer la scène
        Scene scene = new Scene(root);

        // Ajouter le fichier CSS
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        System.out.println("Fichier CSS chargé depuis : " + getClass().getResource("/css/style.css"));

        // Configurer la fenêtre
        primaryStage.setTitle("Liste des Offres 💼");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}