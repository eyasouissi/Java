package tn.esprit.controllers.Category;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.entities.Category;
import tn.esprit.services.CategoryService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategoriesController implements Initializable {

    @FXML
    private HBox categoriesCarousel;

    private List<Category> categories;
    private int currentIndex = 0;
    private final CategoryService categoryService = CategoryService.getInstance();
    private Timeline autoPlayTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
        if (categories != null && !categories.isEmpty()) {
            showCategories();
            setupAutoPlay();
        } else {
            showNoCategories();
        }
    }

    private void loadCategories() {
        categories = categoryService.getAll();
    }

    private void showNoCategories() {
        categoriesCarousel.getChildren().clear();
        Label noCategories = new Label("No categories available");
        noCategories.getStyleClass().add("no-categories-label");
        categoriesCarousel.getChildren().add(noCategories);
    }

    private void showCategories() {
        categoriesCarousel.getChildren().clear();

        // Show 3 categories at a time
        for (int i = 0; i < Math.min(3, categories.size()); i++) {
            int displayIndex = (currentIndex + i) % categories.size();
            Category category = categories.get(displayIndex);
            categoriesCarousel.getChildren().add(createCategoryCard(category));
        }
    }

    private VBox createCategoryCard(Category category) {
        VBox card = new VBox(10);
        card.getStyleClass().add("category-card");
        card.setMinWidth(300);
        card.setMaxWidth(300);

        // Image/Media
        ImageView imageView = new ImageView();
        imageView.setFitWidth(280);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("category-image");

        try {
            String imagePath = category.getIcon() != null ?
                    (category.getIcon().startsWith("/") ?
                            category.getIcon() :
                            "/assets/images/" + category.getIcon()) :
                    "/assets/images/default-category.png";

            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                imageView.setImage(new Image(imageUrl.toString()));
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/interfaces/Category/images/education-banner.jpg")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/interfaces/Category/images/education-banner.jpg")));
            } catch (Exception ex) {
                System.err.println("Error loading default image: " + ex.getMessage());
            }
        }

        // Card content
        Label title = new Label(category.getName() != null ? category.getName() : "Unnamed Category");
        title.getStyleClass().add("category-title");

        Label desc = new Label(category.getDescription() != null ? category.getDescription() : "No description available");
        desc.getStyleClass().add("category-desc");
        desc.setWrapText(true);

        Button viewBtn = new Button("View Courses", new FontIcon("fas-eye"));
        viewBtn.getStyleClass().add("view-button");

        card.getChildren().addAll(imageView, title, desc, viewBtn);
        return card;
    }

    private void setupAutoPlay() {
        autoPlayTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> nextCategory())
        );
        autoPlayTimeline.setCycleCount(Timeline.INDEFINITE);
        autoPlayTimeline.play();
    }

    @FXML
    private void prevCategory() {
        if (categories == null || categories.isEmpty()) return;

        currentIndex = (currentIndex - 1 + categories.size()) % categories.size();
        showCategories();
        resetAutoPlay();
    }

    @FXML
    private void nextCategory() {
        if (categories == null || categories.isEmpty()) return;

        currentIndex = (currentIndex + 1) % categories.size();
        showCategories();
        resetAutoPlay();
    }

    private void resetAutoPlay() {
        if (autoPlayTimeline != null) {
            autoPlayTimeline.stop();
            autoPlayTimeline.play();
        }
    }
}