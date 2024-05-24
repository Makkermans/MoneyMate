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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import model.Acount;
import model.AcountDAOException;
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
    private TableColumn<Charge, String> categoryColumn;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up the cell value factories for the table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category")); // Assuming there's a getCategoryName method in Charge
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        try{
        User currentUser = Acount.getInstance().getLoggedUser();
        if (currentUser != null) {
            this.nickname = currentUser.getNickName();
            username.setText(this.nickname); // Set the username in the TextField
            if (currentUser.getImage() != null) {
                //profilePicture.setImage(currentUser.getImage());
                circle.setFill(new ImagePattern(currentUser.getImage()));
            }
            loadData();
            
        }
        }       catch (AcountDAOException | IOException ex) {
        Logger.getLogger(editUserController.class.getName()).log(Level.SEVERE, null, ex); 
        }
    }    
    private void loadData() throws IOException {
    try {
        List<Charge> charges = Acount.getInstance().getUserCharges(); // Using the getUserCharges method
        if (charges != null) {
            chargeData.setAll(charges);
        } else {
            chargeData.clear();
        }
        expenseTable.setItems(chargeData);
    } catch (AcountDAOException e) {
        e.printStackTrace(); // Log the exception
        // You might want to show an error message to the user
    }}
    
    @FXML
    private void SignOffClicked(ActionEvent event) throws Exception{
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
    private void userChangeClicked(MouseEvent event) {
        try {
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/editUser-view.fxml"));
        Parent root = fxmlloader.load();
        MoneyMateApplication.setRoot(root);
    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging purposes.
        // Consider displaying an error message to the user or logging the error more formally.
    }
    }
    

    @FXML
    private void modifyButtonClicked(ActionEvent event) {
    }

    @FXML
    private void deleteButtonClicked(ActionEvent event) throws IOException {
        Charge selectedCharge = expenseTable.getSelectionModel().getSelectedItem();
        if (selectedCharge != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this charge?");
            alert.setContentText("Name: " + selectedCharge.getName() + "\nAmount: " + selectedCharge.getCost());

            if (alert.showAndWait().get() == ButtonType.OK) {
                chargeData.remove(selectedCharge);
                try {
                    Acount.getInstance().removeCharge(selectedCharge); // Assuming there's a method to remove charge from the account
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
        // Consider displaying an error message to the user or logging the error more formally.
    }
    }
}
