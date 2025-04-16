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

            // Store plain password before hashing
            String plainPassword = user.getPassword();
            System.out.println("\n=== Signup Process ===");
            System.out.println("Original password: '" + plainPassword + "'");

            // Generate salt and hash
            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(plainPassword, salt);
            System.out.println("Generated salt: " + salt);
            System.out.println("Hashed password: " + hashedPassword);
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
                // Set required fields
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getName());
                statement.setString(4, rolesJson);

                // Set defaults
                statement.setBoolean(5, user.getRestricted() != null && user.getRestricted());
                statement.setTimestamp(6, Timestamp.valueOf(
                        user.getCreationDate() != null ? user.getCreationDate() : LocalDateTime.now()
                ));
                statement.setBoolean(7, user.getVerified() != null && user.getVerified());

                // Set optional fields
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

                // Immediate verification test using plain password
                User savedUser = getByEmail(user.getEmail());
                if (savedUser != null) {
                    boolean verified = BCrypt.checkpw(plainPassword, savedUser.getPassword());
                    System.out.println("Immediate verification test: " + verified);
                    if (!verified) {
                        System.err.println("WARNING: Immediate verification failed!");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add user. Database constraint violation. Error: " + e.getMessage(), e);
        }
    }

    public boolean verifyPassword(String email, String plainPassword) {
        try {
            System.out.println("\n=== Starting Verification ===");
            System.out.println("Email: " + email);
            System.out.println("Input Password: '" + plainPassword + "'");

            User user = getByEmail(email);
            if (user == null) {
                System.out.println("User not found");
                return false;
            }

            String storedHash = user.getPassword();
            System.out.println("Stored Hash: '" + storedHash + "'");

            if (storedHash == null || storedHash.isEmpty()) {
                System.out.println("No password hash stored");
                return false;
            }

            // Normalize inputs
            storedHash = storedHash.trim();
            plainPassword = plainPassword.trim();

            System.out.println("Normalized Stored Hash: '" + storedHash + "'");
            System.out.println("Normalized Input Password: '" + plainPassword + "'");

            // Verify the hash structure first
            if (!storedHash.startsWith("$2a$")) {
                System.err.println("Invalid hash format");
                return false;
            }

            // Standard BCrypt verification
            boolean result = BCrypt.checkpw(plainPassword, storedHash);
            System.out.println("Verification Result: " + result);

            return result;
        } catch (Exception e) {
            System.err.println("Verification error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void modifier(User user) {
        try {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                System.out.println("Updating password for user: " + user.getEmail());
                String salt = BCrypt.gensalt();
                String hashedPassword = BCrypt.hashpw(user.getPassword(), salt);
                user.setPassword(hashedPassword);
            }

            String query = "UPDATE user SET email = ?, password = ?, name = ? WHERE id = ?";
            try (PreparedStatement pst = cnx.prepareStatement(query)) {
                pst.setString(1, user.getEmail());
                pst.setString(2, user.getPassword());
                pst.setString(3, user.getName());
                pst.setLong(4, user.getId());

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

    @Override
    public void supprimer(int id) {
        String query = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("No user found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
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
                user.setName(rs.getString("name"));

                // Get password and ensure proper handling
                String storedHash = rs.getString("password");
                if (storedHash != null) {
                    storedHash = storedHash.trim();
                }
                user.setPassword(storedHash);

                user.setVerified(rs.getBoolean("is_verified"));
                user.setRestricted(rs.getBoolean("is_restricted"));

                System.out.println("Retrieved user: " + user.getEmail());
                System.out.println("Retrieved hash: " + user.getPassword());

                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User getOne(User user) {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setLong(1, user.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                User foundUser = new User(
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("password")
                );
                foundUser.setId(rs.getLong("id"));  // Add this line
                return foundUser;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
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
                User user = new User(
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("password")
                );
                user.setId(rs.getLong("id"));  // Add this line
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public static void testBCryptBehavior() {
        String password = "123123";

        System.out.println("\n=== BCrypt Behavior Test ===");

        // Test basic hash and verify
        String hash1 = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Test 1 - Hash: " + hash1);
        System.out.println("Test 1 - Verify: " + BCrypt.checkpw(password, hash1));
    }
}