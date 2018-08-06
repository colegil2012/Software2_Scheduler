/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii_project.Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Cole
 */
public class Customer {
    
    private SimpleIntegerProperty customerID;
    private SimpleStringProperty customerName;
    private SimpleIntegerProperty addressID;
    private SimpleIntegerProperty active;
    private SimpleStringProperty createDate;
    private SimpleStringProperty createdBy;
    private SimpleStringProperty lastUpdate;
    private SimpleStringProperty lastUpdateBy;
    
    public static int selectedCustomer = -1;
    public static int selectedAddress = -1;
    
    public static void setSelectedCustomer(int custID) {
        selectedCustomer = custID;
    }
    
    public static int getSelectedCustomer() {
        return selectedCustomer;
    }
    
    public static int getSelectedAddress() {
        return selectedAddress;
    }
    
    public static void setSelectedAddress(int addID) {
        selectedAddress = addID;
    }
    
    public Customer(int ID, String name) {
        this.customerID = new SimpleIntegerProperty(ID);
        this.customerName = new SimpleStringProperty(name);
    }
    
    public void setCustomerID(int ID) {
        this.customerID = new SimpleIntegerProperty(ID);
    } 
    
    public SimpleIntegerProperty custIDProperty() {
        return customerID;
    }
    
    public int getCustomerID() {
        return customerID.get();
    }
    
    public void setCustomerName(String name) {
        this.customerName = new SimpleStringProperty(name);
    }
    
    public SimpleStringProperty custNameProperty() {
        return customerName;
    }
    
    public void setAddressID(int ID) {
        this.addressID = new SimpleIntegerProperty(ID);
    }
    
    public SimpleIntegerProperty getAddressID() {
        return addressID;
    }
    
    public void setActive(int isActive) {
        this.active = new SimpleIntegerProperty(isActive);
    }
    
    public SimpleIntegerProperty getActive() {
        return active;
    }
    
    public void setCreateDate(String date) {
        this.createDate = new SimpleStringProperty(date);
    }
    
    public SimpleStringProperty getCreateDate() {
        return createDate;
    }
    
    public void setCreatedBy(String name) {
        this.createdBy = new SimpleStringProperty(name);
    }
    
    public SimpleStringProperty getCreatedBy() {
        return createdBy;
    }
    
    public void setLastUpdate(String update) {
        this.lastUpdate = new SimpleStringProperty(update);
    }
    
    public SimpleStringProperty getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdateBy(String name) {
        this.lastUpdateBy = new SimpleStringProperty(name);
    }
    
    public SimpleStringProperty getLastUpdateBy() {
        return lastUpdateBy;
    }
}
