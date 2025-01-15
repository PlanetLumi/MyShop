package clients.accounts;

import dbAccess.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import dbAccess.DBAccess;
import dbAccess.DBAccessFactory;
import security.Encryption;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDB {
        public static Connection theCon = null;
        public static DBAccess dbDriver = null;
        public static ResultSet rs = null;
        public static PreparedStatement pstmt = null;

        public List<String[]> getAllProductInfo(String description) {
                List<String[]> results = new ArrayList<>();
                DBAccessFactory.setAction("Create");
                try {
                        dbDriver = (new DBAccessFactory()).getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );

                        String getAllQuery = "SELECT * FROM products WHERE description LIKE ?";
                        pstmt = theCon.prepareStatement(getAllQuery);
                        pstmt.setString(1, "%" + description + "%"); // Use wildcard for partial matches
                        rs = pstmt.executeQuery();

                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        while (rs.next()) {
                                String[] row = new String[columnCount];
                                for (int i = 1; i <= columnCount; i++) {
                                        row[i - 1] = rs.getString(i); // Retrieve column data
                                }
                                results.add(row); // Add row to the results list
                        }
                } catch (Exception e) {
                        System.err.println("Error fetching product info: " + e.getMessage());
                        e.printStackTrace();
                } finally {
                        try {
                                if (rs != null) rs.close();
                                if (pstmt != null) pstmt.close();
                                if (theCon != null) theCon.close();
                        } catch (SQLException e) {
                                System.err.println("Error closing resources: " + e.getMessage());
                        }
                }

                return results; // Return all the results
        }
}
