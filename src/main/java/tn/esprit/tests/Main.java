package tn.esprit.tests;

import tn.esprit.entities.Offre;
import tn.esprit.entities.Paiement;
import tn.esprit.entities.User;
import tn.esprit.services.offreService;
import tn.esprit.services.paiementService;
import tn.esprit.tools.MyDataBase;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws SQLException {
        offreService pst = new offreService();
        paiementService ps = new paiementService();

        // Créer une nouvelle offre (initiale sans ID)
        Offre offre = new Offre("Abonnement Premium", "images/premium.jpg", 29.99, LocalDateTime.now(), LocalDate.now().plusMonths(1), "Accès complet à tous les cours premium.");
        Offre offre1 = new Offre("Premium", "images/premium.jpg", 29.99, LocalDateTime.now(), LocalDate.now().plusMonths(1), "Accès complet à tous les cours premium.");
        // Ajouter l'offre dans la base de données
       // pst.ajouter(offre);
        //System.out.println("Offre ID après insertion : " + offre.getId()); // Vérifie que l'ID a bien été généré

        // Créer un utilisateur pour le paiement
        User user = new User();
        user.setId(2L);  // L'ID de l'utilisateur doit être valide

        // Créer un paiement pour l'offre insérée
      //  Paiement paiement = new Paiement(user, offre);
        Paiement paiement1 = new Paiement(user, offre1);
      
        paiement1.setId(1L);
        try {

         //  pst.ajouter(offre1);
          //  pst.modifier(1, newOffre);
            //pst.supprimer(offre2);
          //  ps.ajouter(paiement);
         //   ps.modifier(1,paiement1);
            System.out.println(pst.recuperer());
            System.out.println(ps.recuperer());
            ps.supprimer(paiement1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void testSimpleQuery(Connection cnx) {
        try (Statement stmt = cnx.createStatement()) {
            // Try querying the database version
            ResultSet rs = stmt.executeQuery("SELECT VERSION()");
            if (rs.next()) {
                System.out.println("MySQL Version: " + rs.getString(1));
            }

            // Try listing tables (optional)
            System.out.println("\nListing tables in your database:");
            rs = stmt.executeQuery("SHOW TABLES");
            while (rs.next()) {
                System.out.println("- " + rs.getString(1));
            }
        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }
    }
}