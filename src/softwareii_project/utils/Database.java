/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii_project.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Cole
 */
public class Database {
         
        public static Connection conn = null;           
	public static String driver   = "com.mysql.jdbc.Driver";         
	public static String db       = "U05bQY";         
	public static String url      = "jdbc:mysql://52.206.157.109/" + db;         
	public static String user     = "U05bQY";         
	public static String pass     = "53688457914";   

        
        public Database() {}
                
        public static void connect() throws ClassNotFoundException {
            try {             
		Class.forName(driver);             
		conn = DriverManager.getConnection(url,user,pass);             
		System.out.println("Connected to database : " + db); 
            } catch (SQLException e) {             
		System.out.println("SQLException: "+e.getMessage());             
		System.out.println("SQLState: "+e.getSQLState());             
		System.out.println("VendorError: "+e.getErrorCode());         
		}  
        }  
        
        public static void disconnect() {
            try {
                conn.close();
            } catch ( SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
            }
        }
        
        
        public static Connection getConnection() {
            return conn;
        }
        
    
}

