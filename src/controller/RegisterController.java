/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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

    
}
