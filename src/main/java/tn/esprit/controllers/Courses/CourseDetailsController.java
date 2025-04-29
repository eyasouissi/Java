package tn.esprit.controllers.Courses;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import tn.esprit.entities.Courses;
import tn.esprit.entities.File;
import tn.esprit.entities.Level;
import tn.esprit.services.CoursesService;
import tn.esprit.services.FileService;
import tn.esprit.services.LevelService;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;


public class CourseDetailsController {

    // Composants FXML
    @FXML private Label titleLabel;
    @FXML private Label tutorLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label categoryLabel;
    @FXML private Label createdAtLabel;
    @FXML private Label premiumLabel;
    @FXML private VBox levelsContainer;
    @FXML private VBox addFilesContainer;
    @FXML private TextField fileNameField;
    @FXML private ListView<File> filesListView;
    @FXML private Label selectedLevelLabel;

    // Données
    private Courses course;
    private Level selectedLevel;

    // Services
    private final CoursesService coursesService = CoursesService.getInstance();
    private final FileService fileService = FileService.getInstance();
    private final LevelService levelService = LevelService.getInstance();

    // Initialise le cours avec ses données
    public void setCourse(Courses course) {
        // Charge le cours complet avec ses niveaux depuis la base
        Courses fullCourse = coursesService.getByIdWithLevels(course.getId());
        this.course = fullCourse;

        // Debug
        System.out.println("[DEBUG] Course loaded - ID: " + fullCourse.getId());
        System.out.println("[DEBUG] Levels count: " +
                (fullCourse.getLevels() != null ? fullCourse.getLevels().size() : "null"));

        updateCourseDetails();
        displayLevels();
    }

    // Met à jour les informations de base du cours
    private void updateCourseDetails() {
        if (course != null) {
            titleLabel.setText(course.getTitle());
            tutorLabel.setText("Par " + course.getTutorName());
            descriptionLabel.setText(course.getDescription());
            categoryLabel.setText("Catégorie : " +
                    (course.getCategory() != null ? course.getCategory().getName() : "Aucune"));
            createdAtLabel.setText("Créé le : " + course.getCreatedAt().toString());
            premiumLabel.setText(course.getIsPremium() ? "Premium" : "Gratuit");
        }
    }

    // Affiche les niveaux du cours
    private void displayLevels() {
        levelsContainer.getChildren().clear();

        if (course == null) {
            showNoLevelsMessage("Aucun cours chargé");
            return;
        }

        if (course.getLevels() == null || course.getLevels().isEmpty()) {
            showNoLevelsMessage("Aucun niveau disponible pour ce cours");
            return;
        }

        Accordion accordion = new Accordion();

        for (Level level : course.getLevels()) {
            TitledPane levelPane = createLevelPane(level);
            accordion.getPanes().add(levelPane);
        }

        levelsContainer.getChildren().add(accordion);
    }

    // Crée un panneau pour un niveau
    private TitledPane createLevelPane(Level level) {
        VBox levelContent = new VBox(10);
        levelContent.setPadding(new Insets(10));

        // En-tête avec boutons d'action
        HBox levelHeader = createLevelHeader(level);

        // Liste des fichiers
        VBox filesContainer = createFilesContainer(level);

        levelContent.getChildren().addAll(levelHeader, filesContainer);

        return new TitledPane("Niveau " + level.getName(), levelContent);
    }

    // Dans la méthode createLevelHeader (modifiez les couleurs des boutons) :
    private HBox createLevelHeader(Level level) {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Niveau: " + level.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #5d3a5d;");

        Button addFilesBtn = new Button("Ajouter Fichiers");
        styleButton(addFilesBtn, "#D8BFD8");
        addFilesBtn.setOnAction(e -> showAddFilesForm(level));

        Button editBtn = new Button("Modifier");
        styleButton(editBtn, "#b38cb3");
        editBtn.setOnAction(e -> editLevel(level));

        Button deleteBtn = new Button("Supprimer");
        styleButton(deleteBtn, "#a57ca5");
        deleteBtn.setOnAction(e -> deleteLevel(level));

