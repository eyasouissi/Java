package tn.esprit.controllers.Courses;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Category;
import tn.esprit.entities.Courses;
import tn.esprit.services.CategoryService;
import tn.esprit.services.CoursesService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CoursesController {

    // Composants FXML
    @FXML private ListView<Courses> coursesListView;
    @FXML private HBox actionButtonsBox;
    @FXML private ComboBox<Category> categoryFilterCombo;
    @FXML private CheckBox premiumFilterCheck;

    // Services
    private final CoursesService coursesService = new CoursesService();
    private final CategoryService categoryService = new CategoryService();

    // Données
    private ObservableList<Courses> allCourses = FXCollections.observableArrayList();

    // Factory pour l'affichage personnalisé des cours
    private final Callback<ListView<Courses>, ListCell<Courses>> courseCellFactory = new Callback<>() {
        @Override
        public ListCell<Courses> call(ListView<Courses> param) {
            return new ListCell<>() {
                @Override
                protected void updateItem(Courses course, boolean empty) {
                    super.updateItem(course, empty);
                    if (empty || course == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        VBox box = new VBox(5);
                        Label title = new Label(course.getTitle());
                        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                        HBox details = new HBox(10);
                        details.getChildren().addAll(
                                new Label("Catégorie: " + course.getCategory().getName()),
                                new Label("Tuteur: " + course.getTutorName()),
                                new Label("Points: " + course.getProgressPointsRequired()),
                                new Label(course.getIsPremium() ? "⭐ Premium" : "🆓 Gratuit")
                        );
                        details.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

                        box.getChildren().addAll(title, details);
                        setGraphic(box);
                    }
                }
            };
        }
    };

    @FXML
    private void initialize() {
        setupListView();
        setupFilters();
        loadAllCourses();
    }

    private void setupListView() {
        coursesListView.setCellFactory(courseCellFactory);
        coursesListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> actionButtonsBox.setVisible(newVal != null)
        );
    }

    private void setupFilters() {
        // Initialisation des catégories avec affichage du nom seulement
        categoryFilterCombo.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });

        // Aussi pour l'affichage dans la liste déroulante
        categoryFilterCombo.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });

        categoryFilterCombo.getItems().setAll(categoryService.getAll());
        categoryFilterCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> filterCourses()
        );

        premiumFilterCheck.selectedProperty().addListener(
                (obs, oldVal, newVal) -> filterCourses()
        );
    }

    private void loadAllCourses() {
        allCourses.setAll(coursesService.getAll());
        coursesListView.setItems(allCourses);
    }

    private void filterCourses() {
        List<Courses> filtered = allCourses.stream()
                .filter(course ->
                        categoryFilterCombo.getValue() == null ||
                                course.getCategory().equals(categoryFilterCombo.getValue()))
                .filter(course ->
                        !premiumFilterCheck.isSelected() ||
                                course.getIsPremium())
                .collect(Collectors.toList());

        coursesListView.setItems(FXCollections.observableArrayList(filtered));
    }
    // Méthodes des boutons
    @FXML
    private void showAddCourseView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Courses/AddCourseView.fxml"));
            Parent root = loader.load();
            AddCourseController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un nouveau cours");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showListCourses() {
        loadAllCourses();
        showAlert("Information", "Liste des cours rafraîchie", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void showPublishedCourses() {
        List<Courses> publishedCourses = allCourses.stream()
                .filter(Courses::getIsPublished)
                .collect(Collectors.toList());
        coursesListView.setItems(FXCollections.observableArrayList(publishedCourses));
    }

    @FXML
    private void showEditCourseView() {
        Courses selected = coursesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Courses/EditCourseView.fxml"));
                Parent root = loader.load();
                EditCourseController controller = loader.getController();
                controller.setCourse(selected);
                controller.setParentController(this);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier le cours");
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void deleteCourse() {
        Courses selected = coursesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Supprimer ce cours ?");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer '" + selected.getTitle() + "' ?");

            if (confirmation.showAndWait().get() == ButtonType.OK) {
                coursesService.supprimer(selected.getId());
                loadAllCourses();
                showAlert("Succès", "Cours supprimé avec succès", Alert.AlertType.INFORMATION);
            }
        }
    }

    @FXML
    private void showCourseDetails() {
        Courses selected = coursesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Détails du cours");
            info.setHeaderText(selected.getTitle());
            info.setContentText(
                    "Description: " + selected.getDescription() + "\n\n" +
                            "Catégorie: " + selected.getCategory().getName() + "\n" +
                            "Tuteur: " + selected.getTutorName() + "\n" +
                            "Points requis: " + selected.getProgressPointsRequired() + "\n" +
                            "Statut: " + (selected.getIsPublished() ? "Publié" : "Non publié") + "\n" +
                            "Type: " + (selected.getIsPremium() ? "Premium" : "Gratuit")
            );
            info.showAndWait();
        }
    }

    // Méthode pour rafraîchir la liste depuis d'autres contrôleurs
    public void refreshCoursesList() {
        loadAllCourses();
    }


    @FXML
    private void navigateToCoursesView() {
        try {
            // Charger la vue des cours
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Category/CategoryView.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Scene currentScene = coursesListView.getScene();

            // Remplacer le contenu de la scène
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page des cours", Alert.AlertType.ERROR);
        }
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}