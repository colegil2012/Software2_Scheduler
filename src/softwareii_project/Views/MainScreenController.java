/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii_project.Views;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import softwareii_project.Models.LoggedUser;

/**
 *
 * @author Cole
 */
public class MainScreenController implements Initializable {

    @FXML TextField welcomeText;
    private final ZoneId zoneId = ZoneId.systemDefault();
    
    Connection dbConnection = null;
    Statement st = null;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        String user = LoggedUser.getUser();
        String welcome = "Welcome, ";
        welcomeText.setText(welcome + user + "!");
      
    }
    
    public void reportBtnHandler(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("ReportScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            
            stage.show();   
    }
    
    public void customerBtnHandler(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("CustomerScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            
            stage.show();
    }
    
    public void appointmentBtnHandler(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AppointmentScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            
            stage.show();
    }
    
    public void calendarBtnHandler(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AppointmentCalendarScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            
            stage.show();
    }
    
    public void onExitBtnClick(ActionEvent event) {
        System.exit(0);
    }
    
    public Timestamp getDate() {
        LocalDateTime now = LocalDateTime.now();
        
        ZonedDateTime startUTC = now.atZone(zoneId).withZoneSameInstant(ZoneId.of("UTC"));
        Timestamp ts = Timestamp.valueOf(startUTC.toLocalDateTime());
        
        return ts;
    }
  
    
}
