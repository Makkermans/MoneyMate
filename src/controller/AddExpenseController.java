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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Acount;
import model.AcountDAOException;
import model.Category;
import model.Charge;
import model.User;

/**
 * FXML Controller class Controller class for the login interface in the
 * MoneyMateApplication. Handles user authentication and navigation to the
 * registration view.
 *
 * @author Dante De Meyer & Thomas Vanderstraeten
 */
public class AddExpenseController implements Initializable {

    @FXML
    private TextField expenseTitle;
    @FXML
    private TextField expenseAmount;
    @FXML
    private TextArea expenseDescription;
    @FXML
    private ComboBox<Category> chooseCategory;
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
    @FXML
    private TextField expenseUnit;
    @FXML
    private Button cancelButton;
    @FXML
    private Circle circleImage;
    @FXML
    private Label nameAddExpense;
    private String username;
    @FXML
    private ImageView receiptPicture;

    @FXML
    private Button removeCatgory;
    @FXML
    private MenuItem SignOff;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        saveButton.disableProperty().bind(
                expenseTitle.textProperty().isEmpty()
                        .or(expenseAmount.textProperty().isEmpty())
                        .or(expenseUnit.textProperty().isEmpty())
                        .or(datapicker.valueProperty().isNull())
                        .or(chooseCategory.valueProperty().isNull())
        );

        try {
            User currentUser = Acount.getInstance().getLoggedUser();
            if (currentUser != null) {
                this.username = currentUser.getNickName();
                nameAddExpense.setText(this.username);
                if (currentUser.getImage() != null) {
                    circleImage.setFill(new ImagePattern(currentUser.getImage()));
                }
            }
        } catch (AcountDAOException | IOException ex) {
            //Logger.getLogger(EditUserController.class.getName()).log(Level.SEVERE, null, ex);
            errorMessage.setText("Failed to load user details.");
        }

        chooseCategory.setCellFactory(lv -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        chooseCategory.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        try {
            List<Category> categories = Acount.getInstance().getUserCategories();
            if (categories != null) {
                chooseCategory.getItems().clear();
                chooseCategory.getItems().addAll(categories);
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            errorMessage.setText("Failed to load categories.");
        }
    }

    @FXML
    private void addCategoryPressed(ActionEvent event) {
        TextInputDialog nameDialog = new TextInputDialog("Category Name");
        nameDialog.setTitle("Add Category");
        nameDialog.setHeaderText("Create New Category");
        nameDialog.setContentText("Please enter the category name:");
        Optional<String> nameResult = nameDialog.showAndWait();

        TextInputDialog descDialog = new TextInputDialog("Category Description");
        descDialog.setTitle("Add Category Description");
        descDialog.setHeaderText("Describe the Category");
        descDialog.setContentText("Please enter the category description:");
        Optional<String> descResult = descDialog.showAndWait();

        if (nameResult.isPresent() && descResult.isPresent()) {
            String name = nameResult.get();
            String description = descResult.get();
            try {
                if (Acount.getInstance().registerCategory(name, description)) {
                    System.out.println("Category added successfully.");
                    List<Category> categories = Acount.getInstance().getUserCategories();
                    Optional<Category> newCategory = categories.stream()
                            .filter(c -> c.getName().equals(name))
                            .findFirst();
                    if (newCategory.isPresent()) {
                        chooseCategory.getItems().add(newCategory.get());
                        chooseCategory.setValue(newCategory.get());
                    }
                } else {
                    System.out.println("Failed to add category.");
                }
            } catch (Exception e) {
                System.out.println("Error adding category: " + e.getMessage());
            }
        }
    }

    @FXML
    private void addFilePressed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            receiptPicture.setImage(image);  // Display the selected image
        }
    }

    @FXML
    private void saveButtonPressed(ActionEvent event) {
        try {
            String title = expenseTitle.getText();
            String amountStr = expenseAmount.getText();
            String unitStr = expenseUnit.getText();
            String description = expenseDescription.getText();
            Category category = chooseCategory.getValue();
            Image image = receiptPicture.getImage();
            LocalDate date = datapicker.getValue();

            // Check if title is only numbers
            if (title.matches("^\\d+$")) {
                errorMessage.setText("Invalid title. Title cannot be just numbers, please use a descriptive text.");
                return;
            }
            // Validate amount and unit
            if (!isValidNumber(amountStr)) {
                errorMessage.setText("Invalid cost. Please enter a valid number. Use a '.' instead of ',' for decimal costs");
                return;
            }
            if (!isValidInteger(unitStr)) {
                errorMessage.setText("Invalid unit. Please enter a valid integer.");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            int unit = Integer.parseInt(unitStr);

            boolean saveSuccess = Acount.getInstance().registerCharge(title, description, amount, unit, image, date, category);  // This method needs to be implemented

            if (saveSuccess) {
                // Show success animation or message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Expense Added");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Expense successfully added!");
                successAlert.showAndWait();

                // Clear all fields after success
                expenseTitle.clear();
                expenseAmount.clear();
                expenseDescription.clear();
                chooseCategory.setValue(null);
                receiptPicture.setImage(null);
                expenseUnit.clear();
                datapicker.setValue(null);
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

    @FXML
    private void removeCategoryPressed(ActionEvent event) {
        // Get the currently selected category
        Category selectedCategory = chooseCategory.getValue();
        if (selectedCategory != null) {
            // Confirmation dialog
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Delete");
            confirmationDialog.setHeaderText("Delete Category and Related Charges");

            // Set custom text with wrapping
            String contentText = "Are you sure you want to delete the category '" + selectedCategory.getName()
                    + "' and all related charges? This action cannot be undone.";
            Label label = new Label(contentText);
            label.setWrapText(true);  // Enable text wrapping within the label
            label.setMinHeight(Label.USE_PREF_SIZE);  // Ensure the label size is based on content
            confirmationDialog.getDialogPane().setContent(label);

            // Optionally set the minimum width of the dialog
            confirmationDialog.getDialogPane().setMinWidth(400);  // Adjust width as needed

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Delete the category and all related charges from the database
                    boolean deleteSuccess = Acount.getInstance().removeCategory(selectedCategory);
                    if (deleteSuccess) {
                        // Remove the category from the ComboBox
                        chooseCategory.getItems().remove(selectedCategory);
                        System.out.println("Category and related charges deleted successfully.");
                    } else {
                        System.out.println("Failed to delete category and charges.");
                    }
                } catch (Exception e) {
                    System.out.println("Error deleting category: " + e.getMessage());
                    errorMessage.setText("Error deleting category. Please try again.");
                }
            }
        } else {
            // No category selected
            errorMessage.setText("Please select a category to delete.");
        }
    }

    @FXML
    private void cancelButtonPressed(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Dashboard-view.fxml"));
            Parent root = fxmlLoader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void SignOffClicked(ActionEvent event) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/hello-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
