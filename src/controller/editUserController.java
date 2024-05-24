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
        // Example of collecting data from input fields
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

        boolean saveSuccess = Acount.getInstance().registerUser(name, surname, email, this.username, password, image, LocalDate.now());

        if (saveSuccess) {
            navigateToLoginScreen();
        } else {
            System.out.println("Failed to save data. Please try again.");
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("An error occurred. Please try again later.");
    }
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
        Wrongpassword.setText("Passwords must be 8-20 characters, include at least one uppercase & lowercase letter, one digit and one special character.");
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
    
}
