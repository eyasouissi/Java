package tn.esprit.controllers;

import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.DefaultStringConverter;
import tn.esprit.entities.Forum;
import tn.esprit.entities.Post;
import tn.esprit.tools.MyDataBase;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class PostController implements Initializable {

    @FXML private VBox postContainer;
    @FXML private Label forumTitle;
    @FXML private TextArea postContent;
    @FXML private ListView<Post> postsListView;
    @FXML private FlowPane photosFlowPane;
    @FXML private Button submitPostBtn;
    @FXML private Label charCountLabel;
    @FXML
    private VBox postInputContainer;
    @FXML
    private Label postErrorLabel;
    @FXML
    private Label contentErrorLabel;




    private ObservableList<Post> posts = FXCollections.observableArrayList();
    private Forum currentForum;
    private List<String> selectedPhotoNames = new ArrayList<>();
    private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPostListView();
        setupInputValidation();
    }

    private void setupInputValidation() {
        // Character limit enforcement
        postContent.setTextFormatter(new TextFormatter<>(new DefaultStringConverter(), "", change -> {
            if (change.getControlNewText().length() <= 200) {
                return change;
            }
            return null;
        }));

        // Character count binding
        charCountLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            int length = postContent.getText().length();
            return length + "/200";
        }, postContent.textProperty()));

        // Validation styling
        postContent.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean isEmpty = newVal.trim().isEmpty();
            boolean isTooLong = newVal.length() > 200;

            postContent.pseudoClassStateChanged(errorClass, isEmpty || isTooLong);
            charCountLabel.pseudoClassStateChanged(errorClass, isTooLong);
        });
    }

    public void setForum(Forum forum) {
        this.currentForum = forum;
        forumTitle.setText(forum.getTitle());
        loadPosts();
    }

    private void loadPosts() {
        posts.clear();
        String sql = "SELECT * FROM post WHERE forum_id = ?";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, currentForum.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setContent(rs.getString("content"));
                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                post.setLikes(rs.getInt("likes"));
                post.setPhotos(rs.getString("photos"));
                post.setGifUrl(rs.getString("gif_url"));
                posts.add(post);
            }

            postsListView.setItems(posts);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading posts: " + e.getMessage());
        }
    }

    private void setupPostListView() {
        postsListView.setCellFactory(lv -> new ListCell<Post>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setGraphic(null);
                } else {
                    setGraphic(createPostCard(post));
                }
            }
        });
    }

    private VBox createPostCard(Post post) {
        VBox card = new VBox(10);
        card.getStyleClass().add("post-card");

        Text content = new Text(post.getContent());
        content.getStyleClass().add("post-content");

        Text createdAt = new Text("Posted: " + post.getCreatedAt().toLocalDate());
        createdAt.getStyleClass().add("post-date");

        if (post.getPhotos() != null && !post.getPhotos().isEmpty()) {
            FlowPane photosContainer = new FlowPane(5, 5);
            Arrays.stream(post.getPhotos().split(","))
                    .forEach(photo -> {
                        try {
                            File imageFile = new File("uploads/" + photo.trim());
                            if (imageFile.exists()) {
                                ImageView imgView = new ImageView(new Image(imageFile.toURI().toString()));
                                imgView.setFitWidth(100);
                                imgView.setFitHeight(100);
                                imgView.setPreserveRatio(true);
                                photosContainer.getChildren().add(imgView);
                            }
                        } catch (Exception e) {
                            System.err.println("Error loading image: " + e.getMessage());
                        }
                    });
            card.getChildren().add(photosContainer);
        }

        HBox actions = new HBox(10);
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        editBtn.setOnAction(e -> handleEditPost(post));
        deleteBtn.setOnAction(e -> handleDeletePost(post));
        actions.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(content, createdAt, actions);
        return card;
    }

    @FXML
    private void handleAddPhotos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());
        if (files != null) {
            files.forEach(file -> {
                try {
                    Path uploadDir = Paths.get("uploads");
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                    }

                    String fileName = file.getName();
                    Path destination = uploadDir.resolve(fileName);

                    if (!Files.exists(destination)) {
                        Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                    }

                    if (!selectedPhotoNames.contains(fileName)) {
                        selectedPhotoNames.add(fileName);
                        photosFlowPane.getChildren().add(createPhotoThumbnail(fileName, destination.toUri().toString()));
                    }
                } catch (IOException e) {
                    showAlert("File Error", "Failed to save: " + e.getMessage());
                }
            });
        }
    }

    @FXML
    private void handleCreatePost() {
        postContent.pseudoClassStateChanged(errorClass, false);
        charCountLabel.pseudoClassStateChanged(errorClass, false);
        contentErrorLabel.setText(""); // Reset error text

        String content = postContent.getText().trim();
        boolean hasErrors = false;

        if (content.isEmpty()) {
            // Add error class and show message
            postContent.getStyleClass().add("text-field-error");
            contentErrorLabel.setText("Post content cannot be empty!");

            // Play error animation (optional)
            playPopAnimation(postInputContainer);

            // Make the error message disappear after 3 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> contentErrorLabel.setText(""));
            pause.play();

            return;
        } else {
            postContent.getStyleClass().remove("text-field-error");
        }

        if (content.length() > 200) {
            charCountLabel.pseudoClassStateChanged(errorClass, true);
            showAlert("Error", "Post content cannot exceed 2000 characters!");
            hasErrors = true;
        }

        if (hasErrors) return;

        Post newPost = new Post();
        newPost.setContent(content);
        newPost.setForum(currentForum);
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setUpdatedAt(LocalDateTime.now());
        newPost.setLikes(0);
        newPost.setPhotos(selectedPhotoNames.isEmpty() ? null : String.join(",", selectedPhotoNames));

        String sql = "INSERT INTO post (content, forum_id, user_id, likes, photos, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, newPost.getContent());
            pstmt.setLong(2, currentForum.getId());
            pstmt.setInt(3, 1); // Replace with actual user ID
            pstmt.setInt(4, newPost.getLikes());
            pstmt.setString(5, newPost.getPhotos());
            pstmt.setTimestamp(6, Timestamp.valueOf(newPost.getCreatedAt()));
            pstmt.setTimestamp(7, Timestamp.valueOf(newPost.getUpdatedAt()));

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) newPost.setId(rs.getInt(1));
            }

            posts.add(newPost);
            postContent.clear();
            photosFlowPane.getChildren().clear();
            selectedPhotoNames.clear();
            postContent.getStyleClass().remove("text-field-error");

        } catch (SQLException e) {
            showAlert("Database Error", "Create post failed: " + e.getMessage());
        }
    }

    private void playPopAnimation(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.05);
        st.setToY(1.05);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }


    private Node createPhotoThumbnail(String fileName, String imageUrl) {
        HBox container = new HBox(5);
        container.getStyleClass().add("photo-thumbnail");

        ImageView imageView = new ImageView(new Image(imageUrl));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        Button removeBtn = new Button("X");
        removeBtn.getStyleClass().add("remove-photo-btn");
        removeBtn.setOnAction(e -> {
            selectedPhotoNames.remove(fileName);
            photosFlowPane.getChildren().remove(container);
        });

        container.getChildren().addAll(imageView, removeBtn);
        return container;
    }

    private void handleEditPost(Post post) {
        // Create a dialog for the user to input new content
        TextInputDialog dialog = new TextInputDialog(post.getContent());
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit your post content");

        // Show dialog and get result
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newContent -> {
            // If content is empty, return early and show an error message
            if (newContent.trim().isEmpty()) {
                showAlert("Error", "Content cannot be empty!");
                return;
            }

            // SQL to update the post content in the database
            String sql = "UPDATE post SET content=?, updated_at=? WHERE id=?";
            try (Connection conn = MyDataBase.getInstance().getCnx();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, newContent);  // Set new content
                pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));  // Set updated timestamp
                pstmt.setInt(3, post.getId());  // Set the post id for the correct record

                // Execute the update query
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Successfully updated, so refresh the posts and show a success alert
                    loadPosts();
                    showSuccessAlert("Success", "Post updated!");
                } else {
                    // If no rows are affected, show an error (shouldn't happen in normal case)
                    showAlert("Error", "Post update failed, please try again.");
                }

            } catch (SQLException e) {
                // Show database error if there's an issue with the update
                showAlert("Error", "Update failed: " + e.getMessage());
            }
        });
    }


    private void handleDeletePost(Post post) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete this post?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM post WHERE id=?";
            try (Connection conn = MyDataBase.getInstance().getCnx();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, post.getId());
                pstmt.executeUpdate();
                posts.remove(post);
                showSuccessAlert("Success", "Post deleted!");

            } catch (SQLException e) {
                showAlert("Error", "Delete failed: " + e.getMessage());
            }
        }
    }

    private void showSuccessAlert(String title, String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}