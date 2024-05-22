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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author jsoler
 * Modified carferl2
 */
public class HelloController implements Initializable {
    @FXML
    private TextField userField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button LoginButton;

    @FXML
    private Label register;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void registerButtonPressed(MouseEvent event) throws Exception{
        FXMLLoader fxmlloader= new FXMLLoader(getClass().getResource("/com/example/expenseapplication/views/Register-view.fxml"));
        Parent root = fxmlloader.load();
        MoneyMateApplication.setRoot(root);

    }
}