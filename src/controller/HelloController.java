/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import application.MoneyMateApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Acount;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

/**
 * FXML Controller class
 *
 * @author jsoler
 * Modified carferl2
 */
public class HelloController implements Initializable {

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button LoginButton;

    @FXML
    private Label register;
    @FXML
    private TextField nameField;
    @FXML
    private Label incorrect;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    @FXML
    public void registerButtonPressed(MouseEvent event) {
        try {
            // Load the registration view
            FXMLLoader fxmlloader= new FXMLLoader(getClass().getResource("/view/Register-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging purposes.
            // Consider displaying an error message to the user or logging the error more formally.
        }
    }


    
    @FXML
    private void loginClicked(ActionEvent event) {
        String username = nameField.getText();
        String password = passwordField.getText();

        try {
            boolean loginSuccessful = Acount.getInstance().logInUserByCredentials(username, password);
            if (loginSuccessful) {
                // Load the main application dashboard or home screen
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Dashboard-view.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                // Display an error message on the UI
                incorrect.setText("Login failed: Incorrect username or password. Try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions such as network issues, configuration errors, etc.
            incorrect.setText("Login failed: Unable to connect. Please try again later.");
        }
    }

}