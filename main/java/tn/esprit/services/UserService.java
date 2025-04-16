package tn.esprit.services;

import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.entities.User;
import tn.esprit.tools.MyDataBase;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserService implements IServices<User> {
    private Connection cnx;
    private static UserService instance;

    public UserService() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    @Override
    public void ajouter(User user) {
        try {
            // Validate required fields
            if (user.getEmail() == null || user.getEmail().isEmpty() ||
                    user.getPassword() == null || user.getPassword().isEmpty() ||
                    user.getName() == null || user.getName().isEmpty()) {
                throw new IllegalArgumentException("Email, password and name are required");
            }

            // Hash password
            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(user.getPassword(), salt);
            user.setPassword(hashedPassword);

            // Handle roles
            Set<String> roles = user.getRoles() != null ? user.getRoles() : new HashSet<>();
            if (roles.isEmpty()) {
                roles.add("ROLE_STUDENT");
            }
            String rolesJson = "[\"" + String.join("\",\"", roles) + "\"]";

            String query = "INSERT INTO user (email, password, name, roles, is_restricted, date_creation, " +
                    "is_verified, bio, gender, diplome, speciality, age, country, pfp, bg) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getName());
                statement.setString(4, rolesJson);
                statement.setBoolean(5, user.getRestricted() != null && user.getRestricted());
                statement.setTimestamp(6, Timestamp.valueOf(
                        user.getCreationDate() != null ? user.getCreationDate() : LocalDateTime.now()
                ));
                statement.setBoolean(7, user.getVerified() != null && user.getVerified());
                statement.setString(8, user.getBio() != null ? user.getBio() : "");
                statement.setString(9, user.getGender() != null ? user.getGender() : "");
                statement.setString(10, user.getDiplome() != null ? user.getDiplome() : "");
                statement.setString(11, user.getSpeciality() != null ? user.getSpeciality() : "");
                statement.setObject(12, user.getAge(), Types.INTEGER);
                statement.setString(13, user.getCountry() != null ? user.getCountry() : "");
                statement.setString(14, user.getpfp() != null ? user.getpfp() : "");
                statement.setString(15, user.getbg() != null ? user.getbg() : "");

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add user. Database constraint violation. Error: " + e.getMessage(), e);
        }
    }

    @Override
    public void modifier(User user) {
        try {
            String query = "UPDATE user SET " +
                    "email = ?, " +
                    "name = ?, " +
                    "bio = ?, " +
                    "gender = ?, " +
                    "diplome = ?, " +
                    "speciality = ?, " +
                    "age = ?, " +
                    "country = ?, " +
                    "pfp = ?, " +
                    "bg = ? " +
                    "WHERE id = ?";

            try (PreparedStatement pst = cnx.prepareStatement(query)) {
                pst.setString(1, user.getEmail());
                pst.setString(2, user.getName());
                pst.setString(3, user.getBio());
                pst.setString(4, user.getGender());
                pst.setString(5, user.getDiplome());
                pst.setString(6, user.getSpeciality());
                pst.setObject(7, user.getAge(), Types.INTEGER);
                pst.setString(8, user.getCountry());
                pst.setString(9, user.getpfp());
                pst.setString(10, user.getbg());
                pst.setLong(11, user.getId());

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("User updated successfully!");
                } else {
                    System.out.println("No user found with ID: " + user.getId());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    public void updatePassword(User user) {
        try {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(user.getPassword(), salt);
            user.setPassword(hashedPassword);

            String query = "UPDATE user SET password = ? WHERE id = ?";
            try (PreparedStatement pst = cnx.prepareStatement(query)) {
                pst.setString(1, user.getPassword());
                pst.setLong(2, user.getId());

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Password updated successfully!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String query = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    public User getById(long id) {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setLong(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setVerified(rs.getBoolean("is_verified"));
                user.setRestricted(rs.getBoolean("is_restricted"));
                user.setCreationDate(rs.getTimestamp("date_creation").toLocalDateTime());
                user.setBio(rs.getString("bio"));
                user.setGender(rs.getString("gender"));
                user.setDiplome(rs.getString("diplome"));
                user.setSpeciality(rs.getString("speciality"));
                user.setAge(rs.getInt("age"));
                user.setCountry(rs.getString("country"));
                user.setpfp(rs.getString("pfp"));
                user.setbg(rs.getString("bg"));

                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
        }
        return null;
    }

    public User getByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setVerified(rs.getBoolean("is_verified"));
                user.setRestricted(rs.getBoolean("is_restricted"));
                user.setCreationDate(rs.getTimestamp("date_creation").toLocalDateTime());
                user.setBio(rs.getString("bio"));
                user.setGender(rs.getString("gender"));
                user.setDiplome(rs.getString("diplome"));
                user.setSpeciality(rs.getString("speciality"));
                user.setAge(rs.getInt("age"));
                user.setCountry(rs.getString("country"));
                user.setpfp(rs.getString("pfp"));
                user.setbg(rs.getString("bg"));

                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User getOne(User user) {
        return getById(user.getId());
    }

    public void supprimer(long id) {
        supprimer((int) id);
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                user.setVerified(rs.getBoolean("is_verified"));
                user.setRestricted(rs.getBoolean("is_restricted"));
                user.setCreationDate(rs.getTimestamp("date_creation").toLocalDateTime());
                user.setBio(rs.getString("bio"));
                user.setGender(rs.getString("gender"));
                user.setDiplome(rs.getString("diplome"));
                user.setSpeciality(rs.getString("speciality"));
                user.setAge(rs.getInt("age"));
                user.setCountry(rs.getString("country"));
                user.setpfp(rs.getString("pfp"));
                user.setbg(rs.getString("bg"));

                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public boolean verifyPassword(String email, String plainPassword) {
        try {
            User user = getByEmail(email);
            if (user == null || user.getPassword() == null) {
                return false;
            }
            return BCrypt.checkpw(plainPassword, user.getPassword());
        } catch (Exception e) {
            System.err.println("Verification error: " + e.getMessage());
            return false;
        }
    }
}