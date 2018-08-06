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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import softwareii_project.Models.Appointment;
import softwareii_project.utils.Database;

/**
 *
 * @author Cole
 */
public class AppointmentCalendarScreenController implements Initializable {

    @FXML TableView<Appointment> appointmentTable;
    @FXML TableColumn<Appointment, Integer> appointmentIDColumn;
    @FXML TableColumn<Appointment, String> contactColumn, meetingColumn, customerColumn, locationColumn, startColumn, endColumn;
    Connection dbConnection = null;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        setColumns();
        appointmentTable.setItems(getAllAppointments());
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
    

    
    public ObservableList<Appointment> getThisWeeksAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        
        LocalDateTime now = LocalDateTime.now();
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdt = now.atZone(zid);
        LocalDateTime ldt = zdt.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime ldt2 = ldt.plusDays(7);
              
        try {
            dbConnection = Database.getConnection();
            Statement statement = dbConnection.createStatement();

            String query = "SELECT appointment.appointmentId, customer.customerName, appointment.description, appointment.location, appointment.contact, appointment.start, "
                    + "appointment.end FROM appointment, customer WHERE customer.customerId = appointment.customerId AND date(start) BETWEEN '" + ldt + "' AND '" + ldt2 + "'";
            ResultSet results = statement.executeQuery(query);
           
            System.out.println(query);
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
    
        public ObservableList<Appointment> getThisMonthsAppointments() {
                ObservableList<Appointment> appointments = FXCollections.observableArrayList();
                
                int thisMonth;
                Calendar cal = Calendar.getInstance();
                int month = cal.get(cal.MONTH);
                thisMonth = month + 1;
        try {
            dbConnection = Database.getConnection();
            Statement statement = dbConnection.createStatement();

            String query = "SELECT appointment.appointmentId, customer.customerName, appointment.description, appointment.location, appointment.contact, appointment.start, "
                    + "appointment.end FROM appointment, customer WHERE customer.customerId = appointment.customerId AND Month(start) = " + thisMonth;
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
    
    public void weekBtnHandler(ActionEvent event) throws IOException {
        appointmentTable.setItems(getThisWeeksAppointments());
        appointmentTable.refresh();
    }
    
    public void monthBtnHandler(ActionEvent event) throws IOException {
        appointmentTable.setItems(getThisMonthsAppointments());
        appointmentTable.refresh();
    } 
    
    public void allBtnHandler(ActionEvent event) throws IOException {
        appointmentTable.setItems(getAllAppointments());
        appointmentTable.refresh();
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
