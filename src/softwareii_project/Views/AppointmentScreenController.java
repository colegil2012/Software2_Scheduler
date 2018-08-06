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
import softwareii_project.Models.Appointment;
import softwareii_project.utils.Database;

/**
 *
 * @author Cole
 */
public class AppointmentScreenController implements Initializable {

    @FXML TableView<Appointment> appointmentTable;
    @FXML TableColumn<Appointment, Integer> appointmentIDColumn;
    @FXML TableColumn<Appointment, String> contactColumn, meetingColumn, customerColumn, locationColumn, startColumn, endColumn;
    
    
    Connection dbConnection = null;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        setColumns();
        appointmentTable.setItems(getAllAppointments());
        
        
    }
    
    //Returns all appointment objects to populate the appointment table
    public ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try {
            dbConnection = Database.getConnection();
            Statement statement = dbConnection.createStatement();

            String query = "SELECT appointment.appointmentId, customer.customerName, appointment.description, appointment.location, appointment.contact, appointment.start, "
                    + "appointment.end FROM appointment, customer WHERE customer.customerId = appointment.customerId";
            ResultSet results = statement.executeQuery(query);
           
            while(results.next()) {
                
                appointments.add(new Appointment(Integer.parseInt(results.getString("appointmentId")), results.getString("contact"), results.getString("location"), 
                        results.getString("description"), results.getString("customerName"), results.getString("start"), results.getString("end")));
                //Logger.log(user, true);

            }
        } catch(SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        
        return appointments;
        
    }
   
    
    //assigns the cellvalues to each column of the appointment table
    public void setColumns() {
        appointmentIDColumn.setCellValueFactory((TableColumn.CellDataFeatures<Appointment, Integer> param) -> {
            ObservableValue<Integer> returnValue;
            returnValue = (param.getValue().appointmentIDProperty()).asObject();
            return returnValue;
        });
          
        contactColumn.setCellValueFactory((TableColumn.CellDataFeatures<Appointment, String> param) -> {
            ObservableValue<String> returnValue;
            returnValue = (param.getValue().contactProperty());
            return returnValue;
        });
        
        meetingColumn.setCellValueFactory((TableColumn.CellDataFeatures<Appointment, String> param) -> {
            ObservableValue<String> returnValue;
            returnValue = (param.getValue().meetingTypeProperty());
            return returnValue;
        });
        
        customerColumn.setCellValueFactory((TableColumn.CellDataFeatures<Appointment, String> param) -> {
            ObservableValue<String> returnValue;
            returnValue = (param.getValue().customerNameProperty());
            return returnValue;
        });
        
        locationColumn.setCellValueFactory((TableColumn.CellDataFeatures<Appointment, String> param) -> {
            ObservableValue<String> returnValue;
            returnValue = (param.getValue().locationProperty());
            return returnValue;
        });
        
        startColumn.setCellValueFactory((TableColumn.CellDataFeatures<Appointment, String> param) -> {
            ObservableValue<String> returnValue;
            returnValue = (param.getValue().startProperty());
            return returnValue;
        });
        
        endColumn.setCellValueFactory((TableColumn.CellDataFeatures<Appointment, String> param) -> {
            ObservableValue<String> returnValue;
            returnValue = (param.getValue().endProperty());
            return returnValue;
        });
            
    }
    
    public void onAddAppointmentBtnClick(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AddAppointmentScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.show();
    }
    
    public void onModAppointmentBtnClick(ActionEvent event) throws IOException {
        
            if(appointmentTable.getSelectionModel().getSelectedItem() != null) {
                Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
                int selectedID = selectedAppointment.getAppointmentID();

                Appointment.setSelectedAppointmentId(selectedID);
 
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("ModAppointmentScreen.fxml"));

                Parent root = (Parent)fxmlLoader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);

                stage.show();
            
            
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("No Appointment Selected");
                alert.showAndWait();
        }
            
    }
    
    public void onDelAppointmentBtnClick(ActionEvent event) {
    if(appointmentTable.getSelectionModel().getSelectedItem() != null) {
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            int selectedID = selectedAppointment.getAppointmentID();

            try {
                dbConnection = Database.getConnection();
                Statement statement = dbConnection.createStatement();
                String update = "DELETE FROM appointment WHERE appointmentID=" + selectedID;
                String query = "SELECT * FROM appointment WHERE appointmentID=" + selectedID;
                
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

            appointmentTable.setItems(getAllAppointments());
            appointmentTable.refresh();
                
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("No Appointment Selected");
            alert.showAndWait();
        }
    }
    
    public void onCancelBtnBlick(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("MainScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.show();
    }

    
    
}
