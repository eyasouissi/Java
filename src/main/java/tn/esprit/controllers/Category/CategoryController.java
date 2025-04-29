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
import javafx.scene.input.MouseEvent;
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
    @FXML private Button addButton;
   // @FXML private Button editButton;

    private final CategoryService categoryService = CategoryService.getInstance();
    private ObservableList<Category> originalList;

    @FXML
    public void initialize() {
        setupUI();
        setupListView();
        setupSearch();
        loadCategories();


    }

    private void setupUI() {
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setGraphic(new FontIcon("fas-plus"));
    }

    private void setupListView() {
        categoryListView.setCellFactory(param -> new ListCell<Category>() {
            private final HBox container = new HBox(10);
            private final ImageView iconView = new ImageView();
            private final VBox textContainer = new VBox(3);
            private final Label nameLabel = new Label();
            private final Label descLabel = new Label();
            private final HBox buttonBox = new HBox(5);

            {
                // Setup container
                container.setAlignment(Pos.CENTER_LEFT);
                container.setPadding(new Insets(10));
                container.setStyle("-fx-background-radius: 5;");

                // Setup icon
                iconView.setFitWidth(32);
                iconView.setFitHeight(32);
                iconView.setPreserveRatio(true);
                iconView.getStyleClass().add("clickable-icon");

                // Setup labels
                nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
                descLabel.setMaxWidth(300);
                descLabel.setWrapText(true);

                // Setup buttons
                Button detailsBtn = createActionButton("Détails", "fas-info", "#2196F3");
                Button editBtn = createActionButton("Modifier", "fas-edit", "#FFC107");
                Button deleteBtn = createActionButton("Supprimer", "fas-trash", "#F44336");

                detailsBtn.setOnAction(event -> showCategoryDetails(getItem()));
                editBtn.setOnAction(event -> handleEditCategory(getItem()));
                deleteBtn.setOnAction(event -> handleDeleteCategory(getItem()));

                buttonBox.getChildren().addAll(detailsBtn, editBtn, deleteBtn);
                textContainer.getChildren().addAll(nameLabel, descLabel);
                container.getChildren().addAll(iconView, textContainer, new Region(), buttonBox);
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
                    loadIcon(item.getIcon());
                    setGraphic(container);
                }
            }

            private void loadIcon(String iconUrl) {
                try {
                    if (iconUrl != null && !iconUrl.isEmpty()) {
                        Image image = new Image(iconUrl, true);
                        image.errorProperty().addListener((obs, wasError, isNowError) -> {
                            if (isNowError) {
                                iconView.setImage(getDefaultIcon());
                                iconView.setOnMouseClicked(e -> handleDefaultIconClick(e));
                            }
                        });
                        iconView.setImage(image);
                        iconView.setOnMouseClicked(e -> showFullScreenImage(iconUrl));
                    } else {
                        iconView.setImage(getDefaultIcon());
                        iconView.setOnMouseClicked(e -> handleDefaultIconClick(e));
                    }
                } catch (Exception ex) {
                    iconView.setImage(getDefaultIcon());
                    iconView.setOnMouseClicked(e -> handleDefaultIconClick(e));
                }
            }

            private Image getDefaultIcon() {
                return new Image(getClass().getResourceAsStream("/interfaces/Category/images/default-icon.png"));
            }
        });
    }

    @FXML
    private void handleDefaultIconClick(MouseEvent event) {
        showFullScreenImage(getClass().getResource("/interfaces/Category/images/default-icon.png").toString());
    }

    private Button createActionButton(String text, String iconCode, String color) {
        Button button = new Button(text);
        button.setGraphic(new FontIcon(iconCode));
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-padding: 5 10;",
                color,
                color.equals("#FFC107") ? "black" : "white"
        ));
        return button;
    }

    private void setupSearch() {
        searchField.setPromptText("Rechercher...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                categoryListView.setItems(originalList);
            } else {
                FilteredList<Category> filteredList = new FilteredList<>(originalList);
                filteredList.setPredicate(category ->
                        category.getName().toLowerCase().contains(newVal.toLowerCase()) ||
                                (category.getDescription() != null &&
                                        category.getDescription().toLowerCase().contains(newVal.toLowerCase()))
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
            showAlert("Erreur", "Erreur de chargement", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showCategoryDetails(Category category) {
        Stage detailsStage = new Stage();
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.setTitle("Détails de la Catégorie");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label titleLabel = new Label("Détails de la Catégorie");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);
        detailsGrid.setPadding(new Insets(15));
        detailsGrid.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        addDetailRow(detailsGrid, 0, "Nom:", category.getName());
        addDetailRow(detailsGrid, 1, "Description:",
                category.getDescription() != null ? category.getDescription() : "Non spécifiée");
        addDetailRow(detailsGrid, 2, "Statut:", category.getIsActive() ? "Activée" : "Désactivée");
        addDetailRow(detailsGrid, 3, "Date de création:", category.getCreatedAt().toString());
        addDetailRow(detailsGrid, 4, "Nombre de cours:", String.valueOf(category.getCourseCount()));

        if (category.getIcon() != null && !category.getIcon().isEmpty()) {
            try {
                ImageView iconView = new ImageView(new Image(category.getIcon()));
                iconView.setFitWidth(100);
                iconView.setFitHeight(100);
                iconView.setPreserveRatio(true);
                iconView.getStyleClass().add("clickable-icon");
                iconView.setOnMouseClicked(e -> showFullScreenImage(category.getIcon()));

                detailsGrid.add(new Label("Icône:"), 0, 5);
                detailsGrid.add(iconView, 1, 5);
            } catch (Exception e) {
                detailsGrid.add(new Label("Icône: (erreur de chargement)"), 0, 5);
            }
        }

        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #607d8b; -fx-text-fill: white;");
        closeButton.setOnAction(e -> detailsStage.close());

        root.getChildren().addAll(titleLabel, detailsGrid, closeButton);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 500, 400);
        detailsStage.setScene(scene);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                detailsStage.close();
            }
        });

        detailsStage.showAndWait();
    }

    private void showFullScreenImage(String imageUrl) {
        Stage fullScreenStage = new Stage();
        fullScreenStage.initModality(Modality.APPLICATION_MODAL);
        fullScreenStage.setTitle("Icône en plein écran");
        fullScreenStage.setFullScreen(true);
        fullScreenStage.setFullScreenExitHint("Appuyez sur ESC pour quitter le mode plein écran");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");

        ImageView fullImageView = new ImageView(new Image(imageUrl));
        fullImageView.setPreserveRatio(true);
        fullImageView.fitWidthProperty().bind(root.widthProperty());
        fullImageView.fitHeightProperty().bind(root.heightProperty());

        root.getChildren().add(fullImageView);

        Scene scene = new Scene(root, Color.BLACK);
        fullScreenStage.setScene(scene);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                fullScreenStage.close();
            }
        });

        fullScreenStage.showAndWait();
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold;");
        grid.add(lbl, 0, row);

        Label val = new Label(value);
        val.setWrapText(true);
        grid.add(val, 1, row);
        GridPane.setHgrow(val, Priority.ALWAYS);
    }

    @FXML
    private void handleAddCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Category/AddCategoryView.fxml"));            Parent root = loader.load();

            AddCategoryController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Nouvelle catégorie");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadCategories();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleEditCategory(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Category/EditCategoryView.fxml"));
            Parent root = loader.load(); // Ajoutez cette ligne pour charger le FXML

            EditCategoryController controller = loader.getController();
            controller.setCategory(category);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root)); // Utilisez la variable root déclarée ci-dessus
            stage.setTitle("Modifier catégorie");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadCategories();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        categoryListView.setItems(originalList);
    }

    private void handleDeleteCategory(Category category) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la catégorie '" + category.getName() + "' ?");
        confirmation.setContentText("Cette action est irréversible.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    categoryService.supprimer(category.getId());
                    showAlert("Succès", "Catégorie supprimée", "La catégorie a été supprimée avec succès.", Alert.AlertType.INFORMATION);
                    loadCategories();
                } catch (Exception e) {
                    showAlert("Erreur", "Échec de la suppression", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleEditCategory() {
        Category selected = categoryListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            handleEditCategory(selected);
        } else {
            showAlert("Erreur", "Aucune sélection", "Veuillez sélectionner une catégorie à modifier", Alert.AlertType.WARNING);
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