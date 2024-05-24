/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import application.MoneyMateApplication;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import model.Acount;
import model.AcountDAOException;
import model.User;
import model.Charge;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class DashboardController implements Initializable {

    @FXML
    private MenuItem SignOff;
    @FXML
    private Label allExpenses;
    @FXML
    private TableView<Charge> expenseTable;
    @FXML
    private TableColumn<Charge, String> nameColumn;
    @FXML
    private TableColumn<Charge, LocalDate> dateColumn;
    @FXML
    private TableColumn<Charge, String> categoryColumn;
    @FXML
    private TableColumn<Charge, Double> amountColumn;
    @FXML
    private Button addExpense;
    @FXML
    private ImageView changeUser;
    @FXML
    private BarChart<String, Number> expenseChart;
    @FXML
    private Label monthlyExpense;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Label nameDashboard;

    private String username;
    
    private ObservableList<Charge> chargeData = FXCollections.observableArrayList();
    @FXML
    private Circle circleImage;
    @FXML
    private ComboBox<String> selectFilter;
    
    private ObservableList<String> filterList;
    @FXML
    private Button deleteExpense;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBox to choose different filter for the Graph
        filterList = selectFilter.getItems();
        filterList.addAll("Category", "Monthly", "Year-by-Year");
        
        // Set up the cell value factories for the table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category")); // Assuming there's a getCategoryName method in Charge
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        try{
        User currentUser = Acount.getInstance().getLoggedUser();
        if (currentUser != null) {
            this.username = currentUser.getNickName();
            nameDashboard.setText(this.username); // Set the username in the TextField
            if (currentUser.getImage() != null) {
                //profilePicture.setImage(currentUser.getImage());
                circleImage.setFill(new ImagePattern(currentUser.getImage()));
            }
            loadData();
            setupChart("Category");
            updateMonthlyExpenses(); 
        }
        }       catch (AcountDAOException | IOException ex) {
        Logger.getLogger(editUserController.class.getName()).log(Level.SEVERE, null, ex); 
        }
        
        selectFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            setupChart(newValue);
        });
    }    
    private void updateMonthlyExpenses() {
    LocalDate now = LocalDate.now(); // Get the current date
    double total = chargeData.stream()
        .filter(charge -> charge.getDate().getMonth() == now.getMonth() && charge.getDate().getYear() == now.getYear())
        .mapToDouble(Charge::getCost)
        .sum(); // Calculate the sum of costs for the current month

    monthlyExpense.setText(String.format("€%.2f", total)); // Update the label
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
    }
}


    private void setupChart(String filter) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        if (filter == "Category"){
            Map<String, Double> summary = chargeData.stream()
            .collect(Collectors.groupingBy(
            charge -> charge.getCategory().toString(),
            Collectors.summingDouble(Charge::getCost)));

            series.getData().clear(); // Clear previous data points if necessary
            summary.forEach((category, sum) -> series.getData().add(new XYChart.Data<>(category, sum)));
        } else if(filter == "Monthly"){
            
        };

        expenseChart.getData().clear(); // Clear previous series if necessary
        expenseChart.getData().add(series);
    }
    
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
    private void allExpensesClicked(MouseEvent event) {
        try {
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/OverviewExpense-view.fxml"));
        Parent root = fxmlloader.load();
        MoneyMateApplication.setRoot(root);
    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging purposes.
        // Consider displaying an error message to the user or logging the error more formally.
    }
    }

    @FXML
    private void addExpenseClicked(ActionEvent event) {
        try {
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/AddExpense-view.fxml"));
        Parent root = fxmlloader.load();
        MoneyMateApplication.setRoot(root);
    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging purposes.
        // Consider displaying an error message to the user or logging the error more formally.
    }
    
    }

    @FXML
    private void changeUserPressed(MouseEvent event) {
        try {
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/editUser-view.fxml"));
        Parent root = fxmlloader.load();
        MoneyMateApplication.setRoot(root);
    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging purposes.
        // Consider displaying an error message to the user or logging the error more formally.
    }
    }
    
}
