package tn.esprit.services;

import tn.esprit.entities.Courses;
import tn.esprit.entities.Category;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CoursesService implements IServices<Courses> {
    private Connection cnx;
    private static CoursesService instance;

    public CoursesService() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public static CoursesService getInstance() {
        if (instance == null) {
            instance = new CoursesService();
        }
        return instance;
    }

    @Override
    public void ajouter(Courses course) {
        String query = "INSERT INTO courses (title, description, is_published, progress_points_required, "
                + "created_at, category_id, is_premium, tutor_name) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, course.getTitle());
            pst.setString(2, course.getDescription());
            pst.setBoolean(3, course.getIsPublished());
            pst.setInt(4, course.getProgressPointsRequired());
            pst.setTimestamp(5, Timestamp.from(course.getCreatedAt()));
            pst.setInt(6, course.getCategory().getId());
            pst.setBoolean(7, course.getIsPremium());
            pst.setString(8, course.getTutorName());

            int affectedRows = pst.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating course failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    course.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating course failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add course: " + e.getMessage(), e);
        }
    }

    @Override
    public void modifier(Courses course) {
        String query = "UPDATE courses SET title = ?, description = ?, is_published = ?, "
                + "progress_points_required = ?, category_id = ?, is_premium = ?, "
                + "tutor_name = ? WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, course.getTitle());
            pst.setString(2, course.getDescription());
            pst.setBoolean(3, course.getIsPublished());
            pst.setInt(4, course.getProgressPointsRequired());
            pst.setInt(5, course.getCategory().getId());
            pst.setBoolean(6, course.getIsPremium());
            pst.setString(7, course.getTutorName());
            pst.setInt(8, course.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No course found with ID: " + course.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update course: " + e.getMessage(), e);
        }
    }

    @Override
    public Courses getOne(Courses course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        String query = "SELECT c.*, cat.name as category_name FROM courses c "
                + "JOIN category cat ON c.category_id = cat.id WHERE c.id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, course.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get course: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void supprimer(int id) {
        String query = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("No course found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete course: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Courses> getAll() {
        List<Courses> courses = new ArrayList<>();
        String query = "SELECT c.*, cat.name as category_name FROM courses c "
                + "JOIN category cat ON c.category_id = cat.id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get courses: " + e.getMessage(), e);
        }
        return courses;
    }

    public List<Courses> getByCategory(int categoryId) {
        List<Courses> courses = new ArrayList<>();
        String query = "SELECT c.*, cat.name as category_name FROM courses c "
                + "JOIN category cat ON c.category_id = cat.id "
                + "WHERE c.category_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, categoryId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get courses by category: " + e.getMessage(), e);
        }
        return courses;
    }

    public List<Courses> getPublishedCourses() {
        List<Courses> courses = new ArrayList<>();
        String query = "SELECT c.*, cat.name as category_name FROM courses c "
                + "JOIN category cat ON c.category_id = cat.id "
                + "WHERE c.is_published = true";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get published courses: " + e.getMessage(), e);
        }
        return courses;
    }

    private Courses mapResultSetToCourse(ResultSet rs) throws SQLException {
        Courses course = new Courses();
        course.setId(rs.getInt("id"));
        course.setTitle(rs.getString("title"));
        course.setDescription(rs.getString("description"));
        course.setIsPublished(rs.getBoolean("is_published"));
        course.setProgressPointsRequired(rs.getInt("progress_points_required"));
        course.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        course.setIsPremium(rs.getBoolean("is_premium"));
        course.setTutorName(rs.getString("tutor_name"));

        Category category = new Category();
        category.setId(rs.getInt("category_id"));
        category.setName(rs.getString("category_name"));
        course.setCategory(category);

        return course;
    }
}