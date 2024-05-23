/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import application.MoneyMateApplication;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class DashboardController implements Initializable {

    @FXML
    private MenuItem SignOff;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void SignOffClicked(ActionEvent event) throws Exception{
        FXMLLoader fxmlloader= new FXMLLoader(getClass().getResource("view/hello-view.fxml"));
        Parent root = fxmlloader.load();
        MoneyMateApplication.setRoot(root);
    }
    
}
