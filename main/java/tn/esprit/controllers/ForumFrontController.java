package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Callback;
import tn.esprit.entities.Forum;
import tn.esprit.tools.MyDataBase;

import java.net.URL;
import java.sql.*;
import java.util.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import java.io.IOException;

public class ForumFrontController implements Initializable {

    @FXML private ListView<Forum> forumListView;
    @FXML private TextField searchField;
    @FXML private FlowPane topicsFlowPane;
    @FXML private Text totalPostsText;
    @FXML private Text totalLikesText;
    @FXML private VBox sidebar;

    private ObservableList<Forum> forumList = FXCollections.observableArrayList();
    private FilteredList<Forum> filteredForums;
    private Set<String> allTopics = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadForums();
        setupForumListView();
        loadPopularTopics();
        setupForumClick();
    }

    private void loadPopularTopics() {
        topicsFlowPane.getChildren().clear();
        for (String topic : allTopics) {
            Label bubble = createTopicBubble(topic);
            topicsFlowPane.getChildren().add(bubble);
        }
    }

    private void loadForums() {
        String sql = "SELECT * FROM forum WHERE is_public = true";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Forum forum = new Forum();
                forum.setId(rs.getLong("id"));
                forum.setTitle(rs.getString("title"));
                forum.setDescription(rs.getString("description"));
                forum.setTopics(rs.getString("topics"));
                forum.setViews(rs.getInt("views"));
                forum.setTotalPosts(rs.getInt("totalposts"));

                forumList.add(forum);
                extractTopics(forum);
            }

            filteredForums = new FilteredList<>(forumList);
            forumListView.setItems(filteredForums);

        } catch (SQLException e) {
            showAlert("Database Error", "Error loading forums: " + e.getMessage());
        }
    }

    private void extractTopics(Forum forum) {
        if (forum.getTopics() != null) {
            Arrays.stream(forum.getTopics().split(","))
                    .map(String::trim)
                    .filter(topic -> !topic.isEmpty())
                    .forEach(allTopics::add);
        }
    }

    private void setupForumListView() {
        forumListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Forum> call(ListView<Forum> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Forum forum, boolean empty) {
                        super.updateItem(forum, empty);
                        if (empty || forum == null) {
                            setGraphic(null);
                        } else {
                            VBox container = new VBox(10);
                            container.getStyleClass().add("forum-container");

                            // Title with icon
                            HBox titleBox = new HBox(5);
                            Text title = new Text(forum.getTitle());
                            title.getStyleClass().add("forum-title");
                            titleBox.getChildren().addAll(createIcon("M12 3C6.48 3 2 6.48 2 10s4.48 7 10 7c2.44 0 4.7-.88 6.44-2.34L22 20l-2.67-6.67C18.7 12.88 20 10.44 20 10c0-3.52-4.48-7-8-7zm0 12c-3.31 0-6-2.69-6-6s2.69-6 6-6s6 2.69 6 6s-2.69 6-6 6z"), title);

                            // HTML Description - using WebView with proper styling
                            WebView descriptionWebView = new WebView();
                            WebEngine webEngine = descriptionWebView.getEngine();
                            descriptionWebView.setPrefHeight(100);
                            descriptionWebView.setPrefWidth(forumListView.getWidth() - 30);
                            descriptionWebView.setContextMenuEnabled(false);

                            // Create HTML content with styles that match your JavaFX CSS
                            String wrappedContent = "<!DOCTYPE html><html><head>" +
                                    "<style>" +
                                    "body { " +
                                    "   margin: 0; padding: 0; " +
                                    "   font-family: 'Segoe UI', Arial, sans-serif; " +
                                    "   color: #333333; " +
                                    "   background-color: transparent; " +
                                    "   line-height: 1.5; " +
                                    "}" +
                                    "h1 { font-size: 1.2em; color: #2c3e50; margin: 5px 0; }" +
                                    "h2 { font-size: 1.1em; color: #2c3e50; margin: 5px 0; }" +
                                    "p { margin: 5px 0; }" +
                                    "strong { font-weight: bold; }" +
                                    "em { font-style: italic; }" +
                                    "ul, ol { margin: 5px 0; padding-left: 20px; }" +
                                    "a { color: #3498db; text-decoration: none; }" +
                                    "</style>" +
                                    "</head><body>" +
                                    forum.getDescription() +
                                    "</body></html>";

                            webEngine.loadContent(wrappedContent);

                            // Topics
                            HBox topicsBox = new HBox(5);
                            if (forum.getTopics() != null) {
                                Arrays.stream(forum.getTopics().split(","))
                                        .map(String::trim)
                                        .forEach(topic -> topicsBox.getChildren().add(createTopicBubble(topic)));
                            }

                            // Stats
                            HBox statsBox = new HBox(10);
                            Region spacer = new Region();
                            HBox.setHgrow(spacer, Priority.ALWAYS);

                            statsBox.getChildren().addAll(
                                    spacer,
                                    createStatItem("M16 8s-3 5-8 5-8-5-8-5 3-5 8-5 8 5 8 5z", forum.getViews() + " Views"),
                                    createStatItem("M14 1H2a1 1 0 0 0-1 1v11a1 1 0 0 0 1 1h10l3 3V2a1 1 0 0 0-1-1z", forum.getTotalPosts() + " Posts")
                            );

                            container.getChildren().addAll(titleBox, descriptionWebView, topicsBox, statsBox);
                            setGraphic(container);
                        }
                    }
                };
            }
        });
    }

    private SVGPath createIcon(String pathData) {
        SVGPath icon = new SVGPath();
        icon.setContent(pathData);
        icon.getStyleClass().add("forum-icon");
        return icon;
    }

    private Label createTopicBubble(String topic) {
        Label bubble = new Label(topic);
        bubble.getStyleClass().add("topic-bubble");
        return bubble;
    }

    private HBox createStatItem(String iconPath, String text) {
        HBox box = new HBox(5);
        box.getChildren().addAll(createIcon(iconPath), new Text(text));
        box.getStyleClass().add("stat-item");
        return box;
    }

    private void setupForumClick() {
        forumListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Forum selectedForum = forumListView.getSelectionModel().getSelectedItem();
                if (selectedForum != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/PostView.fxml"));
                        Parent root = loader.load();

                        PostController controller = loader.getController();
                        controller.setForum(selectedForum);

                        Stage currentStage = (Stage) forumListView.getScene().getWindow();
                        currentStage.setTitle("Posts in " + selectedForum.getTitle());
                        currentStage.setScene(new Scene(root));

                        // Increment views count
                        selectedForum.incrementViews();
                        MyDataBase.getInstance().updateForumViews(selectedForum.getId(), selectedForum.getViews());

                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Load Error", "Could not load PostView.fxml: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}