/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import application.MoneyMateApplication;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Acount;
import model.AcountDAOException;
import model.Category;
import model.Charge;
import model.User;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class AddExpenseController implements Initializable {

    @FXML
    private TextField expenseTitle;
    @FXML
    private TextField expenseAmount;
    @FXML
    private TextArea expenseDescription;
    @FXML
    private ImageView profilePicture;
    @FXML
    private ChoiceBox<Category> chooseCategory;
    @FXML
    private Button addCategory;
    @FXML
    private Button addFile;
    @FXML
    private Button saveButton;
    @FXML
    private Label errorMessage;
    @FXML
    private DatePicker datapicker;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        saveButton.disableProperty().bind(
            expenseTitle.textProperty().isEmpty()
            .or(expenseAmount.textProperty().isEmpty())
        );
        try {
            User currentUser = Acount.getInstance().getLoggedUser();
            if (currentUser.getImage() != null) {
                profilePicture.setImage(currentUser.getImage());  
            }
        } catch (AcountDAOException | IOException ex) {
            Logger.getLogger(editUserController.class.getName()).log(Level.SEVERE, null, ex); 
        }
        // Attempt to load categories
        try {
            List<Category> categories = Acount.getInstance().getUserCategories(); // Fetch categories
            if (categories != null) {
                chooseCategory.getItems().clear(); // Clear existing items to avoid duplication on refreshes
                chooseCategory.getItems().addAll(categories); // Correctly add all fetched categories at once
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            errorMessage.setText("Failed to load categories.");
        }
    }
    
    
    @FXML
    private void pickDatepressed(ActionEvent event) {
    }

    @FXML
    private void addCategoryPressed(ActionEvent event) {
    // Dialog for category name
    TextInputDialog nameDialog = new TextInputDialog();
    nameDialog.setTitle("Add Category");
    nameDialog.setHeaderText("New Category");
    nameDialog.setContentText("Please enter the category name:");
    Optional<String> nameResult = nameDialog.showAndWait();

    // Dialog for category description
    TextInputDialog descDialog = new TextInputDialog();
    descDialog.setTitle("Add Category Description");
    descDialog.setHeaderText("New Category Description");
    descDialog.setContentText("Please enter the category description:");
    Optional<String> descResult = descDialog.showAndWait();

    if (nameResult.isPresent() && descResult.isPresent()) {
    String name = nameResult.get();
    String description = descResult.get();

    // Optionally save new category to database
    try {
        if (Acount.getInstance().registerCategory(name, description)) {
            System.out.println("New category added to database successfully.");

            // Fetch the list of categories from the database
            List<Category> categories = Acount.getInstance().getUserCategories();
            if (categories != null) {
                // Find the newly added category by name (assuming names are unique)
                Optional<Category> newCategory = categories.stream()
                    .filter(c -> c.getName().equals(name) && c.getDescription().equals(description))
                    .findFirst();

                // Check if the new category is found
                if (newCategory.isPresent()) {
                    // Add new category to the ChoiceBox for display.
                    chooseCategory.getItems().add(newCategory.get());
                    chooseCategory.setValue(newCategory.get()); // Set the newly added category as the selected one.
                } else {
                    System.out.println("New category registered but not found.");
                }
            }
        } else {
            System.out.println("Failed to add new category to database.");
        }
    } catch (Exception e) {
        System.out.println("Error saving new category: " + e.getMessage());
    }
}
    }
    @FXML
    private void addFilePressed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            profilePicture.setImage(image);  // Display the selected image
        }
    }

    @FXML
    private void saveButtonPressed(ActionEvent event) {
        try {
            String title = expenseTitle.getText();
            double amount = Double.parseDouble(expenseAmount.getText());
            String description = expenseDescription.getText();
            Category category = chooseCategory.getValue();
            Image image = profilePicture.getImage();
            LocalDate date = datapicker.getValue();   // This could also come from a DatePicker

            
            boolean saveSuccess = Acount.getInstance().registerCharge(title, description, amount, 1, image, date, category);  // This method needs to be implemented

            if (saveSuccess) {
                
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Dashboard-view.fxml"));
                Parent root = fxmlLoader.load();
                MoneyMateApplication.setRoot(root);
            } else {
                errorMessage.setText("Failed to save data. Please try again.");
            }
        } catch (Exception ex) {
            errorMessage.setText("An error occurred. Please check input data.");
            ex.printStackTrace();
        }
        
    
    }
    


}
