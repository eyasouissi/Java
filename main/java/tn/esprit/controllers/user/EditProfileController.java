package tn.esprit.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;
import tn.esprit.tools.FileUploadUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EditProfileController {
    @FXML private ImageView profileImageView;
    @FXML private ImageView backgroundImageView;
    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private TextField countryField;
    @FXML private TextField specialityField;
    @FXML private TextArea bioArea;
    @FXML private Label diplomaFileNameLabel;

    private User currentUser;
    private File profileImageFile;
    private File backgroundImageFile;
    private File diplomaFile;

    public void setUserData(User user) {
        this.currentUser = user;
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            ageField.setText(currentUser.getAge() != null ? currentUser.getAge().toString() : "");
            countryField.setText(currentUser.getCountry() != null ? currentUser.getCountry() : "");
            specialityField.setText(currentUser.getSpeciality() != null ? currentUser.getSpeciality() : "");
            bioArea.setText(currentUser.getBio() != null ? currentUser.getBio() : "");

            // Load images
            loadImage(profileImageView, currentUser.getpfp(), "/assets/images/pfp/default-profile.png");
            loadImage(backgroundImageView, currentUser.getbg(), "/assets/images/bg/default-bg.jpg");

            // Set diploma file name if exists
            if (currentUser.getDiplome() != null && !currentUser.getDiplome().isEmpty()) {
                File file = FileUploadUtil.getUploadedFile(currentUser.getDiplome());
                diplomaFileNameLabel.setText(file != null && file.exists() ? file.getName() : "File not found");
            }
        }
    }

    private void loadImage(ImageView imageView, String path, String defaultPath) {
        try {
            if (path != null && !path.isEmpty()) {
                // Try as resource first
                InputStream is = getClass().getResourceAsStream("/" + path);
                if (is != null) {
                    imageView.setImage(new Image(is));
                    return;
                }

                // Try as uploaded file
                File file = FileUploadUtil.getUploadedFile(path);
                if (file != null && file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                    return;
                }
            }

            // Load default if specified path fails
            if (defaultPath != null) {
                InputStream defaultStream = getClass().getResourceAsStream(defaultPath);
                if (defaultStream != null) {
                    imageView.setImage(new Image(defaultStream));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangeProfilePicture() {
        File file = showFileChooser("Select Profile Picture", "*.png", "*.jpg", "*.jpeg");
        if (file != null) {
            profileImageFile = file;
            profileImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleChangeBackgroundImage() {
        File file = showFileChooser("Select Background Image", "*.png", "*.jpg", "*.jpeg");
        if (file != null) {
            backgroundImageFile = file;
            backgroundImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleUploadDiploma() {
        File file = showFileChooser("Select Diploma PDF", "*.pdf");
        if (file != null) {
            // Check file size
            if (file.length() > FileUploadUtil.MAX_FILE_SIZE) {
                showAlert("Error", "File size exceeds 10MB limit");
                return;
            }
            diplomaFile = file;
            diplomaFileNameLabel.setText(file.getName());
        }
    }

    private File showFileChooser(String title, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files", extensions));
        return fileChooser.showOpenDialog(diplomaFileNameLabel.getScene().getWindow());
    }

    @FXML
    private void handleSave() {
        try {
            // Validate and update fields
            currentUser.setName(nameField.getText().trim());

            if (!ageField.getText().isEmpty()) {
                currentUser.setAge(Integer.parseInt(ageField.getText()));
            } else {
                currentUser.setAge(null);
            }

            currentUser.setCountry(countryField.getText().trim());
            currentUser.setSpeciality(specialityField.getText().trim());
            currentUser.setBio(bioArea.getText().trim());

            // Handle file uploads
            if (profileImageFile != null) {
                String pfpPath = FileUploadUtil.uploadFile(profileImageFile, "pfp");
                currentUser.setpfp(pfpPath);
            }

            if (backgroundImageFile != null) {
                String bgPath = FileUploadUtil.uploadFile(backgroundImageFile, "bg");
                currentUser.setbg(bgPath);
            }

            if (diplomaFile != null) {
                String diplomaPath = FileUploadUtil.uploadFile(diplomaFile, "diplomas");
                currentUser.setDiplome(diplomaPath);
            }

            // Save to database
            UserService userService = new UserService();
            userService.modifier(currentUser);

            // Close window
            ((Stage) profileImageView.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showAlert("Invalid Age", "Please enter a valid number for age.");
        } catch (IOException e) {
            showAlert("Upload Error", "Failed to upload file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Failed to save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) profileImageView.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}