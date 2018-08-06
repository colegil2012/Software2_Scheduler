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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import softwareii_project.Models.Customer;
import softwareii_project.utils.Database;

/**
 *
 * @author Cole
 */
public class CustomerScreenController implements Initializable {

    @FXML TableView <Customer> customerTable;
    @FXML TableColumn <Customer, Integer> custIDColumn;
    @FXML TableColumn <Customer, String> custNameColumn;
    
    Connection dbConnection = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        
        //the param object is manipulated within the lambda expression in order to retrieve the returnValue rather than instantiated outside of the method
        //Observable value is the interface involved
        custIDColumn.setCellValueFactory((TableColumn.CellDataFeatures<Customer, Integer> param) -> {
            ObservableValue<Integer> returnValue;
            returnValue = (param.getValue().custIDProperty()).asObject();
            return returnValue;
        });
        
        custNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<Customer, String> param) -> {
            ObservableValue<String> returnValue;
            returnValue = (param.getValue().custNameProperty());
            return returnValue;
        });
        
        try {
            customerTable.setItems(getAllCustomers());
        } catch (SQLException ex) {
            Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Gets all customer objects from the database and passes the list of these objects back to populate the customer table
    public ObservableList <Customer> getAllCustomers() throws SQLException {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        
        
        try {
            dbConnection = Database.getConnection();
            Statement statement = dbConnection.createStatement();

            String query = "SELECT * FROM customer";
            ResultSet results = statement.executeQuery(query);
           
            while(results.next()) {
                customers.add(new Customer(Integer.parseInt(results.getString("customerID")), results.getString("customerName")));
                //Logger.log(user, true);

            }
        } catch(SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        
        return customers;
    }
    
    public void cancelBtnHandler(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("MainScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.show();
        
    }
    
    //Navigates to the addcustomer screen
    public void onCustomerAddBtnClick(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AddCustomerScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            
            stage.show();
    }
    
    //Navigates to the modify customer screen based off which customer object is selected in the tableview
    public void onCustomerModBtnClick(ActionEvent event) throws IOException {
            if(customerTable.getSelectionModel().getSelectedItem() != null) {
                Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
                int selectedID = selectedCustomer.getCustomerID();
                
                Customer.setSelectedCustomer(selectedID);
                
                setSelectedAddressID(selectedID);
                
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("ModCustomerScreen.fxml"));

                Parent root = (Parent)fxmlLoader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);

                stage.show();
            } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("No Customer Selected");
            alert.showAndWait();
        }   
    }
    
    //Button handler for the Delete button on the customer screen, will delete the selected object in the tableView from the DB
    public void onCustomerDelBtnClick(ActionEvent event) throws SQLException {
            if(customerTable.getSelectionModel().getSelectedItem() != null) {
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            int selectedID = selectedCustomer.getCustomerID();

            try {
                dbConnection = Database.getConnection();
                Statement statement = dbConnection.createStatement();
                String update = "DELETE FROM customer WHERE customerID=" + selectedID;
                String query = "SELECT * FROM customer WHERE customerID=" + selectedID;
                
                statement.executeUpdate(update);
                
                ResultSet results = statement.executeQuery(query);
                if(results.next()) {
                    System.out.println("Update did not execute successfully");
                } else {
                    System.out.println("Update Completed!");
                }
                
            
            } catch (SQLException e) {
                System.out.println("SQL Error: " + e.getMessage());
            }

            customerTable.setItems(getAllCustomers());
            customerTable.refresh();
                
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("No Customer Selected");
            alert.showAndWait();
        }
    }
    
    public void setSelectedAddressID(int id) {
        try {
            dbConnection = Database.getConnection();
            Statement st = dbConnection.createStatement();
            
            String query = "SELECT addressId FROM customer WHERE customerid = " + id;
            
            ResultSet r = st.executeQuery(query);
            while(r.next()) {
                Customer.setSelectedAddress(Integer.parseInt(r.getString("addressId")));
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
