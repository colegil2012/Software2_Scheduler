/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii_project.Models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import softwareii_project.utils.Database;

/**
 *
 * @author Cole
 */
public class LoggedUser {
    
    public static String userName = "";
    public static String password = "";
    
    public static void setUser(String name) {
        userName = name;
    }
    
    public static String getUser() {
        return userName;
    }
    
    
    //Static method to verify the username/password combo is valid to logon to the system
    public static Boolean verifyUser(String user, String pass) throws ClassNotFoundException, SQLException {
        
       Connection dbConnection = null;
       Statement statement = null;
       String query = "SELECT * FROM user WHERE userName='" + user + "' AND password='" + pass + "'";
       Boolean success = false;
        
       try {
           
           dbConnection = Database.getConnection();
           statement = dbConnection.createStatement();
     
           ResultSet results = statement.executeQuery(query);
           
           if(results.next()) {
               userName = results.getString("userName");
               //Logger.log(user, true);
               
               success = true;
           }
           
       } catch (SQLException e) {
           System.out.println("SQL Error: " + e.getMessage());
       }
       
       return success;
    }
    
    
}
