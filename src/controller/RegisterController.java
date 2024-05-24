/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import application.MoneyMateApplication;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.io.File;
import java.lang.System.Logger;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import model.Persona;
import model.User;
import model.Acount;

/**
 * FXML Controller class
 *
 * @author jsoler
 * Modified carferl2
 */
public class RegisterController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextField userField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView profilePicture;

    @FXML
    private Button editPictureButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;
    @FXML
    private TextField surnameField;
    @FXML
    private Label wrongNickname;
    @FXML
    private Label wrongEmail;
    @FXML
    private Label wrongPassword;
    @FXML
    private Circle circle;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Default Image
        Image defaultImage = new Image("/Pictures/default.jpg");
        circle.setFill(new ImagePattern(defaultImage));
        
        
        // Button Bindings
        confirmButton.disableProperty().bind(
        nameField.textProperty().isEmpty()
        .or(surnameField.textProperty().isEmpty())
        .or(userField.textProperty().isEmpty())
        .or(emailField.textProperty().isEmpty())
        .or(passwordField.textProperty().isEmpty())
    );
    }   

    @FXML
    private void cancelClicked(ActionEvent event) {
        try {
            // Load the registration view
            FXMLLoader fxmlloader= new FXMLLoader(getClass().getResource("/view/hello-view.fxml"));
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
        String username = userField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        Image image = profilePicture.getImage();

        // Validate data
        if (!validateData(username, password, email)) {
            System.out.println("Invalid input. Please check your data and try again.");
            return;
        }
        
        boolean saveSuccess = Acount.getInstance().registerUser(name, surname, email, username, password, image, LocalDate.now());


        if (saveSuccess) {
            // Show success alert
            showAlert("Registration Successful", "You have been successfully signed up! Please log in to use the application.");
            
            // Data saved successfully, navigate back to login screen
            navigateToLoginScreen();
        } else {
            System.out.println("Failed to save data. Please try again.");
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("An error occurred. Please try again later.");
    }
}

private void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}

private void navigateToLoginScreen() {
    try {
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/hello-view.fxml"));
        Parent root = fxmlloader.load();
        MoneyMateApplication.setRoot(root);
    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging purposes.
        // Consider displaying an error message to the user or logging the error more formally.
    }
}


private boolean validateData(String nickname, String password, String email) {
    boolean isValid = true;
    // Validate password length
    if (!User.checkPassword(password)) {
        String errorPassword = "Password needs at least: \n"+
                              "8-20 characters, 1 special character,\n"+
                              "1 uppercase, 1 lowercase, and 1 number.";
        wrongPassword.setText(errorPassword);
        isValid = false;
    }
    else {
        wrongPassword.setText("");
    }

    // Validate email format
    if (!User.checkEmail(email)) {
        wrongEmail.setText("Enter a valid email address.");
        isValid = false;
    }
    else {
        wrongEmail.setText("");
    }

    // Check if the nickname already exists in the database
    if (!User.checkNickName(nickname)) {
        String errorNickname = "Nickname already taken \n" +
                                "Please choose another.";
        wrongNickname.setText(errorNickname);
        isValid = false;
    }
    else {
        wrongNickname.setText("");
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
                Image image = new Image(imagePath);
                //profilePicture.setImage(image); // Set the image in ImageView
                circle.setFill(new ImagePattern(image));
            } catch (MalformedURLException ex) {
                System.err.println("Error loading image: "+ ex.getMessage());
                // Handle exceptions possibly with a dialog
            }
        }
        
    }

    
}