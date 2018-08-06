/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii_project.Models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Cole
 */
public class Appointment {
    
    private SimpleIntegerProperty appointmentID;
    private SimpleStringProperty contact;
    private SimpleStringProperty location;
    private SimpleStringProperty meetingType;
    private SimpleStringProperty customerName;
    private SimpleStringProperty start;
    private SimpleStringProperty end;
    private SimpleIntegerProperty customerId;
    
    public static int selectedAppointmentId = -1;
    
    public static void setSelectedAppointmentId(int id) {
        selectedAppointmentId = id;
    } 
    
    public static int getSelectedAppointmentId() {
        return selectedAppointmentId;
    }
    
    public Appointment(int id, String con, String loc, String mt, String cust, String st, String end) {
        this.appointmentID = new SimpleIntegerProperty(id);
        this.contact = new SimpleStringProperty(con);
        this.location = new SimpleStringProperty(loc);
        this.meetingType = new SimpleStringProperty(mt);
        this.customerName = new SimpleStringProperty(cust);
        this.start = new SimpleStringProperty(st);
        this.end = new SimpleStringProperty(end);
    }
    
    public Appointment(int id, String con, String loc, String mt, int custID, String st, String end) {
        this.appointmentID = new SimpleIntegerProperty(id);
        this.contact = new SimpleStringProperty(con);
        this.location = new SimpleStringProperty(loc);
        this.meetingType = new SimpleStringProperty(mt);
        this.customerId = new SimpleIntegerProperty(custID);
        this.start = new SimpleStringProperty(st);
        this.end = new SimpleStringProperty(end);
    }
    
    public SimpleIntegerProperty appointmentIDProperty() {
        return appointmentID;
    }
    
    public SimpleIntegerProperty customerIDProperty() {
        return customerId;
    }
    
    public int getCustomerID() {
        return customerId.get();
    }
    
    public void setAppointmentID(int id) {
        appointmentID = new SimpleIntegerProperty(id);
    }
    
    public int getAppointmentID() {
        return appointmentID.get();
    }
    
    public SimpleStringProperty contactProperty() {
        return contact;
    }
    
    public void setContact(String c) {
        contact = new SimpleStringProperty(c);
    }
    
    public String getContact() {
        return contact.get();
    }
    
    public SimpleStringProperty locationProperty() {
        return location;
    }
    
    public void setLocation(String l) {
        location = new SimpleStringProperty(l);
    }
    
    public String getLocation() {
        return location.get();
    }
    
    public SimpleStringProperty meetingTypeProperty() {
        return meetingType;
    }
    
    public void setMeetingType(String mt) {
        meetingType = new SimpleStringProperty(mt);
    }
    
    public String getMeetingType() {
        return meetingType.get();
    }
    
    public SimpleStringProperty customerNameProperty() {
        return customerName;
    }
    
    public void setCustomer(String c) {
        customerName = new SimpleStringProperty(c);
    }
    
    public String getCustomer() {
        return customerName.get();
    }
    
    public SimpleStringProperty startProperty() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"); 
 	LocalDateTime ldt = LocalDateTime.parse(this.start.getValue(), df);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime utcDate = zdt.withZoneSameInstant(zid); 
        SimpleStringProperty date = new SimpleStringProperty(utcDate.toLocalDateTime().toString());
        return date;
    }
    
    public void setStart(String s) {
        start = new SimpleStringProperty(s);
    }
    
    public String getStart() {
        return start.get();
    }
    
    public SimpleStringProperty endProperty() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"); 
 	LocalDateTime ldt = LocalDateTime.parse(this.end.getValue(), df);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime utcDate = zdt.withZoneSameInstant(zid); 
        SimpleStringProperty date = new SimpleStringProperty(utcDate.toLocalDateTime().toString());
        return date;
    }
    
    public void setEnd(String e) {
        end = new SimpleStringProperty(e);
    }
    
    public String getEnd() {
        return end.get();
    }
}
