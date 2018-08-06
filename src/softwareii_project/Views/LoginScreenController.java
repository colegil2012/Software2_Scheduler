package softwareii_project.Views;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import softwareii_project.Models.LoggedUser;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import softwareii_project.utils.Database;
import softwareii_project.utils.LogWriter;

/**
 *
 * @author Cole
 */
public class LoginScreenController implements Initializable {
    
    public String myIP = "";
    
    @FXML private Label locationLabel, usernameLabel, passwordLabel;
    @FXML private TextField userTF, passTF;
    @FXML private Button loginBtn;
    
    
    private String errorHeader;
    private String errorBody;
    
    Connection dbConnection;
    Statement st;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Locale locale = Locale.getDefault();
        rb = ResourceBundle.getBundle("softwareii_project/language_files/rb", locale);
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        loginBtn.setText(rb.getString("Login"));
        
        
        errorHeader = rb.getString("errorHeader");
        errorBody = rb.getString("errorBody");
        
        
        System.out.println(myIP);
        
        locationLabel.setText(myIP);   
    } 
    
    //Handler method for the "Login" button
    public void loginBtnHandler(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {
        String user = userTF.getText();
        String pass = passTF.getText();
        Boolean valid;
        
        valid = LoggedUser.verifyUser(user, pass);
        LogWriter.log(user, valid);
        
        if(valid) {
            
            compareTimeStamp();
           
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("MainScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(errorHeader);
            alert.setHeaderText(null);
            alert.setContentText(errorBody);
            alert.showAndWait();
        }
        
        
    }
  
    
    public void compareTimeStamp() throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdt = now.atZone(zid);
        LocalDateTime ldt = zdt.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime ldt2 = ldt.plusMinutes(15);
        String username = LoggedUser.getUser();
        String customer, id;
        
        try {
            dbConnection = Database.getConnection();
            st = dbConnection.createStatement();
            
            String query = "SELECT appointmentId, customer.customerName FROM appointment, customer WHERE start BETWEEN '" + ldt + "' AND '" + ldt2 + 
                    "' AND contact = '" + username + "' AND appointment.customerid = customer.customerId";
            ResultSet r = st.executeQuery(query);
            while(r.next()) {
                customer = r.getString("customerName");
                id = r.getString("appointmentId");
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Alert");
                alert.setHeaderText(null);
                alert.setContentText("Appointment ID #" + id + " with " + customer + " will begin in less than 15 minutes");
                alert.showAndWait();
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    
}

   
    
