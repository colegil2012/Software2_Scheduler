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
import softwareii_project.Models.LoggedUser;
import softwareii_project.utils.Database;

/**
 *
 * @author Cole
 */
public class AddAppointmentScreenController implements Initializable {
    
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
        } catch (SQLException ex) {
            Logger.getLogger(AddAppointmentScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
    
    public void onCancelBtnClick(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AppointmentScreen.fxml"));

            Parent root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.show();
    }
    
    public void onSaveBtnClick(ActionEvent event) throws SQLException, IOException {
        String contact, meetingType, meetingTitle, location, start, end;
        int customer;
        Timestamp now;
        LocalDate date;
        LocalTime startTime, endTime;
        
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
            
            now = getDate();
            
            if(validateTime(contact, startsqlts)) {
                try {
                    dbConnection = Database.getConnection();
                    st = dbConnection.createStatement();

                    String update = "INSERT INTO appointment (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdateBy)" +
                            " VALUES (" + customer + ",'" + meetingTitle + "','" + meetingType + "','" + location + "','" + contact + "','','"
                            + startsqlts + "','" + endsqlts + "','" + now + "','" + LoggedUser.getUser() + "','" + LoggedUser.getUser() + "')";

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
    
   
    public Timestamp getDate() {
        LocalDateTime now = LocalDateTime.now();
        
        ZonedDateTime startUTC = now.atZone(zoneId).withZoneSameInstant(ZoneId.of("UTC"));
        Timestamp ts = Timestamp.valueOf(startUTC.toLocalDateTime());
        
        return ts;
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
    
    public Boolean validateTime(String contact, Timestamp ts) {
        Boolean valid = true;
        
        try {
            dbConnection = Database.getConnection();
            st = dbConnection.createStatement();
            String query = "SELECT start FROM appointment WHERE contact = '" + contact + "'";
        
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
 
                    switch (newValue) {
                        case "Amanda":
                            locationText.setText(AMANDA_LOCATION);
                            break;
                        case "Garret":
                            locationText.setText(GARRET_LOCATION);
                            break;
                        case "LaFawnda":
                            locationText.setText(LAFAWNDA_LOCATION);
                            break;
                        default:
                            locationText.setText("online");
                            break;
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
