package admin;

import java.sql.*;

import dbAccess.DBAccess;
import dbAccess.DBAccessFactory;
import security.Encryption;

import java.util.Observable;

public class AdminCreateTable {
    public static void main(String[] args) {
        TestDerbyDriver();
        Connection theCon = null;      // Connection to database
        DBAccess dbDriver = null;
        DBAccessFactory.setAction("Create");
        System.out.println("Runtime Classpath: " + System.getProperty("java.class.path"));

        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            System.out.println("Loading Derby driver...");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            System.out.println("Database URL: " + dbDriver.urlOfDatabase());
            dbDriver.loadDriver();
            System.out.println("Driver loaded successfully.");
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String dropTableSQL = "DROP TABLE admin_accounts";
            try (Statement stmt = theCon.createStatement()) {
                // Drop the table if it exists
                try {
                    stmt.executeUpdate(dropTableSQL);
                    System.out.println("Table dropped successfully!");
                } catch (SQLException e) {
                    System.out.println("Table does not exist, skipping drop.");
                }
            }
            String createTableSQL =
                    """
                    CREATE TABLE admin_accounts(
                    username VARCHAR(50) PRIMARY KEY,
                    password VARCHAR(255),
                    salt VARCHAR(255),
                    role VARCHAR(50)
                    )""";
            try(Statement stmt = theCon.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Table Admin Accounts created successfully");
            } catch(Exception e){
                 e.printStackTrace();
                 System.err.println("Failed to create table admin accounts");
            } finally{
                if(theCon != null){
                    try{
                        theCon.close();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public static void TestDerbyDriver() {
        try{
            System.out.println("Loading Derby Embedded Driver...");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            System.out.println("Driver loaded successfully!");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
