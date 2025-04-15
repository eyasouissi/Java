package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.tools.MyDataBase;
import tn.esprit.entities.Offre;
import tn.esprit.entities.Paiement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class paiementService implements IServices<Paiement> {
    Connection cnx;

    public paiementService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Paiement paiement) throws SQLException {
        String sql = "INSERT INTO paiement (id_user, id_offre, date_paiement) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Utilisez directement l'ID de l'utilisateur dans l'objet Paiement
            long userId = paiement.getUser().getId();

            // Vérifiez si l'utilisateur existe dans la base de données
            String checkUserQuery = "SELECT id FROM user WHERE id = ?";
            try (PreparedStatement checkUserStmt = cnx.prepareStatement(checkUserQuery)) {
                checkUserStmt.setLong(1, userId);
                try (ResultSet rs = checkUserStmt.executeQuery()) {
                    if (rs.next()) {
                        // Utilisateur trouvé, on procède à l'insertion du paiement
                        pst.setLong(1, userId);  // ID de l'utilisateur
                        pst.setLong(2, paiement.getOffre().getId());  // ID de l'offre
                        pst.setTimestamp(3, Timestamp.valueOf(paiement.getPaymentDate()));  // Date du paiement

                        int affectedRows = pst.executeUpdate();

                        // Vérifier si l'insertion a réussi
                        if (affectedRows > 0) {
                            // Récupérer les clés générées (ID auto-incrémenté)
                            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    paiement.setId(generatedKeys.getLong(1));  // Affecter l'ID à l'objet Paiement
                                    System.out.println("Paiement ID après insertion : " + paiement.getId());
                                }
                            }
                        }
                    } else {
                        System.out.println("Utilisateur non trouvé avec l'ID : " + userId);
                    }
                }
            }
        }
    }
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name FROM user";  // Requête pour récupérer id et name des utilisateurs
        try (PreparedStatement pst = cnx.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));        // Récupérer l'ID de l'utilisateur
                user.setName(rs.getString("name"));  // Récupérer le nom de l'utilisateur
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Gérer l'erreur correctement
        }
        return users;
    }


    @Override
    public void supprimer(Paiement paiement) throws SQLException {
        String sql = "DELETE FROM paiement WHERE id_paiement = " + paiement.getId();
        Statement st = cnx.createStatement();
        int rowsDeleted = st.executeUpdate(sql);

        if (rowsDeleted > 0) {
            System.out.println("Paiement supprimé avec succès !");
        } else {
            System.out.println("Aucun paiement trouvé avec cet ID.");
        }
    }

    @Override
    public void modifier(int id, Paiement newPaiement) throws SQLException {
        String sql = "UPDATE paiement SET " +
                "id_user = " + newPaiement.getUser().getId() + ", " +
                "id_offre = " + newPaiement.getOffre().getId() + ", " +
                "date_paiement = '" + Timestamp.valueOf(newPaiement.getPaymentDate()) + "' " +
                "WHERE id_paiement = " + id;

        Statement st = cnx.createStatement();
        int rowsUpdated = st.executeUpdate(sql);

        if (rowsUpdated > 0) {
            System.out.println("Paiement modifié avec succès !");
        } else {
            System.out.println("Aucun paiement trouvé avec l'ID : " + id);
        }
    }

    @Override
    public List<Paiement> recuperer() throws SQLException {
        String sql = "SELECT * FROM paiement";
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        List<Paiement> paiements = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id_paiement");
            int userId = rs.getInt("id_user");
            int offreId = rs.getInt("id_offre");
            Timestamp datePaiementTimestamp = rs.getTimestamp("date_paiement");

            LocalDateTime datePaiement = (datePaiementTimestamp != null) ? datePaiementTimestamp.toLocalDateTime() : null;

            User user = new User();
            user.setId((long) userId);

            Offre offre = new Offre();
            offre.setId((long) offreId);

            Paiement paiement = new Paiement(user, offre, datePaiement);
            paiement.setId((long) id);
            paiements.add(paiement);
        }

        return paiements;
    }

    public List<String> getAllUserNames() throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT nom FROM user";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            names.add(rs.getString("nom"));
        }
        return names;
    }
}
