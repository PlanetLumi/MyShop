package clients.accounts;

import dbAccess.DBAccess;
import dbAccess.DBAccessFactory;
import security.Encryption;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountCreation {
    public static Connection theCon = null;
    public static DBAccess dbDriver = null;
    public static ResultSet rs = null;
    public static PreparedStatement pstmt = null;

    public void createAccount(String username, String password, String role) throws SQLException {
        DBAccessFactory.setAction("Create");
        String salt = Encryption.generateSalt();
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            password = Encryption.hashPassword(password, salt);
            String creationSQL = "INSERT INTO Accounts (username, password, salt, role, locked) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = theCon.prepareStatement(creationSQL);
            // Bind parameters
            pstmt.setString(1, username); // Bind username
            pstmt.setString(2, password); // Bind hashed password
            pstmt.setString(3, salt);     // Bind salt
            pstmt.setString(4, role);     // Bind role
            pstmt.setBoolean(5, false);

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

    public void lockUser(long userID, boolean locked) {
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String updateSQL = "UPDATE Accounts SET locked = ? WHERE account_id = ?";
            pstmt = theCon.prepareStatement(updateSQL);
            pstmt.setBoolean(1, locked);
            pstmt.setLong(2, userID);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Account is locked?" + locked);
            } else {
                System.out.println("Account has not changed lock");
            }
            pstmt.close();
            theCon.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkLock(long userID) {
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String checkTable = "SELECT locked FROM Accounts WHERE account_id = ?";
            pstmt = theCon.prepareStatement(checkTable);
            pstmt.setLong(1, userID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("locked");
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void newData(String tableName, String columnName, String[] values, long userID) {
        DBAccessFactory.setAction("Create");

        if (!tableName.matches("[a-zA-Z0-9_]+") || !columnName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Invalid table or column name.");
        }

        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection(
                    dbDriver.urlOfDatabase(),
                    dbDriver.username(),
                    dbDriver.password()
            );
            System.out.println(tableName + columnName + values[0] + userID);
            String updateSQL = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE account_id = ?";
            pstmt = theCon.prepareStatement(updateSQL);

            for (String value : values) {
                System.out.println(value);
                pstmt.setString(1, value);
                pstmt.setLong(2, userID);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Updated column " + columnName + " to: " + value);
                } else {
                    System.out.println("No rows updated. Check account_id: " + userID);
                    String testSQL = "SELECT * FROM Accounts WHERE account_id = ?";
                    pstmt = theCon.prepareStatement(updateSQL);
                }
            }
        } catch (Exception e) {
            System.err.println("SQL Error: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (theCon != null) theCon.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    public boolean checkAccount(String username) throws SQLException {
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String checkTable = "SELECT * FROM Accounts WHERE username = ?";
            pstmt = theCon.prepareStatement(checkTable);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                rs.close();
                pstmt.close();
                theCon.close();
                return true;
            } else {
                rs.close();
                pstmt.close();
                theCon.close();
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] getAll(long ID) throws SQLException {
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String readAccountSQL = "SELECT * FROM UserDetails WHERE account_id = ?";
            pstmt = theCon.prepareStatement(readAccountSQL);
            pstmt.setLong(1, ID);
            rs = pstmt.executeQuery();
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
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String readIDSQL = "SELECT account_id FROM Accounts WHERE username = ?";
            pstmt = theCon.prepareStatement(readIDSQL);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
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

    public Object[] loginAccount(String username, String password) {
        Object[] userData = new Object[2];
        DBAccessFactory.setAction("Create");
        System.out.println(username);
        System.out.println(password);
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String sql = "SELECT account_id, password, salt FROM Accounts WHERE username = ?";
            pstmt = theCon.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                System.out.println("PASSWORD," + storedPassword);
                String storedSalt = rs.getString("salt");
                System.out.println(storedSalt);
                long storedID = rs.getLong("account_id");
                System.out.println(storedID);
                String hashedPassword = Encryption.hashPassword(password, storedSalt);
                System.out.println(hashedPassword);
                if (!checkLock(storedID)) {
                    if (hashedPassword.equals(storedPassword)) {
                        password = null;
                        storedPassword = null;
                        System.out.println("Login successful.");
                        userData[0] = storedID;
                        userData[1] = (UUID.randomUUID());
                        return userData;
                    } else {
                        password = null;
                        storedPassword = null;
                        System.out.println("Login failed.");
                        return null;
                    }
                } else {
                    System.out.println("Account locked.");
                    return null;
                }
            } else {
                password = null;
                System.out.println("Username not found");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        password = null;
        return null;
    }

    public String getRole(long ID) throws SQLException {
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String readAccountSQL = "SELECT role FROM Accounts WHERE account_id = ?";
            pstmt = theCon.prepareStatement(readAccountSQL);
            pstmt.setLong(1, ID);
            rs = pstmt.executeQuery();
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

    public List<String> getUser(String query, String role, boolean showLockedOnly) {
        List<String> employeelist = new ArrayList<>();
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            // Base SQL query
            String readAccountSQL = "SELECT username FROM Accounts WHERE username LIKE ? AND role = ? ";

            // Add condition for locked accounts if necessary
            if (showLockedOnly) {
                readAccountSQL += "AND locked = true ";
            }

            // Append the order and limit clause
            readAccountSQL += "ORDER BY username ASC FETCH FIRST 20 ROWS ONLY";
            pstmt = theCon.prepareStatement(readAccountSQL);
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, role);
            rs = pstmt.executeQuery();
            role = null;
            while (rs.next()) {
                employeelist.add(rs.getString("username"));
            }
            rs.close();
            pstmt.close();
            theCon.close();
            return employeelist;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public List<Object> readData(String tableName, String[] columnNames, long userID) throws SQLException {
        DBAccessFactory.setAction("Create");
        List<Object> userData = new ArrayList<>();

        // Validate table name and column names to avoid SQL injection
        if (tableName == null || tableName.isEmpty() || columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException("Invalid table name or column names.");
        }

        // Construct the SQL query dynamically
        StringBuilder readSQL = new StringBuilder("SELECT ");
        for (int i = 0; i < columnNames.length; i++) {
            readSQL.append(columnNames[i]);
            if (i < columnNames.length - 1) {
                readSQL.append(", ");
            }
        }
        readSQL.append(" FROM ").append(tableName).append(" WHERE account_id = ?");

        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection(
                    dbDriver.urlOfDatabase(),
                    dbDriver.username(),
                    dbDriver.password()
            );

            // Prepare the SQL statement
            pstmt = theCon.prepareStatement(readSQL.toString());
            pstmt.setLong(1, userID);

            // Execute the query and process the results
            rs = pstmt.executeQuery();
            while (rs.next()) {
                for (String column : columnNames) {
                    userData.add(rs.getObject(column)); // Fetch each column value
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // Close resources
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (theCon != null) theCon.close();
        }

        return userData;
    }

    public void testSQL(long userID) throws SQLException {
        DBAccessFactory.setAction("Create");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection
                    (dbDriver.urlOfDatabase(),
                            dbDriver.username(),
                            dbDriver.password());
            String readAccountSQL = "SELECT * FROM UserDetails WHERE account_id = ?";
            pstmt = theCon.prepareStatement(readAccountSQL);
            pstmt.setLong(1, userID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Account Found:");
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println(metaData.getColumnName(i) + ": " + rs.getString(i));
                }
            } else {
                System.out.println("Account not found");
            }
            rs.close();
            pstmt.close();
            theCon.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}