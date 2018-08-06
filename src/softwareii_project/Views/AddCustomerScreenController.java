/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii_project.Views;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import softwareii_project.Models.LoggedUser;
import softwareii_project.utils.Database;

/**
 *
 * @author Cole
 */
public class AddCustomerScreenController implements Initializable {
    
    private final ZoneId zoneId = ZoneId.systemDefault();

    @FXML ComboBox countryCombo, cityCombo;
    @FXML TextField custNameText, addressOneText, addressTwoText, zipText, phoneText;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setCountryComboBox();
            
            countryCombo.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    try {
                        setCityComboBox(newValue);
                    } catch (SQLException ex) {
                        Logger.getLogger(AddCustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            });
        } catch (SQLException ex) {
            Logger.getLogger(AddCustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //Save Button Handler, will insert new customer and address objects into the database based on entered information
    public void onSaveBtnClick(ActionEvent event) throws SQLException, IOException {
        String name, add1, add2, cityString, city = "", zip, phone, addressId, update, update2;
        Timestamp date;
        int active = 1;
        Connection dbConnection = Database.getConnection();
        Statement st = dbConnection.createStatement();
        ResultSet r1;
       
        
        name = custNameText.getText();
        add1 = addressOneText.getText();
        add2 = addressTwoText.getText();
        zip = zipText.getText();
        phone = phoneText.getText();
        date = getDate();
        
        if(cityCombo.getSelectionModel().getSelectedItem() != null) {
            cityString = (String) cityCombo.getSelectionModel().getSelectedItem();
            
            String query = "SELECT * FROM city WHERE city = '" + cityString + "'";
            r1 = st.executeQuery(query);
            
            while(r1.next()) {
                city = r1.getString("cityId");
            }
        }
        
        System.out.println("name: " + name);
        System.out.println("add1: " + add1);
        System.out.println("add2: " + add2);
        System.out.println("zip: " + zip);
        System.out.println("phone: " + phone);
        System.out.println("city: " + city);
        
        
        if( validateForm()) {
        update = "INSERT INTO address (address, address2, cityId, postalCode, phone, createdBy, createDate, lastUpdateBy)" +
                "VALUES ('" + add1 + "','" + add2 + "'," + city + ",'" + zip + "','" + phone + "','" + LoggedUser.getUser() +
                    "','" + date + "','" + LoggedUser.getUser() + "')";
            
            System.out.println(update);
            
            st.executeUpdate(update);
            
            addressId = getLastAddressID();
            
            update2 = "INSERT INTO customer (customerName, addressId, active, createdBy, createDate, lastUpdateBy) " +
                    "VALUES ('" + name + "'," + addressId + "," + active + ",'" + LoggedUser.getUser() + 
                    "','" + date + "','" + LoggedUser.getUser() + "')";
            
            System.out.println(update2);
            st.executeUpdate(update2);
            
            //return to mainscreen
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("CustomerScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            
            stage.show();
        }
        
    }
    
    public Boolean validateForm() {
        Boolean valid = false;
        String errorBody = "";
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Form Submission Error:");
        alert.setHeaderText(null);
        
        if( !(custNameText.getText().equals(""))) {
            if( !(addressOneText.getText().equals(""))) {
                if( countryCombo.getSelectionModel().getSelectedItem() != null) {
                    if(cityCombo.getSelectionModel().getSelectedItem() != null) {
                        if( !(zipText.getText().equals(""))) {
                            if( !(phoneText.getText().equals(""))) {
                                valid = true;
                            } else {
                                errorBody = "Phone number not entered.";
                            }
                        } else {
                            errorBody = "Postal Code not entered.";
                        }
                    } else {
                        errorBody = "City not selected.";
                    }
                } else {
                    errorBody = "Country not selected.";
                } 
            } else {
                errorBody = "Address not entered.";
            }
        } else {
            errorBody = "Customer Name not entered";
        }
        
        if( !(errorBody.equals(""))) {
            alert.setContentText(errorBody);
            alert.showAndWait();
        }
        
        return valid;
    }
    
    public void onCancelBtnClick(ActionEvent event) throws IOException {
    //return to mainscreen
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("CustomerScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            
            stage.show();
    }
    
    //Method to update the cityCombo list items based on the passed country name
    public void setCityComboBox(String country) throws SQLException {
        
        int id = -1;
        
        ObservableList<String> cities = FXCollections.observableArrayList();
        
        Connection dbConnection = Database.getConnection();
        Statement st = dbConnection.createStatement();
        
        String query1 = "SELECT * FROM country WHERE country = '" + country + "'";
        
        ResultSet r1 = st.executeQuery(query1);
        
        while(r1.next()) {
            id = Integer.parseInt(r1.getString("countryId"));
        }
        
        String query2 = "SELECT * FROM city WHERE countryId = " + id;
        
        ResultSet r2 = st.executeQuery(query2);
        while(r2.next()) {
            cities.add(r2.getString("city"));
        }
        
        cityCombo.setItems(cities);
    }
    
    
    //Set country combobox list items based on DB values
    public void setCountryComboBox() throws SQLException {
        ObservableList<String> countries = FXCollections.observableArrayList();
        
        Connection dbConnection = Database.getConnection();
        Statement st = dbConnection.createStatement();
        
        String query = "SELECT * FROM country";
        
        ResultSet r = st.executeQuery(query);
        while(r.next()) {
            countries.add(r.getString("country"));
        }
        
        countryCombo.setItems(countries);
    }
    
    //This method is to acquire the current date
    public Timestamp getDate() {
        LocalDateTime now = LocalDateTime.now();
        
        ZonedDateTime startUTC = now.atZone(zoneId).withZoneSameInstant(ZoneId.of("UTC"));
        Timestamp ts = Timestamp.valueOf(startUTC.toLocalDateTime());
        
        return ts;
    }
    
    //This will get the highest address ID in the db, this will be the address that was just entered for this customer. 
    public String getLastAddressID() throws SQLException {
        
        ArrayList<Integer> addIDs = new ArrayList();
        Connection dbConnection = Database.getConnection();
        Statement st = dbConnection.createStatement();
        
        String query = "Select * FROM address";
        ResultSet r = st.executeQuery(query);
        
        while(r.next()) {
            addIDs.add(Integer.parseInt(r.getString("addressID")));
        }
        
        Integer max = Collections.max(addIDs);
        
        String maxString = max.toString();
        return maxString;
        
    }
    
}
