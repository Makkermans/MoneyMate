/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import application.MoneyMateApplication;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import model.Acount;
import model.AcountDAOException;
import model.User;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class editUserController implements Initializable {

    @FXML
    private Button cancelButton, confirmButton;
    @FXML
    private TextField nameField, userField, emailField, surnameField;
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label wrongEmail, Wrongpassword;

    private String username; // Declare username as a class member
    @FXML
    private Button editPictureButton;
    @FXML
    private Circle circleImage;
    private Image profilePicture;
    @FXML
    private MenuItem logoutButton;
    @FXML
    private TextField plainTextField;
    @FXML
    private CheckBox showPasswordCheckBox;
    @FXML
    private PasswordField passwordField2;
    @FXML
    private TextField plainTextField2;

    @Override
public void initialize(URL url, ResourceBundle rb) {
    try {
        // Assuming Acount.getInstance().getLoggedUser() correctly fetches the current logged-in user
        User currentUser = Acount.getInstance().getLoggedUser();
        if (currentUser != null) {
            this.username = currentUser.getNickName();
            // Set all user details into their respective fields
            nameField.setText(currentUser.getName());
            surnameField.setText(currentUser.getSurname());
            userField.setText(this.username); // Set the username in the TextField
            emailField.setText(currentUser.getEmail());
            passwordField.setText(currentUser.getPassword());
            passwordField2.setText(currentUser.getPassword());
            if (currentUser.getImage() != null) {
                profilePicture = currentUser.getImage();
                circleImage.setFill(new ImagePattern(profilePicture));
            }

            // Make the username field non-editable
            userField.setEditable(false);
        }
    } catch (AcountDAOException | IOException ex) {
        Logger.getLogger(editUserController.class.getName()).log(Level.SEVERE, null, ex);
        
    }
    plainTextField.setManaged(false);
    plainTextField.setVisible(false);
    plainTextField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
    plainTextField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());

    passwordField.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
    passwordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());

    // Bind the text properties together
    plainTextField.textProperty().bindBidirectional(passwordField.textProperty());
    
    // Second password
        plainTextField2.setManaged(false);
        plainTextField2.setVisible(false);
        plainTextField2.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        plainTextField2.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordField2.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordField2.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());
        plainTextField2.textProperty().bindBidirectional(passwordField2.textProperty());


    // Handle checkbox action
    showPasswordCheckBox.setOnAction(e -> handleCheckboxAction());
}

    

    @FXML
    private void cancelClicked(ActionEvent event) {
        try {
            // Load the registration view
            FXMLLoader fxmlloader= new FXMLLoader(getClass().getResource("/view/Dashboard-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging purposes.
            // Consider displaying an error message to the user or logging the error more formally.
        }
    }
    
    @FXML
private void applyClicked(ActionEvent event) {
    try {
        // Prepare new data
        String name = nameField.getText();
        String surname = surnameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        Image image = (profilePicture != null) ? profilePicture : new Image("/Pictures/default.jpg");

        // Validate data
        if (!validateData(password, email)) {
            System.out.println("Invalid input. Please check your data and try again.");
            return;
        }

        // Get current user
        User currentUser = Acount.getInstance().getLoggedUser();
        if (currentUser != null) {
            // Store old data for comparison
            String oldName = currentUser.getName();
            String oldSurname = currentUser.getSurname();
            String oldEmail = currentUser.getEmail();
            String oldPassword = currentUser.getPassword();
            Image oldImage = currentUser.getImage();

            // Confirmation Dialog
            boolean confirm = showConfirmationDialog(oldName, oldSurname, oldEmail, oldPassword, oldImage, name, surname, email, password, image);
            if (confirm) {
                // Update user details if confirmed
                currentUser.setName(name);
                currentUser.setSurname(surname);
                currentUser.setEmail(email);
                currentUser.setPassword(password);
                currentUser.setImage(image);
                navigateToLoginScreen();
            } else {
                System.out.println("Update canceled by user.");
            }
        } else {
            System.out.println("No user is currently logged in.");
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("An error occurred. Please try again later.");
    }
}

private boolean showConfirmationDialog(String oldName, String oldSurname, String oldEmail, String oldPassword, Image oldImage, String newName, String newSurname, String newEmail, String newPassword, Image newImage) {
    // Create an Alert or a custom Dialog to show changes and ask for user confirmation
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Changes");
    alert.setHeaderText("Please confirm your changes");

    // Construct the message showing old vs new data
    String contentText = "Please review your changes:\n\n" +
                         "Name: " + oldName + " -> " + newName + "\n" +
                         "Surname: " + oldSurname + " -> " + newSurname + "\n" +
                         "Email: " + oldEmail + " -> " + newEmail + "\n" +
                         "Password: [PROTECTED]" + "\n\n" +
                         "Click OK to confirm or Cancel to revert changes.";
    alert.setContentText(contentText);

    // Show dialog and wait for response
    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == ButtonType.OK;
}




private void navigateToLoginScreen() {
    try {
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/Dashboard-view.fxml"));
        Parent root = fxmlloader.load();
        MoneyMateApplication.setRoot(root);
    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging purposes.
        // Consider displaying an error message to the user or logging the error more formally.
    }
}


private boolean validateData(String password, String email) {
    boolean isValid = true;
    // Validate password length
    if (!User.checkPassword(password)) {
        String errorPassword = "Password needs at least: \n"+
                              "8-20 characters, 1 special character,\n"+
                              "1 uppercase, 1 lowercase, and 1 number.";
        Wrongpassword.setText(errorPassword);  
        isValid = false;
    }else if (!password.equals(passwordField2.getText())) { // Check if passwords match
        Wrongpassword.setText("Passwords do not match.");
        isValid = false;
    }
    else {
        Wrongpassword.setText("");
    }

    // Validate email format
    if (!User.checkEmail(email)) {
        wrongEmail.setText("Enter a valid email address.");
        isValid = false;
    }
    else {
        wrongEmail.setText("");
    }

    return isValid;
}

    @FXML
    private void editPictureClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        
        // Set filter to only allow images
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);
        // Set to the user's directory or go to default if cannot access
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);

        // Open the file chooser dialog
        File file = fileChooser.showOpenDialog(null);

        // Check if a file was selected
        if (file != null) {
            try {
                String imagePath = file.toURI().toURL().toString();
                profilePicture = new Image(imagePath);
                circleImage.setFill(new ImagePattern(profilePicture)); // Set the image in ImageView
            } catch (MalformedURLException ex) {
                System.err.println("Error loading image: "+ ex.getMessage());
                // Handle exceptions possibly with a dialog
            }
        }
    }

    @FXML
    private void logoutClicked(ActionEvent event) throws Exception{
        try {
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/hello-view.fxml"));
        Parent root = fxmlloader.load();
        MoneyMateApplication.setRoot(root);
    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging purposes.
        // Consider displaying an error message to the user or logging the error more formally.
    }
    }

    @FXML
    private void handleCheckboxAction() {
        if (showPasswordCheckBox.isSelected()) {
        plainTextField.requestFocus();
        plainTextField2.requestFocus();
    } else {
        passwordField.requestFocus();
        passwordField2.requestFocus();
    }
    }
    
}
