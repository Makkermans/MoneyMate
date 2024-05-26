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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Acount;
import model.AcountDAOException;
import model.Category;
import model.Charge;
import model.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class OverviewExpenseController implements Initializable {

    @FXML
    private MenuItem SignOff;
    @FXML
    private Circle circle;
    @FXML
    private Label username;
    @FXML
    private ImageView userChange;
    @FXML
    private TableView<Charge> expenseTable;
    @FXML
    private TableColumn<Charge, String> nameColumn;
    @FXML
    private TableColumn<Charge, LocalDate> dateColumn;
    @FXML
    private TableColumn<Charge, Category> categoryColumn;
    @FXML
    private TextField search;
    @FXML
    private DatePicker datepicker;
    @FXML
    private TableColumn<Charge, Double> amountColumn;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button printButton;
    @FXML
    private Button returnButton;

    private String nickname;

    private ObservableList<Charge> chargeData = FXCollections.observableArrayList();

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @FXML
    private TableColumn<Charge, Integer> unitsColumn;
    @FXML
    private Button detailButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up the cell value factories for the table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setCellFactory(column -> new TableCell<Charge, Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty || category == null ? null : category.getName());
            }
        });
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
        try {
            User currentUser = Acount.getInstance().getLoggedUser();
            if (currentUser != null) {
                this.nickname = currentUser.getNickName();
                username.setText(this.nickname); // Set the username in the TextField
                if (currentUser.getImage() != null) {

                    circle.setFill(new ImagePattern(currentUser.getImage()));
                }
                loadData();

            }
        } catch (AcountDAOException | IOException ex) {
            Logger.getLogger(editUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Add a listener to the search TextField
        search.textProperty().addListener((observable, oldValue, newValue) -> filterCharges());
        datepicker.valueProperty().addListener((observable, oldValue, newValue) -> filterCharges());
    }

    private void loadData() throws IOException {
        try {
            List<Charge> charges = Acount.getInstance().getUserCharges();
            if (charges != null) {
                chargeData.setAll(charges);
            } else {
                chargeData.clear();
            }
            expenseTable.setItems(chargeData);
        } catch (AcountDAOException e) {
            e.printStackTrace(); // Log the exception

        }
    }

    private void filterCharges() {
        String searchText = search.getText();
        LocalDate filterDate = datepicker.getValue();

        if ((searchText == null || searchText.isEmpty()) && filterDate == null) {
            expenseTable.setItems(chargeData); // Reset the table view if the search text and filter date are empty
        } else {
            ObservableList<Charge> filteredCharges = FXCollections.observableArrayList();
            for (Charge charge : chargeData) {
                boolean matchesSearchText = searchText == null || searchText.isEmpty()
                        || charge.getName().toLowerCase().contains(searchText.toLowerCase())
                        || charge.getCategory().getName().toLowerCase().contains(searchText.toLowerCase())
                        || charge.getDate().toString().contains(searchText)
                        || String.valueOf(charge.getCost()).contains(searchText);

                boolean matchesDate = filterDate == null || charge.getDate().isEqual(filterDate);

                if (matchesSearchText && matchesDate) {
                    filteredCharges.add(charge);
                }
            }
            expenseTable.setItems(filteredCharges);
        }
    }

    @FXML
    private void SignOffClicked(ActionEvent event) throws Exception {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/hello-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging purposes.

        }
    }

    @FXML
    private void userChangeClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/editUser-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging purposes.

        }
    }

    @FXML
    private void modifyButtonClicked(ActionEvent event) {
        Charge selectedCharge = expenseTable.getSelectionModel().getSelectedItem();
        if (selectedCharge != null) {
            final double LABEL_WIDTH = 100;
            final double MAX_IMAGE_SIZE = 150; // Define a maximum size for the image

            // Fields initialization
            TextField nameField = new TextField(selectedCharge.getName());
            TextField costField = new TextField(String.valueOf(selectedCharge.getCost()));
            TextField descriptionField = new TextField(selectedCharge.getDescription());
            DatePicker datePicker = new DatePicker(selectedCharge.getDate());
            TextField categoryField = new TextField(selectedCharge.getCategory().getName());
            TextField unitsField = new TextField(String.valueOf(selectedCharge.getUnits()));
            Stream.of(nameField, costField, descriptionField, datePicker, categoryField, unitsField)
                    .forEach(field -> field.setMinWidth(200));

            // Image view setup
            ImageView imageView = new ImageView(selectedCharge.getImageScan());
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(MAX_IMAGE_SIZE);
            imageView.setFitWidth(MAX_IMAGE_SIZE);

            // Change Image Button
            Button changeImageButton = new Button("Change Image");
            changeImageButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
                );
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    Image newImage = new Image(selectedFile.toURI().toString(), MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, true, true);
                    imageView.setImage(newImage);
                }
            });

            // Layout setup
            HBox nameBox = createHBox("Name:", nameField, LABEL_WIDTH);
            HBox categoryBox = createHBox("Category:", categoryField, LABEL_WIDTH);
            HBox descriptionBox = createHBox("Description:", descriptionField, LABEL_WIDTH);
            HBox dateBox = createHBox("Date:", datePicker, LABEL_WIDTH);
            HBox costBox = createHBox("Cost:", costField, LABEL_WIDTH);
            HBox unitsBox = createHBox("Units:", unitsField, LABEL_WIDTH);
            HBox imageBox = new HBox(10, new Label("Image:"), imageView, changeImageButton);
            ((Label) imageBox.getChildren().get(0)).setMinWidth(LABEL_WIDTH);

            VBox mainContainer = new VBox(10, nameBox, categoryBox, descriptionBox, dateBox, costBox, unitsBox, imageBox);
            mainContainer.setPadding(new Insets(15));

            // Alert setup
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Modify Charge");
            alert.setHeaderText("Modify the details of the selected charge:");
            alert.getDialogPane().setContent(mainContainer);
            alert.setResizable(true);

            alert.getDialogPane().expandedProperty().addListener((observable, oldValue, newValue) -> {
                alert.getDialogPane().requestLayout();
                alert.getDialogPane().getScene().getWindow().sizeToScene();
            });

            // Show dialog and process results
            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    Acount.getInstance().removeCharge(selectedCharge);
                    Acount.getInstance().registerCharge(
                            nameField.getText(),
                            descriptionField.getText(),
                            Double.parseDouble(costField.getText()),
                            Integer.parseInt(unitsField.getText()),
                            imageView.getImage(),
                            datePicker.getValue(),
                            selectedCharge.getCategory()
                    );
                    loadData();
                } catch (NumberFormatException | IOException | AcountDAOException e) {
                    showErrorMessage("Error updating charge: " + e.getMessage());
                }
            }
        } else {
            showAlert("Warning", "No Charge Selected", "Please select a charge in the table.");
        }
    }

    private HBox createHBox(String labelText, Node field, double labelWidth) {
        Label label = new Label(labelText);
        label.setMinWidth(labelWidth);
        return new HBox(10, label, field);
    }

    @FXML
    private void deleteButtonClicked(ActionEvent event) throws IOException {
        Charge selectedCharge = expenseTable.getSelectionModel().getSelectedItem();
        if (selectedCharge != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this charge?");
            alert.setContentText("Name: " + selectedCharge.getName() + "\nAmount: " + selectedCharge.getCost() + "\nCategory: " + selectedCharge.getCategory().getName() + "\nDate: " + selectedCharge.getDate());

            if (alert.showAndWait().get() == ButtonType.OK) {
                chargeData.remove(selectedCharge);
                try {
                    Acount.getInstance().removeCharge(selectedCharge);
                } catch (AcountDAOException e) {
                    showErrorMessage("Failed to delete the charge: " + e.getMessage());
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Charge Selected");
            alert.setContentText("Please select a charge in the table.");
            alert.showAndWait();
        }

    }

    @FXML
    public void printButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("Annual_Expenses_Report.pdf");
        File file = fileChooser.showSaveDialog(printButton.getScene().getWindow());

        if (file != null) {
            try {
                String filePath = file.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }
                generatePdfReport(filePath);
                showSuccessMessage();
            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
        }
    }

    public void generatePdfReport(String filePath) throws IOException, AcountDAOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Title
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA.COURIER_BOLD), 18);
                contentStream.newLineAtOffset(100, 750);
                contentStream.showText("Annual Expenses Report");
                contentStream.endText();

                // Table header
                float margin = 100; // Left margin
                float yPosition = 700; // Starting y position for table header
                float yIncrement = 15; // Space between rows

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Name");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 100, yPosition);
                contentStream.showText("Date");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 200, yPosition);
                contentStream.showText("Category");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 300, yPosition);
                contentStream.showText("Units");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 400, yPosition);
                contentStream.showText("Cost");
                contentStream.endText();
                yPosition -= yIncrement;

                // Table content
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);

                List<Charge> charges = Acount.getInstance().getUserCharges();
                for (Charge charge : charges) {

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(charge.getName());
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 100, yPosition);
                    try {
                        String formattedDate = charge.getDate().format(dateFormatter);
                        contentStream.showText(formattedDate);
                    } catch (Exception e) {
                        contentStream.showText("Invalid Date");
                    }
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 200, yPosition);
                    contentStream.showText(charge.getCategory().getName());
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 300, yPosition);
                    contentStream.showText(String.valueOf(charge.getUnits()));
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 400, yPosition);
                    contentStream.showText(String.format("%.2f", charge.getCost()));
                    contentStream.endText();

                    yPosition -= yIncrement;
                }
            }

            document.save(filePath);
        }
    }

    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("PDF report generated successfully.");
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("An error occurred: " + message);
        alert.showAndWait();
    }

    @FXML
    private void returnButtonClikced(ActionEvent event) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/Dashboard-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging purposes.

        }
    }

    @FXML
    private void detailButtonClicked(ActionEvent event) {
        Charge selectedCharge = expenseTable.getSelectionModel().getSelectedItem();
        if (selectedCharge != null) {
            // Setup fields and labels with non-editable configurations
            TextField nameField = setupTextField(selectedCharge.getName(), false);
            TextField costField = setupTextField(String.format("%.2f", selectedCharge.getCost()), false);
            TextField descriptionField = setupTextField(selectedCharge.getDescription(), false);
            DatePicker datePicker = new DatePicker(selectedCharge.getDate());
            datePicker.setEditable(false);
            TextField categoryField = setupTextField(selectedCharge.getCategory().getName(), false);
            TextField unitsField = setupTextField(String.valueOf(selectedCharge.getUnits()), false);

            // Image view setup
            ImageView imageView = new ImageView(selectedCharge.getImageScan());
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(150);
            imageView.setFitWidth(150);
            Button viewImageButton = new Button("View Larger Image");
            viewImageButton.setOnAction(e -> openImageInNewWindow(selectedCharge.getImageScan()));

            // Create HBox for each field
            HBox nameBox = createHBox("Name:", nameField, 100);
            HBox categoryBox = createHBox("Category:", categoryField, 100);
            HBox descriptionBox = createHBox("Description:", descriptionField, 100);
            HBox dateBox = createHBox("Date:", datePicker, 100);
            HBox costBox = createHBox("Cost:", costField, 100);
            HBox unitsBox = createHBox("Units:", unitsField, 100);
            HBox imageBox = new HBox(10, new Label("Image:"), imageView, viewImageButton);
            ((Label) imageBox.getChildren().get(0)).setMinWidth(100);

            // Combine all HBoxes into a VBox
            VBox mainContainer = new VBox(10, nameBox, categoryBox, descriptionBox, dateBox, costBox, unitsBox, imageBox);
            mainContainer.setPadding(new Insets(15));

            // Display the alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Expense Details");
            alert.setHeaderText("Details of the selected expense:");
            alert.getDialogPane().setContent(mainContainer);
            alert.showAndWait();
        } else {
            showAlert("Warning", "No Charge Selected", "Please select a charge in the table.");
        }
    }

    private TextField setupTextField(String text, boolean editable) {
        TextField textField = new TextField(text);
        textField.setEditable(editable);
        return textField;
    }

    private void openImageInNewWindow(Image image) {
        ImageView largerImageView = new ImageView(image);
        largerImageView.setFitHeight(600);
        largerImageView.setFitWidth(800);
        largerImageView.setPreserveRatio(true);
        Stage imageStage = new Stage();
        imageStage.setTitle("View Image");
        VBox imageBox = new VBox(largerImageView);
        imageBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(imageBox);
        imageStage.setScene(scene);
        imageStage.initModality(Modality.APPLICATION_MODAL);
        imageStage.show();
    }

    private void showAlertWithContent(String title, String headerText, VBox content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
