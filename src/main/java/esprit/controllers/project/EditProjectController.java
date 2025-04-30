package esprit.controllers.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import esprit.entities.project.Project;
import esprit.services.project.ProjectService;
import java.io.File;
import java.time.LocalDate;

public class EditProjectController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField difficulteField;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
    private TextField pdfField;

    @FXML
    private TextField imageField;

    @FXML
    private Button browsePdfButton;

    @FXML
    private Button browseImageButton;

    @FXML
    private Button updateButton;

    private Project selectedProject;

    private String pdfPath;
    private String imagePath;

    public void setProject(Project project) {
        this.selectedProject = project;
        titleField.setText(project.getTitre());
        descriptionArea.setText(project.getDescriptionProject());  // Mise à jour pour utiliser description_project
        difficulteField.setText(String.valueOf(project.getDifficulte()));
        deadlinePicker.setValue(project.getDeadline());
        pdfField.setText(project.getPdfFile());
        imageField.setText(project.getImage());
    }

    @FXML
    private void handleBrowsePdf(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(browsePdfButton.getScene().getWindow());
        if (selectedFile != null) {
            pdfPath = selectedFile.getAbsolutePath();
            pdfField.setText(pdfPath);
        }
    }

    @FXML
    private void handleBrowseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(browseImageButton.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            imageField.setText(imagePath);
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String difficulteStr = difficulteField.getText();
        Integer difficulte = Integer.valueOf(difficulteStr);

        LocalDate deadline = deadlinePicker.getValue();

        if (title.isEmpty() || description.isEmpty() || difficulte == null) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all required fields.");
            return;
        }

        selectedProject.setTitre(title);
        selectedProject.setDescriptionProject(description);
        selectedProject.setDifficulte(difficulte);
        selectedProject.setDeadline(deadline);
        selectedProject.setPdfFile(pdfPath);
        selectedProject.setImage(imagePath);

        try {
            ProjectService.getInstance().updateProject(selectedProject);
            showAlert(Alert.AlertType.INFORMATION, "Project updated successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error while updating project: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
