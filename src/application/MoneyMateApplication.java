/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.util.HashMap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jsanchez
 * Modified carferl2
 */
public class MoneyMateApplication extends Application {
    private static Scene scene;
    static HashMap<String, Parent> roots = new HashMap<>();

    public static void setRoot(Parent root){
        scene.setRoot(root);
    }

    public static void setRoot(String name){
        Parent root = roots.get(name);
        if(root != null){
            scene.setRoot(root);
        } else {
            System.err.printf("Root %s not found", name);
        }
    }

    protected static Parent getRoot(String name){
        Parent root = roots.get(name);
        if(root != null){
            scene.setRoot(root);
        } else {
            System.err.printf("Root %s not found", name);
        }
        return root;
    }

    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root;
        FXMLLoader fxmlloader;
        // Start Screen
        fxmlloader = new FXMLLoader(getClass().getResource("/view/hello-view.fxml"));
        root = fxmlloader.load();
        roots.put("HelloController", root);
        

        scene = new Scene(roots.get("HelloController"));
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("MoneyMate - Your Financial Companion");
        stage.setHeight(400);
        stage.setWidth(600);
        stage.setMinHeight(100);
        stage.setMinWidth(200);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
