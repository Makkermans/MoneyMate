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
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Persona;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
                profilePicture.setImage(image); // Set the image in ImageView
            } catch (MalformedURLException ex) {
                System.err.println("Error loading image: "+ ex.getMessage());
                // Handle exceptions possibly with a dialog
            }
        }
        
    }

    
}