        header.getChildren().addAll(nameLabel, addFilesBtn, editBtn, deleteBtn);
        return header;
    }

    // Dans la méthode createFileRow :
    private HBox createFileRow(File file, Level level) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 8; -fx-background-color: #f9f5fa; -fx-background-radius: 5;");

        Node icon = createFileIcon(file.getFileName());

        Label nameLabel = new Label(file.getFileName());
        nameLabel.setStyle("-fx-text-fill: #5d3a5d; -fx-font-weight: bold;");

        Button openBtn = new Button("Ouvrir");
        styleButton(openBtn, "#9d65a5", 12);
        openBtn.setOnAction(e -> openAndMarkFile(file, nameLabel));

        Button deleteBtn = new Button("Supprimer");
        styleButton(deleteBtn, "#a57ca5", 12);
        deleteBtn.setOnAction(e -> deleteFile(file, level));

        row.getChildren().addAll(icon, nameLabel, openBtn, deleteBtn);
        return row;
    }

    // Mettez à jour la méthode styleButton :
    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 5 15; " +
                "-fx-background-radius: 15;");
    }

    private void styleButton(Button button, String color, int fontSize) {
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: " + fontSize + "px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 3 10; " +
                "-fx-background-radius: 10;");
    }
    private VBox createFilesContainer(Level level) {
        VBox container = new VBox(5);
        Label title = new Label("Fichiers disponibles:");
        title.setStyle("-fx-font-weight: bold;");
        container.getChildren().add(title);

        if (level.getFiles() == null || level.getFiles().isEmpty()) {
            container.getChildren().add(
                    new Label("Aucun fichier disponible pour ce niveau"));
        } else {
            for (File file : level.getFiles()) {
                HBox fileRow = createFileRow(file, level);
                container.getChildren().add(fileRow);
            }
        }

        return container;
    }

    // Crée une ligne pour un fichier
  /*  private HBox createFileRow(File file, Level level) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 5;");

        Node icon = createFileIcon(file.getFileName());

        Label nameLabel = new Label(file.getFileName());
        nameLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");

        Button openBtn = new Button("Ouvrir");
        styleButton(openBtn, "#2ecc71", 12);
        openBtn.setOnAction(e -> openAndMarkFile(file, nameLabel));

        Button deleteBtn = new Button("Supprimer");
        styleButton(deleteBtn, "#e74c3c", 12);
        deleteBtn.setOnAction(e -> deleteFile(file, level));

        row.getChildren().addAll(icon, nameLabel, openBtn, deleteBtn);
        return row;
    }*/


    // Affiche le message quand il n'y a pas de niveaux
    private void showNoLevelsMessage(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #95a5a6;");
        levelsContainer.getChildren().add(label);
    }

    private void showAddFilesForm(Level level) {
        this.selectedLevel = level;
        selectedLevelLabel.setText("Ajout de fichiers - Niveau: " + level.getName());

        try {
            // Recharger explicitement les fichiers depuis la base
            List<File> files = fileService.getFilesForLevel(level.getId());

            // Mettre à jour la liste des fichiers du niveau
            level.setFiles(files);

            // Afficher dans la ListView
            filesListView.getItems().setAll(files);

            if (files.isEmpty()) {
                filesListView.setPlaceholder(new Label("Aucun fichier disponible pour ce niveau"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les fichiers");
            filesListView.getItems().clear();
            filesListView.setPlaceholder(new Label("Erreur de chargement des fichiers"));
        }

        addFilesContainer.setVisible(true);
    }



// Appelez cette méthode chaque fois que vous rouvrez la fenêtre

    @FXML
    private void hideAddFilesForm() {
        addFilesContainer.setVisible(false);
        fileNameField.clear();
    }

    @FXML
    private void handleFileUpload() {
        if (selectedLevel == null) {
            showAlert("Erreur", "Aucun niveau sélectionné !");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        java.io.File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // 1. Préparer l'upload
                String uploadPath = "C:/xampp/htdocs/uploads/";
                Path uploadDir = Paths.get(uploadPath);

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // 2. Créer un nom unique
                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destination = uploadDir.resolve(uniqueFileName);

                // 3. Copier
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // 4. Enregistrer en base
                File newFile = new File();
                newFile.setFileName(selectedFile.getName()); // nom original
                newFile.setFilePath(destination.toString()); // chemin complet
                newFile.setViewed(false);
                newFile.setLevel(selectedLevel);

                fileService.ajouter(newFile);

                // 5. Ajouter à la liste
                selectedLevel.getFiles().add(newFile);
                filesListView.getItems().add(newFile);
                displayLevels();

                showAlert("Succès", "Fichier ajouté avec succès !");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'uploader le fichier:\n" + e.getMessage());
            }
        }
    }

    @FXML
    private void addFile() {
        String fileName = fileNameField.getText().trim();
        if (fileName.isEmpty() || selectedLevel == null) return;

        // Créer le chemin complet comme dans handleFileUpload
        String uploadPath = "C:/xampp/htdocs/uploads/";
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        String destPath = uploadPath + uniqueFileName;

        File newFile = new File();
        newFile.setFileName(fileName);
        newFile.setFilePath(destPath); // ◄◄◄ N'OUBLIEZ PAS CECI !
        newFile.setLevel(selectedLevel);
        newFile.setViewed(false);

        try {
            fileService.ajouter(newFile);

            // Actualise l'affichage
            selectedLevel.getFiles().add(newFile);
            filesListView.getItems().add(newFile);
            fileNameField.clear();
            displayLevels();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteFile(File file, Level level) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le fichier");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce fichier ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Supprimer fichier physique
                    Path filePath = Paths.get(file.getFilePath());
                    Files.deleteIfExists(filePath);

                    // Supprimer base
                    fileService.supprimer((int) file.getId());
                    level.getFiles().remove(file);

                    // Refresh
                    displayLevels();
                    showAlert("Succès", "Fichier supprimé !");
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer le fichier:\n" + e.getMessage());
                }
            }
        });
    }

    // Gestion des niveaux
    private void editLevel(Level level) {
        TextInputDialog dialog = new TextInputDialog(level.getName());
        dialog.setTitle("Modifier le niveau");
        dialog.setHeaderText("Modification du nom du niveau");
        dialog.setContentText("Nouveau nom:");

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                level.setName(newName.trim());
                levelService.modifier(level);
                displayLevels();
            }
        });
    }

    private void deleteLevel(Level level) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le niveau");
        alert.setContentText("Ceci supprimera également tous les fichiers associés. Continuer ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Supprime d'abord les fichiers
                level.getFiles().forEach(file ->
                        fileService.supprimer((int) file.getId()));

                // Puis le niveau
                levelService.supprimer(level.getId());
                course.getLevels().remove(level);
                displayLevels();
            }
        });
    }

    private Node createFileIcon(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        ImageView icon = new ImageView();

        String iconPath;
        switch (extension) {
            case "pdf":
                iconPath = "/assets/icons/pdf-icon2.png";
                break;
            case "doc":
            case "docx":
                iconPath = "/assets/icons/word-icon.png";
                break;
            case "xls":
            case "xlsx":
                iconPath = "/assets/icons/excel-icon.png";
                break;
            case "ppt":
            case "pptx":
                iconPath = "/assets/icons/ppt-icon.png";
                break;
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                iconPath = "/assets/icons/image-icon.png";
                break;
            default:
                iconPath = "/assets/icons/file-icon.png";
                break;
        }

        try {
            icon.setImage(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(24);
            icon.setFitHeight(24);
        } catch (Exception e) {
            System.out.println("[ERROR] Icon not found at path: " + iconPath);
            // Retourner une icône par défaut si l'icône spécifique n'est pas trouvée
            return new Label(extension.toUpperCase());
        }

        return icon;
    }

    private void openAndMarkFile(File file, Label nameLabel) {
        try {
            Path filePath = Paths.get(file.getFilePath());
            if (Files.exists(filePath)) {
                Desktop.getDesktop().open(filePath.toFile());

                // Marquer comme vu
                file.setViewed(true);
                fileService.modifier(file);

                nameLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            } else {
                showAlert("Erreur", "Fichier introuvable !");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur d'ouverture du fichier:\n" + e.getMessage());
        }
    }






    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAddLevel() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau niveau");
        dialog.setHeaderText("Création d'un nouveau niveau");
        dialog.setContentText("Nom du niveau:");

        dialog.showAndWait().ifPresent(levelName -> {
            if (!levelName.trim().isEmpty()) {
                try {
                    Level newLevel = new Level();
                    newLevel.setName(levelName.trim());
                    newLevel.setCourse(course); // Associer le niveau au cours actuel

                    // Sauvegarder le niveau dans la base de données
                    levelService.ajouter(newLevel);

                    // Ajouter le niveau à la liste des niveaux du cours
                    course.getLevels().add(newLevel);

                    // Rafraîchir l'affichage
                    displayLevels();

                    showAlert("Succès", "Niveau ajouté avec succès !");
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Échec de l'ajout du niveau: " + e.getMessage());
                }
            }
        });
    }
}