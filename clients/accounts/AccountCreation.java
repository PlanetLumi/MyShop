package clients.accounts;

import dbAccess.DBAccess;
import dbAccess.DBAccessFactory;
import security.Encryption;

import java.sql.*;

public class AccountCreation {
    public void createAccount(String username, String password, String salt, String role) throws SQLException {
        Connection theCon;      // Connection to database
        DBAccess dbDriver;
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String creationSQL = "INSERT INTO Accounts (username, password, salt, role) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = theCon.prepareStatement(creationSQL);
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

    public static String[] getAll(long ID) throws SQLException {
        Connection theCon;
        DBAccess dbDriver;
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String readAccountSQL = "SELECT * FROM UserDetails WHERE account_id = ?";
            PreparedStatement pstmt = theCon.prepareStatement(readAccountSQL);
            pstmt.setLong(1, ID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int columnCount = rs.getMetaData().getColumnCount();
                String[] accounts = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    accounts[i] = rs.getMetaData().getColumnName(i);
                }
                rs.close();
                pstmt.close();
                theCon.close();
                return accounts;
            } else {
                System.out.println("Account not found");
                pstmt.close();
                theCon.close();
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getID(String username) {
        Connection theCon;      // Connection to database
        DBAccess dbDriver;
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String readIDSQL = "SELECT account_id FROM Accounts WHERE username = ?";
            PreparedStatement pstmt = theCon.prepareStatement(readIDSQL);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                pstmt.close();
                theCon.close();
                return rs.getLong("account_id");
            } else {
                System.out.println("Account not found");
                pstmt.close();
                theCon.close();
                return -1;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long loginAccount(String username, String password) {
        Connection theCon;
        DBAccess dbDriver;
        ResultSet rs;
        PreparedStatement stmt;
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String sql = "SELECT account_id, password, salt FROM Accounts WHERE username = ?";
            stmt = theCon.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String storedSalt = rs.getString("salt");
                long storedID = rs.getLong("account_id");
                String hashedPassword = Encryption.hashPassword(password, storedSalt);
                if (hashedPassword.equals(storedPassword)) {
                    password = null;
                    storedPassword = null;
                    System.out.println("Login successful.");
                    String[] userData = getAll(storedID);
                    Accounts user = new Accounts(storedID, userData[1] + userData[2], username);
                    SessionManager sessionManager = SessionManager.getInstance();
                    String sessionId = "101";
                    sessionManager.login(sessionId, user);
                    return storedID;
                } else {
                    password = null;
                    storedPassword = null;
                    System.out.println("Login failed.");
                    return -1;
                }
            } else {
                password = null;
                System.out.println("Username not found");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        password = null;
        return -1;
    }

    public String getRole(long ID) throws SQLException {
        Connection theCon;
        DBAccess dbDriver;
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String readAccountSQL = "SELECT role FROM Accounts WHERE account_id = ?";
            PreparedStatement pstmt = theCon.prepareStatement(readAccountSQL);
            pstmt.setLong(1, ID);
            ResultSet rs = pstmt.executeQuery();
            String role = null;
            if (rs.next()) {
                role = rs.getString("role");
            } else {
                System.out.println("Account not found");
            }
            rs.close();
            pstmt.close();
            theCon.close();
            return role;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
