package tn.esprit.controllers.Courses;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Category;
import tn.esprit.entities.Courses;
import tn.esprit.entities.Level;
import tn.esprit.services.CategoryService;
import tn.esprit.services.CoursesService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML private Button allCoursesButton;
    @FXML private Button premiumCoursesButton;
    @FXML private Button popularCoursesButton;
    @FXML private HBox categoriesContainer;
    @FXML private FlowPane coursesContainer;
    @FXML private Label currentCategoryLabel;
    @FXML private Label courseCountLabel;
    @FXML private VBox mainView;
    @FXML private VBox addCourseView;
    @FXML private TextField courseTitleField;
    @FXML private TextArea courseDescriptionField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField tutorField;
    @FXML private CheckBox premiumCheckBox;
    @FXML private ListView<Level> levelsListView;
    @FXML private TextField levelNameField;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;
    @FXML private Button prevCategoryButton;
    @FXML private Button nextCategoryButton;
    @FXML private ScrollPane categoriesScrollPane;

    private final CoursesService coursesService = CoursesService.getInstance();
    private final CategoryService categoryService = CategoryService.getInstance();
    private final List<Level> tempLevels = new ArrayList<>();

    // Variables de pagination
    private int currentPage = 0;
    private final int itemsPerPage = 6;
    private int totalCourses = 0;
    private String currentFilter = "all";
    private Category currentCategory = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des labels optionnels
        if (currentCategoryLabel == null) {
            currentCategoryLabel = new Label();
        }
        if (courseCountLabel == null) {
            courseCountLabel = new Label();
        }

        loadCategories();
        loadAllCourses();
        initializeCategoryComboBox();

        prevCategoryButton.getStyleClass().add("nav-button");
        nextCategoryButton.getStyleClass().add("nav-button");

        categoriesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        categoriesScrollPane.setFitToHeight(true);
    }

    private void scrollCategories(int direction) {
        double scrollAmount = categoriesScrollPane.getHvalue();
        double newScrollAmount = scrollAmount + (direction * 0.2);
        newScrollAmount = Math.max(0, Math.min(1, newScrollAmount));
        categoriesScrollPane.setHvalue(newScrollAmount);

        prevCategoryButton.setDisable(newScrollAmount <= 0);
        nextCategoryButton.setDisable(newScrollAmount >= 1);
    }

    private void initializeCategoryComboBox() {
        categoryComboBox.getItems().addAll(categoryService.getAll());
    }

    private void loadCategories() {
        categoriesContainer.getChildren().clear();

        // Carte "Tous" avec icône image
        VBox allCard = new VBox(10);
        allCard.getStyleClass().add("category-card");
        allCard.setAlignment(Pos.CENTER);

        ImageView allIcon = new ImageView();
        try {
            allIcon.setImage(new Image(getClass().getResourceAsStream("/images/all-icon.jpg")));
        } catch (Exception e) {
            allIcon.setImage(getDefaultIcon());
        }
        allIcon.setFitWidth(80);
        allIcon.setFitHeight(80);
        allIcon.setPreserveRatio(true);
        allIcon.getStyleClass().add("category-icon");

        Label allLabel = new Label("Tous");
        allLabel.setStyle("-fx-font-weight: bold;");

        allCard.getChildren().addAll(allIcon, allLabel);
        allCard.setOnMouseClicked(e -> loadAllCourses());
        categoriesContainer.getChildren().add(allCard);

        // Cartes des catégories avec icônes images
        for (Category category : categoryService.getAll()) {
            VBox card = new VBox(10);
            card.getStyleClass().add("category-card");
            card.setAlignment(Pos.CENTER);

            ImageView icon = new ImageView();
            icon.getStyleClass().add("category-icon");
            try {
                if (category.getIcon() != null && !category.getIcon().isEmpty()) {
                    icon.setImage(new Image(category.getIcon()));
                } else {
                    icon.setImage(getDefaultIcon());
                }
            } catch (Exception e) {
                icon.setImage(getDefaultIcon());
            }
            icon.setFitWidth(80);
            icon.setFitHeight(80);
            icon.setPreserveRatio(true);

            Label name = new Label(category.getName());
            name.setStyle("-fx-font-weight: bold;");

            card.getChildren().addAll(icon, name);
            card.setOnMouseClicked(e -> loadCoursesByCategory(category));
            categoriesContainer.getChildren().add(card);
        }

        prevCategoryButton.setOnAction(e -> scrollCategories(-1));
        nextCategoryButton.setOnAction(e -> scrollCategories(1));
    }

    private Image getDefaultIcon() {
        try {
            return new Image(getClass().getResourceAsStream("/interfaces/Category/images/all-icon.jpg"));
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void loadAllCourses() {
        currentCategory = null;
        currentFilter = "all";
        currentPage = 0;
        totalCourses = coursesService.getCount();
        loadCoursesForCurrentPage();
        setButtonStyles("all");
    }

    @FXML
    private void loadPremiumCourses() {
        currentCategory = null;
        currentFilter = "premium";
        currentPage = 0;
        totalCourses = coursesService.getPremiumCount();
        loadCoursesForCurrentPage();
        setButtonStyles("premium");
    }

    @FXML
    private void loadPopularCourses() {
        currentCategory = null;
        currentFilter = "popular";
        currentPage = 0;
        totalCourses = coursesService.getPopularCount();
        loadCoursesForCurrentPage();
        setButtonStyles("popular");
    }

    @FXML
    private void loadCoursesByCategory(Category category) {
        currentCategory = category;
        currentFilter = "category";
        currentPage = 0;
        totalCourses = coursesService.getCountByCategory(category.getId());
        loadCoursesForCurrentPage();
        setButtonStyles("all");
    }

    private void loadCoursesForCurrentPage() {
        List<Courses> courses;
        String categoryText = "";

        switch (currentFilter) {
            case "premium":
                courses = coursesService.getPremiumCoursesPaginated(currentPage, itemsPerPage);
                categoryText = "Premium Courses";
                break;
            case "popular":
                courses = coursesService.getPopularCoursesPaginated(currentPage, itemsPerPage);
                categoryText = "Popular Courses";
                break;
            case "category":
                courses = coursesService.getByCategoryPaginated(currentCategory.getId(), currentPage, itemsPerPage);
                categoryText = currentCategory.getName() + " Courses";
                break;
            default:
                courses = coursesService.getAllPaginated(currentPage, itemsPerPage);
                categoryText = "All Courses";
                break;
        }

        if (currentCategoryLabel != null) {
            currentCategoryLabel.setText(categoryText);
        }

        displayCourses(courses);
        updatePageInfo();
    }

    private void displayCourses(List<Courses> courses) {
        coursesContainer.getChildren().clear();
        if (courseCountLabel != null) {
            courseCountLabel.setText(courses.size() + " of " + totalCourses + " courses");
        }

        for (Courses course : courses) {
            VBox courseCard = createCourseCard(course);
            coursesContainer.getChildren().add(courseCard);
        }
    }

    private VBox createCourseCard(Courses course) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #D8BFD8; -fx-border-radius: 10;");
        card.setPrefSize(250, 160);

        Label titleLabel = new Label(course.getTitle() != null ? course.getTitle() : "Untitled Course");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4B0082; -fx-wrap-text: true;");

        Label tutorLabel = new Label("By " + (course.getTutorName() != null ? course.getTutorName() : "Unknown"));
        tutorLabel.setStyle("-fx-text-fill: #9370DB; -fx-font-size: 12px;");

        HBox footer = new HBox(10);
        Label ratingLabel = new Label(String.format("★ %.1f", course.getAverageRating()));
        ratingLabel.setStyle("-fx-text-fill: #f39c12;");

        Label premiumLabel = new Label(course.getIsPremium() ? "PREMIUM" : "FREE");
        premiumLabel.setStyle(course.getIsPremium()
                ? "-fx-text-fill: #4B0082; -fx-font-weight: bold;"
                : "-fx-text-fill: #2ecc71; -fx-font-weight: bold;");

        footer.getChildren().addAll(ratingLabel, premiumLabel);
        card.getChildren().addAll(titleLabel, tutorLabel, footer);

        card.setOnMouseClicked(event -> openCourseDetails(course));

        return card;
    }

    // ... [Les autres méthodes restent identiques à votre version originale]
    @FXML
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadCoursesForCurrentPage();
        }
    }

    @FXML
    private void nextPage() {
        if ((currentPage + 1) * itemsPerPage < totalCourses) {
            currentPage++;
            loadCoursesForCurrentPage();
        }
    }

    private void updatePageInfo() {
        int totalPages = (int) Math.ceil((double) totalCourses / itemsPerPage);
        pageInfoLabel.setText("Page " + (currentPage + 1) + " of " + totalPages);

        prevPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable((currentPage + 1) * itemsPerPage >= totalCourses);
    }

    private void setButtonStyles(String activeButton) {
        String activeStyle = "-fx-background-color: #4B0082; -fx-text-fill: white; -fx-background-radius: 5;";
        String inactiveStyle = "-fx-background-color: #D8BFD8; -fx-text-fill: #4B0082; -fx-background-radius: 5;";

        allCoursesButton.setStyle(activeButton.equals("all") ? activeStyle : inactiveStyle);
        premiumCoursesButton.setStyle(activeButton.equals("premium") ? activeStyle : inactiveStyle);
        popularCoursesButton.setStyle(activeButton.equals("popular") ? activeStyle : inactiveStyle);
    }

    @FXML
    private void showAddCourseForm() {
        categoryComboBox.getItems().setAll(categoryService.getAll());
        levelsListView.getItems().clear();
        tempLevels.clear();

        mainView.setVisible(false);
        addCourseView.setVisible(true);
    }

    @FXML
    private void cancelAddCourse() {
        addCourseView.setVisible(false);
        mainView.setVisible(true);
    }

    @FXML
    private void addLevel() {
        String levelName = levelNameField.getText().trim();
        if (!levelName.isEmpty()) {
            Level level = new Level(levelName, null);
            tempLevels.add(level);
            levelsListView.getItems().add(level);
            levelNameField.clear();
        }
    }

    @FXML
    private void saveCourse() {
        if (courseTitleField.getText().trim().isEmpty() ||
                categoryComboBox.getValue() == null ||
                tutorField.getText().trim().isEmpty()) {
            showAlert("Error", "Please fill all required fields");
            return;
        }

        if (tempLevels.isEmpty()) {
            showAlert("Error", "Please add at least one level.");
            return;
        }

        Courses newCourse = new Courses();
        newCourse.setTitle(courseTitleField.getText());
        newCourse.setDescription(courseDescriptionField.getText());
        newCourse.setCategory(categoryComboBox.getValue());
        newCourse.setTutorName(tutorField.getText());
        newCourse.setIsPremium(premiumCheckBox.isSelected());

        for (Level level : tempLevels) {
            newCourse.addLevel(level);
        }

        coursesService.ajouter(newCourse);
        cancelAddCourse();
        loadAllCourses();
    }

    private void openCourseDetails(Courses course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Courses/CourseDetailsView.fxml"));
            Parent root = loader.load();

            CourseDetailsController controller = loader.getController();
            controller.setCourse(course);

            Stage stage = new Stage();
            stage.setTitle("Détails du Cours");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}