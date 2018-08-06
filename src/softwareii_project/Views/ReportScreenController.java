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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import softwareii_project.Models.Appointment;
import softwareii_project.utils.Database;

/**
 *
 * @author Cole
 */
public class ReportScreenController implements Initializable {

    @FXML ComboBox contactCombo, customerCombo, meetingTypeCombo, monthCombo;
    @FXML TableView<Appointment> appointmentTable;
    @FXML TableColumn<Appointment, Integer> appointmentIDColumn;
    @FXML TableColumn<Appointment, String> contactColumn, meetingColumn, customerColumn, locationColumn, startColumn, endColumn;
    
    public final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public final String MEET_1 = "First Meeting";
    public final String MEET_2 = "Follow-up";
    public final String MEET_3 = "First Consultation";
    
    Connection dbConnection = null;
    Statement st = null;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        setColumns();
        setContactCombo();
        setCustomerCombo();
        setMeetingTypeCombo();
        setMonthCombo();
        appointmentTable.setItems(getAllAppointments());
        
    }
    
    public ObservableList<Appointment> getSearchedAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        
        String contact = "";
        String customerId = "";
        String meetingType = "";
        int month = -1;
        
        String query = "SELECT appointment.appointmentId, customer.customerName, appointment.description, appointment.location, appointment.contact, appointment.start, "
                    + "appointment.end FROM appointment, customer WHERE customer.customerId = appointment.customerId";
        
        String queryAdd1 = "";
        String queryAdd2 = "";
        String queryAdd3 = "";
        String queryAdd4 = "";
        
        if(contactCombo.getSelectionModel().getSelectedItem() != null) {
            contact = (String) contactCombo.getSelectionModel().getSelectedItem();
            queryAdd1 = " AND appointment.contact = '" + contact + "'";
        }
        if(customerCombo.getSelectionModel().getSelectedItem() != null) {
            customerId = (String) customerCombo.getSelectionModel().getSelectedItem();
            queryAdd2 = " AND customer.customerId = " + customerId;
        }
        if(meetingTypeCombo.getSelectionModel().getSelectedItem() != null) {
            meetingType = (String) meetingTypeCombo.getSelectionModel().getSelectedItem();
            queryAdd3 = " AND appointment.description = '" + meetingType + "'";
        }
        if(monthCombo.getSelectionModel().getSelectedItem() != null) {
            month = monthCombo.getSelectionModel().getSelectedIndex();
            month += 1;
            queryAdd4 = " AND MONTH(start) = " + month;
        }
        
        query += queryAdd1;
        query += queryAdd2;
        query += queryAdd3;
        query += queryAdd4;
        
        try {
            ResultSet r = st.executeQuery(query);

            while(r.next()) {
                appointments.add(new Appointment(Integer.parseInt(r.getString("appointmentId")), r.getString("contact"), r.getString("location"), 
                            r.getString("description"), r.getString("customerName"), r.getString("start"), r.getString("end")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return appointments;
    }
    
    public void resetBtnHandler() {
        monthCombo.setValue(null);
        contactCombo.setValue(null);
        customerCombo.setValue(null);
        meetingTypeCombo.setValue(null);
        appointmentTable.setItems(getAllAppointments());
    }
    
    public void setMonthCombo() {
        ObservableList<String> months = FXCollections.observableArrayList();
        for(int i = 0; i < MONTHS.length; i++) {
            months.add(MONTHS[i]);
        }
        
        monthCombo.setItems(months);
        
        monthCombo.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            appointmentTable.setItems(getSearchedAppointments());
                        }
                    });
    }
    
    public void setMeetingTypeCombo() {
        ObservableList<String> types = FXCollections.observableArrayList();
        types.add(MEET_1);
        types.add(MEET_2);
        types.add(MEET_3);
        
        meetingTypeCombo.setItems(types);
        
        meetingTypeCombo.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            appointmentTable.setItems(getSearchedAppointments());
                        }
                    });
    }
    
    public void setCustomerCombo() {
        ObservableList<String> customers = FXCollections.observableArrayList();
        
        
        try {
            dbConnection = Database.getConnection();
            st = dbConnection.createStatement();
            String query = "SELECT customerId FROM customer";

            ResultSet r = st.executeQuery(query);
            
            while(r.next()) {
                customers.add(r.getString("customerId"));
            }
            
            customerCombo.setItems(customers);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        customerCombo.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            appointmentTable.setItems(getSearchedAppointments());
                        }
                    });
    }
    
    public void setContactCombo() {
        ObservableList<String> contacts = FXCollections.observableArrayList();
        
        
        try {
            dbConnection = Database.getConnection();
            st = dbConnection.createStatement();
            String query = "SELECT userName FROM user";

            ResultSet r = st.executeQuery(query);
            
            while(r.next()) {
                contacts.add(r.getString("userName"));
            }
            
            contactCombo.setItems(contacts);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        contactCombo.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            appointmentTable.setItems(getSearchedAppointments());
                        }
                    });
    }
    
    
    //Returns all appointment objects to populate the appointment table
    public ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try {
            dbConnection = Database.getConnection();
            st = dbConnection.createStatement();

            String query = "SELECT appointment.appointmentId, customer.customerName, appointment.description, appointment.location, appointment.contact, appointment.start, "
                    + "appointment.end FROM appointment, customer WHERE customer.customerId = appointment.customerId";
            ResultSet results = st.executeQuery(query);
           
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
