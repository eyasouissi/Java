package tn.esprit.controllers.Category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.entities.Category;
import tn.esprit.services.CategoryService;

import java.io.IOException;
import java.util.List;

public class CategoryController {

    @FXML private ListView<Category> categoryListView;
    @FXML private TextField searchField;

    private final CategoryService categoryService = CategoryService.getInstance();
    private ObservableList<Category> originalList;

    @FXML
    public void initialize() {
        setupListView();
        setupSearch();
        loadCategories();
    }

    private void setupListView() {
        categoryListView.setCellFactory(param -> new ListCell<Category>() {
            private final HBox container = new HBox(10);
            private final FontIcon icon = new FontIcon("fas-folder-open");
            private final VBox textContainer = new VBox(3);
            private final Label nameLabel = new Label();
            private final Label descLabel = new Label();
            private final Button detailsButton = new Button("Détails");
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttonBox = new HBox(5);

            {
                // Configuration du layout
                container.setAlignment(Pos.CENTER_LEFT);
                container.setStyle("-fx-padding: 10;");

                // Style des éléments
                icon.setIconSize(24);
                icon.setIconColor(Color.web("#8c84a1"));

                nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
                descLabel.setMaxWidth(300);
                descLabel.setWrapText(true);

                // Style des boutons
                detailsButton.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white; -fx-padding: 5 10;");
                editButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-padding: 5 10;");
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-padding: 5 10;");

                // Tooltips
                Tooltip.install(detailsButton, new Tooltip("Voir les détails complets"));
                Tooltip.install(editButton, new Tooltip("Modifier cette catégorie"));
                Tooltip.install(deleteButton, new Tooltip("Supprimer cette catégorie"));

                // Actions des boutons
                detailsButton.setOnAction(event -> showCategoryDetails(getItem()));
                editButton.setOnAction(event -> handleEditCategory(getItem()));
                deleteButton.setOnAction(event -> handleDeleteCategory(getItem()));

                // Construction du layout
                textContainer.getChildren().addAll(nameLabel, descLabel);
                buttonBox.getChildren().addAll(detailsButton, editButton, deleteButton);
                container.getChildren().addAll(icon, textContainer, new Region(), buttonBox);
                HBox.setHgrow(textContainer, Priority.ALWAYS);
            }

            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getName());
                    descLabel.setText(item.getDescription());
                    setGraphic(container);
                }
            }
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                categoryListView.setItems(originalList);
            } else {
                FilteredList<Category> filteredList = new FilteredList<>(originalList);
                filteredList.setPredicate(category ->
                        category.getName().toLowerCase().contains(newVal.toLowerCase()) ||
                                category.getDescription().toLowerCase().contains(newVal.toLowerCase())
                );
                categoryListView.setItems(filteredList);
            }
        });
    }

    public void loadCategories() {
        try {
            List<Category> categories = categoryService.getAll();
            originalList = FXCollections.observableArrayList(categories);
            categoryListView.setItems(originalList);
        } catch (Exception e) {
            showAlert("Erreur", "Chargement des catégories", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Category/AddCategoryView.fxml"));
            Parent root = loader.load();

            AddCategoryController controller = loader.getController();
            controller.setCategoryController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Catégorie");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadCategories();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de chargement", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleEditCategory(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Category/EditCategoryView.fxml"));
            Parent root = loader.load();

            EditCategoryController controller = loader.getController();
            controller.setCategory(category);
            controller.setCategoryController(this);

            Stage stage = new Stage();
            stage.setTitle("Modifier la Catégorie");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadCategories();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de chargement", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleDeleteCategory(Category category) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la catégorie");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la catégorie '" + category.getName() + "'?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    categoryService.supprimer(category.getId());
                    showAlert("Succès", "Catégorie supprimée",
                            "La catégorie a été supprimée avec succès.",
                            Alert.AlertType.INFORMATION);
                    loadCategories();
                } catch (Exception e) {
                    showAlert("Erreur", "Suppression échouée", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showCategoryDetails(Category category) {
        Stage detailsStage = new Stage();
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 10;");

        // Titre centré
        Label title = new Label("Détails de la catégorie");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #8c84a1;");
        title.setPadding(new Insets(0, 0, 10, 0));

        // Conteneur principal (image + détails)
        HBox contentBox = new HBox(30);
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Partie image
        ImageView iconView = new ImageView();
        try {
            Image icon = new Image(getClass().getResourceAsStream(category.getIcon()));
            iconView.setImage(icon);
        } catch (Exception e) {
            iconView.setImage(new Image(getClass().getResourceAsStream("/interfaces/Category/images/default-icon.png")));
        }
        iconView.setFitWidth(120);
        iconView.setFitHeight(120);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);
        iconView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        VBox imageBox = new VBox(10, new Label("Icône:"), iconView);
        imageBox.setAlignment(Pos.CENTER);

        // Partie détails
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);
        detailsGrid.setPadding(new Insets(10));
        detailsGrid.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 15;");

        addDetailRow(detailsGrid, 0, "ID:", String.valueOf(category.getId()));
        addDetailRow(detailsGrid, 1, "Nom:", category.getName());
        addDetailRow(detailsGrid, 2, "Description:", category.getDescription());
        addDetailRow(detailsGrid, 3, "Statut:", category.getIsActive() ? "✅ Active" : "❌ Inactive");
        addDetailRow(detailsGrid, 4, "Date création:", category.getCreatedAt().toString());

        contentBox.getChildren().addAll(imageBox, detailsGrid);
        root.getChildren().addAll(title, new Separator(), contentBox);

        // Configuration de la fenêtre
        Scene scene = new Scene(root, 550, 300);
        detailsStage.setScene(scene);
        detailsStage.setTitle("Détails catégorie - " + category.getName());
        detailsStage.initModality(Modality.APPLICATION_MODAL);

        // Permet de fermer avec ESC
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) detailsStage.close();
        });

        detailsStage.show();
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        grid.add(lbl, 0, row);

        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #333;");
        val.setWrapText(true);
        grid.add(val, 1, row);
    }

    @FXML
    private void navigateToCoursesView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/interfaces/Courses/CoursesView.fxml"));
            Stage stage = (Stage) categoryListView.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            stage.setScene(new Scene(root, width, height));
        } catch (IOException e) {
            showAlert("Erreur", "Navigation impossible", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}