package admin;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

import dbAccess.DBAccess;
import dbAccess.DBAccessFactory;
import security.Encryption;
import java.util.Observable;

public class AdminModel extends Observable {
    public static void callCreate() {
        AdminCreateTable.main(null);
    }
    public static void populateAccount() throws NoSuchAlgorithmException {
        String username = "admin1";
        String salt = Encryption.generateSalt();
        String password = Encryption.hashPassword("Jimmy08!", salt);
        String role = "admin";
        Connection theCon    = null;      // Connection to database
        DBAccess   dbDriver  = null;
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String sql = "INSERT INTO admin_accounts (username, password, salt, role) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = theCon.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, salt);
            pstmt.setString(4, role);
            int rowInserted = pstmt.executeUpdate();
            if (rowInserted > 0) {
                System.out.println("Account created successfully");
            }
            pstmt.close();
            theCon.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void loginAdmin(String username, String password) {
        Connection theCon    = null;
        DBAccess   dbDriver  = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        DBAccessFactory.setAction("Create");
        try{
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String sql = "SELECT password, salt, role FROM admin_accounts WHERE username = ?";
            stmt = theCon.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if(rs.next()) {
                String storedPassword = rs.getString("password");
                String storedSalt = rs.getString("salt");
                String storedRole = rs.getString("role");
                String hashedPassword = Encryption.hashPassword(password, storedSalt);
                if (hashedPassword.equals(storedPassword) && storedRole.equals("admin")) {
                    System.out.println("Login successful.");
                } else {
                    System.out.println("Login failed.");
                }
            }else{
                    System.out.println("Username not found");
                }
            }catch(Exception e){
                e.printStackTrace();
            } finally {
                // Close resources
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (theCon != null) theCon.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    public static void createAdminAccount(){

    }
}
