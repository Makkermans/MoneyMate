/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import application.MoneyMateApplication;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Acount;
import model.AcountDAOException;
import model.User;

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
    private TableView<?> expenseTable;
    @FXML
    private TableColumn<?, ?> nameColumn;
    @FXML
    private TableColumn<?, ?> dateColumn;
    @FXML
    private TableColumn<?, ?> categoryColumn;
    @FXML
    private TableColumn<?, ?> amountColumn;
    @FXML
    private Button addExpense;
    @FXML
    private ImageView changeUser;
    @FXML
    private BarChart<?, ?> expenseChart;
    @FXML
    private Label monthlyExpense;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Label nameDashboard;

    private String username;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
        User currentUser = Acount.getInstance().getLoggedUser();
        if (currentUser != null) {
            this.username = currentUser.getNickName();
            nameDashboard.setText(this.username); // Set the username in the TextField
            if (currentUser.getImage() != null) {
                profilePicture.setImage(currentUser.getImage());
            }
        }
        }       catch (AcountDAOException | IOException ex) {
        Logger.getLogger(editUserController.class.getName()).log(Level.SEVERE, null, ex); 
        }
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
