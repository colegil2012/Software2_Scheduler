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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import softwareii_project.Models.Appointment;
import softwareii_project.Models.LoggedUser;
import softwareii_project.utils.Database;

/**
 *
 * @author Cole
 */
public class ModAppointmentScreenController implements Initializable {
    
    @FXML ComboBox contactCombo, meetingTypeCombo, customerCombo, startCombo, endCombo;
    @FXML TextField locationText;
    @FXML DatePicker datePicker;

    Connection dbConnection = null;
    Statement st = null;
    
    private final ZoneId zoneId = ZoneId.systemDefault();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    
    
    public final String MEET_1 = "First Meeting";
    public final String MEET_2 = "Follow-up";
    public final String MEET_3 = "First Consultation";
    
    public final String AMANDA_LOCATION = "New York, New York";
    public final String GARRET_LOCATION = "Sydney, Australia";
    public final String LAFAWNDA_LOCATION = "London, England";
    
    public final String[] START_TIMES = {"9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00PM", "3:30 PM", "4:00 PM", "4:30 PM"};
    public final String[] END_TIMES = {"9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM"};

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
        try {
            dbConnection = Database.getConnection();
            st = dbConnection.createStatement();
            setContactCombo(dbConnection, st);
            setMeetingTypeCombo();
            setCustomerCombo(dbConnection, st);
            setStartCombo();
            setSelectedCustomerInfo();
            
        } catch (SQLException ex) {
            Logger.getLogger(AddAppointmentScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Instead of instantiating the DateCell Object, the object constructor is passed as an argument to the lambda expression
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate datePicker, boolean empty) {
                super.updateItem(datePicker, empty);
                setDisable(
                    empty || 
                    datePicker.getDayOfWeek() == DayOfWeek.SATURDAY || 
                    datePicker.getDayOfWeek() == DayOfWeek.SUNDAY ||
                    datePicker.isBefore(LocalDate.now()));
                if(datePicker.getDayOfWeek() == DayOfWeek.SATURDAY || datePicker.getDayOfWeek() == DayOfWeek.SUNDAY || datePicker.isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #c2c2d6;");
                }
            }
        });        

    }
    
    public void setSelectedCustomerInfo() throws SQLException {

        int id = Appointment.getSelectedAppointmentId();
        Appointment app;
        
        dbConnection = Database.getConnection();
        st = dbConnection.createStatement();
        
        String query = "SELECT appointment.appointmentId, appointment.customerid, appointment.description, appointment.location, appointment.contact, appointment.start, "
                    + "appointment.end FROM appointment, customer WHERE customer.customerId = appointment.customerId AND appointment.appointmentId = " + id;
        
        ResultSet r = st.executeQuery(query);
        
        while(r.next()) {
            
        app = new Appointment(Integer.parseInt(r.getString("appointmentId")), r.getString("contact"), r.getString("location"), 
            r.getString("description"), Integer.parseInt(r.getString("customerid")), r.getString("start"), r.getString("end"));

            LocalDateTime startTime = LocalDateTime.parse(app.startProperty().getValue());
            setDateAndTimeCombo(startTime);
            
            ObservableList<String> contacts = contactCombo.getItems();
            int contactIndex = contacts.indexOf(app.getContact());
            contactCombo.getSelectionModel().select(contactIndex);
            
            ObservableList<String> types = meetingTypeCombo.getItems();
            int typeIndex = types.indexOf(app.getMeetingType());
            meetingTypeCombo.getSelectionModel().select(typeIndex);
            
            ObservableList<Integer> custIDs = customerCombo.getItems();
            int custIdIndex = custIDs.indexOf(app.getCustomerID());
            customerCombo.getSelectionModel().select(custIdIndex);
            
        }
        
    }
    
    public void setDateAndTimeCombo(LocalDateTime ldt) {
        int hour = ldt.getHour();
        int minutes = ldt.getMinute();
        String suffix = "";
        
        if(hour == 9 || hour == 10 || hour == 11) {
            suffix = "AM";
        } else {
            suffix = "PM";
        }
        
        if(hour > 12) {
            hour = hour - 12;
        }
        
        String compareString;
        String hourString = Integer.toString(hour);
        String minutesString = Integer.toString(minutes);
        
        if(minutes == 0) {
            compareString = hourString + ":" + minutesString + "0 " + suffix;
        } else {
            compareString = hourString + ":" + minutesString + " " + suffix;
        }
        System.out.println(compareString);
        
        ObservableList<String> start = startCombo.getItems();
        int startIndex = start.indexOf(compareString);
        startCombo.getSelectionModel().select(startIndex);
        
        datePicker.setValue(ldt.toLocalDate());
        
    }
    
    public void onSaveBtnClick(ActionEvent event) throws IOException {
        String contact, meetingType, meetingTitle, location, start;
        int customer;
        int id = Appointment.getSelectedAppointmentId();
        LocalDate date;
        LocalTime startTime;
        
        if(validateForm()) {
            contact = (String) contactCombo.getSelectionModel().getSelectedItem();
            meetingType = (String) meetingTypeCombo.getSelectionModel().getSelectedItem();
            
            if(meetingType.equals(MEET_1)) {
                meetingTitle = "Meeting";
            } else {
                meetingTitle = "Consulting";
            }
            
            location = locationText.getText();
            customer = (Integer) customerCombo.getSelectionModel().getSelectedItem();
            
            date = datePicker.getValue();

            start = (String) startCombo.getSelectionModel().getSelectedItem();
            
            LocalDate localDate = date;
            startTime = LocalTime.parse(start, timeDTF);
            LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
            ZonedDateTime startUTC = startDT.atZone(zoneId).withZoneSameInstant(ZoneId.of("UTC"));
            Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime());
            
            LocalDateTime endDT = startDT.plusMinutes(30);
            ZonedDateTime endUTC = endDT.atZone(zoneId).withZoneSameInstant(ZoneId.of("UTC"));
            Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime());
           
            
            if(validateTime(contact, startsqlts, id)) {
                try {
                    dbConnection = Database.getConnection();
                    st = dbConnection.createStatement();

                    String update = "UPDATE appointment SET customerId = " + customer + ", title = '" + meetingTitle + "', description = '" + meetingType +
                            "', location = '" + location + "', contact = '" + contact + "', url = '', start = '" + startsqlts + "', end = '" + endsqlts +
                            "', lastUpdateBy = '" + LoggedUser.getUser() + "' WHERE appointmentId = " + id;
                            
                    
                    st.executeUpdate(update);

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("AppointmentScreen.fxml"));

                    Parent root = (Parent)fxmlLoader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);

                    stage.setScene(scene);

                    stage.show();

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Form Submission Error:");
                alert.setHeaderText(null);
                alert.setContentText(contact + " already has an appointment at this time, choose another time.");
                alert.showAndWait();
            }
        
        }
    }
    
    public Boolean validateForm() {
        Boolean valid = false;
        String errorBody = "";
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Form Submission Error:");
        alert.setHeaderText(null);
        
        if(contactCombo.getSelectionModel().getSelectedItem() != null) {
            if(meetingTypeCombo.getSelectionModel().getSelectedItem() != null) {
                if(customerCombo.getSelectionModel().getSelectedItem() != null) {
                    if(datePicker.getValue() != null) {
                        if(startCombo.getSelectionModel().getSelectedItem() != null) {
                                valid = true;
                        } else {
                            errorBody = "Start Time not selected.";
                        }
                    } else {
                        errorBody = "Date not selected.";
                    }
                } else {
                    errorBody = "Customer ID not selected.";
                }
            } else {
                errorBody = "Meeting Type not selected.";
            }
        } else {
            errorBody = "Contact not selected.";
        }
        if(!(errorBody.equals(""))) {
            alert.setContentText(errorBody);
            alert.showAndWait();
        }
        return valid;
    }
    
    
    public void onCancelBtnClick(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AppointmentScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.show();
    }
    
    public Timestamp getDate() {
        LocalDateTime now = LocalDateTime.now();
        
        ZonedDateTime startUTC = now.atZone(zoneId).withZoneSameInstant(ZoneId.of("UTC"));
        Timestamp ts = Timestamp.valueOf(startUTC.toLocalDateTime());
        
        return ts;
    }
    
    public Boolean validateTime(String contact, Timestamp ts, int id) {
        Boolean valid = true;
        
        try {
            dbConnection = Database.getConnection();
            st = dbConnection.createStatement();
            String query = "SELECT start FROM appointment WHERE contact = '" + contact + "' AND appointmentId <> " + id;
        
            ResultSet rs = st.executeQuery(query);
            
            System.out.println(ts);
            
            while(rs.next()) {
                if((rs.getTimestamp("start").toString()).equals(ts.toString())) {
                    valid = false;
                }
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return valid;
    }
    
    public void setContactCombo(Connection c, Statement st) throws SQLException {
        ObservableList<String> contacts = FXCollections.observableArrayList();
        
        String query = "SELECT * FROM user";
        ResultSet r = st.executeQuery(query);
        while(r.next()) {
            contacts.add(r.getString("userName"));
        }
        contactCombo.setItems(contacts);
        
        contactCombo.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
 
                        if(newValue.equals("Amanda")) {
                            locationText.setText(AMANDA_LOCATION);
                        } else if(newValue.equals("Garret")) {
                            locationText.setText(GARRET_LOCATION);
                        } else if(newValue.equals("LaFawnda")) {
                            locationText.setText(LAFAWNDA_LOCATION);
                        } else {
                            locationText.setText("online");
                        }
                }
        });
    }
    
     public void setMeetingTypeCombo() {
        ObservableList<String> types = FXCollections.observableArrayList();
        types.add(MEET_1);
        types.add(MEET_2);
        types.add(MEET_3);
        
        meetingTypeCombo.setItems(types);
    }
    
    public void setCustomerCombo(Connection c, Statement st) throws SQLException {
        ObservableList<Integer> custIds = FXCollections.observableArrayList();
        
        String query = "SELECT customerId FROM customer";
        ResultSet r = st.executeQuery(query);
        while(r.next()) {
            custIds.add(Integer.parseInt(r.getString("customerId")));
        }
        
        customerCombo.setItems(custIds);
        
    }
    
    public void setStartCombo() {
        ObservableList<String> times = FXCollections.observableArrayList();
        
        times.addAll(Arrays.asList(START_TIMES));
        
        startCombo.setItems(times);
        
    }
   
}
