package clients.accounts;

import catalogue.Basket;
import catalogue.Product;
import dbAccess.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import dbAccess.DBAccess;
import dbAccess.DBAccessFactory;
import security.Encryption;

import java.sql.*;
import java.util.*;

import static java.lang.Integer.parseInt;

public class ProductDB {
        public static Connection theCon = null;
        public static DBAccess dbDriver = null;
        public static ResultSet rs = null;
        public static PreparedStatement pstmt = null;

        public ProductDB() throws SQLException {
        }

        public List<Object[]> getAllProductInfo(String description) {
                List<Object[]> results = new ArrayList<>();
                DBAccessFactory.setAction("Create");
                try {
                        dbDriver = (new DBAccessFactory()).getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );

                        String getAllQuery = "SELECT * FROM ProductTable WHERE description LIKE ? OR productNo LIKE ?";
                        pstmt = theCon.prepareStatement(getAllQuery);
                        pstmt.setString(1, "%" + description.toUpperCase() + "%");
                        pstmt.setString(2, "%" + description.toUpperCase() + "%");
                        rs = pstmt.executeQuery();

                        System.out.println("Executing query: " + getAllQuery);
                        System.out.println("Search term: " + description);

                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        while (rs.next()) {
                                Object[] row = new Object[columnCount];
                                for (int i = 1; i <= columnCount; i++) {
                                        row[i - 1] = rs.getObject(i); // Retrieve column data
                                        System.out.println("Column " + i + ": " + row[i - 1]);
                                }
                                results.add(row); // Add the row to the results list
                        }

                        if (!results.isEmpty()) {
                                Object[] firstRow = results.get(0); // Get the first row
                                if (firstRow[0] != null) {
                                        String productNo = (firstRow[0].toString());
                                        Object stockLevel = getQuant(productNo);
                                        if (stockLevel != null) {
                                                System.out.println("Stock level for product " + productNo + ": " + stockLevel);
                                                // Add stock level to the row or process it as needed
                                                firstRow = Arrays.copyOf(firstRow, firstRow.length + 1);
                                                firstRow[firstRow.length - 1] = stockLevel;
                                                results.set(0, firstRow);
                                        }
                                }
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

        public Object getQuant(String productNo) {
                DBAccessFactory.setAction("Create");
                try {
                        dbDriver = (new DBAccessFactory()).getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );
                        System.out.println("Executing query: " + productNo);
                        String getQuery = "SELECT stockLevel FROM StockTable WHERE productNo = ?";
                        pstmt = theCon.prepareStatement(getQuery);
                        pstmt.setString(1, productNo); // Set productNo as the query parameter
                        rs = pstmt.executeQuery();

                        System.out.println("Executing query: " + getQuery);

                        if (rs.next()) {
                                return rs.getObject("stockLevel"); // Return the stock level
                        }
                } catch (Exception e) {
                        System.err.println("Error fetching stock level: " + e.getMessage());
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
                return null; // Return null if no stock level is found
        }

        public void addBasket(HashMap<Product, Integer> basket, int userID) {
                DBAccessFactory.setAction("Create"); // Setting the action to create
                try {
                        // Initialize database connection
                        dbDriver = (new DBAccessFactory()).getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );

                        // SQL to insert basket data into the UserBasket table
                        String insertSQL = "INSERT INTO BasketItems (basket_id, productNo, quantity, status) VALUES (?, ?, ?, ?)";

                        // Create PreparedStatement
                        PreparedStatement pstmt = theCon.prepareStatement(insertSQL);

                        // Iterate over the HashMap and insert each product and its quantity
                        for (Map.Entry<Product, Integer> entry : basket.entrySet()) {
                                Product product = entry.getKey();  // The product object
                                int quantity = entry.getValue();  // The quantity of the product

                                // Bind parameters
                                pstmt.setInt(1, userID);                // User ID
                                pstmt.setString(2, product.getProductNum()); // Product number
                                pstmt.setInt(3, quantity);              // Quantity
                                pstmt.setString(4, "open");

                                // Execute the INSERT statement
                                int rowsInserted = pstmt.executeUpdate();
                                if (rowsInserted > 0) {
                                        System.out.println("Basket item added: ProductNo " + product.getProductNum() + ", Quantity: " + quantity);
                                }
                        }

                        // Close resources
                        pstmt.close();
                        theCon.close();
                } catch (Exception e) {
                        // Handle exceptions
                        System.err.println("Error adding basket data: " + e.getMessage());
                        e.printStackTrace();
                }
        }

        public String[] getBskItmByContent(String content, int basketID) {
                DBAccessFactory.setAction("Create");
                try {
                        dbDriver = (new DBAccessFactory()).getNewDBAccess();
                        dbDriver = (new DBAccessFactory()).getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );
                        pstmt = theCon.prepareStatement("SELECT * FROM BasketItems WHERE basketID = ? AND description LIKE ?");
                        pstmt.setInt(1, basketID);
                        pstmt.setString(2, "%" + content + "%");
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                                String[] results = new String[5];
                                results[0] = rs.getString("basket_id");
                                results[1] = rs.getString("product_No");
                                results[2] = rs.getString("quantity");
                                return results;
                        } else {
                                return null;
                        }

                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        public int getBasketId(int userID) {
                DBAccessFactory.setAction("Create"); // Setting the action to create
                try {
                        // Initialize database connection
                        dbDriver = (new DBAccessFactory()).getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );
                        pstmt = theCon.prepareStatement(
                                "SELECT basket_id FROM Basket WHERE account_id = ?"
                        );
                        pstmt.setInt(1, userID);

                        rs = pstmt.executeQuery();
                        System.out.println("Executing query: " + userID);
                        if (rs.next()) {
                                return rs.getInt("basket_id");
                        }
                } catch (Exception ex) {
                        throw new RuntimeException(ex);
                }
                return 0;
        }

        public void drpBskItem(int basketID, int productID) {
                DBAccessFactory.setAction("Create");
                try {
                        dbDriver = (new DBAccessFactory()).getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );
                        pstmt = theCon.prepareStatement(
                                "DELETE FROM BasketItems WHERE productNo = ? AND basket_id = ?"
                        );
                        pstmt.setInt(1, productID);
                        pstmt.setInt(2, basketID);
                        int rowsInserted = pstmt.executeUpdate();
                        if (rowsInserted > 0) {
                                System.out.println("Successfully deleted basket item.");
                        }
                        pstmt.close();
                        theCon.close();
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        public void setNewQuantity(int basketID, int productID, int quantity) {
                DBAccessFactory.setAction("Create"); // Setting the action to create
                try {
                        // Initialize database connection
                        dbDriver = (new DBAccessFactory()).getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );
                        pstmt = theCon.prepareStatement("UPDATE BasketItems SET quantity = ? WHERE productNo = ? AND basketID = ?");
                        pstmt.setInt(1, quantity);
                        pstmt.setInt(2, productID);
                        pstmt.setInt(3, basketID);
                        int rowsInserted = pstmt.executeUpdate();
                        if (rowsInserted > 0) {
                                System.out.println("Successfguly updated");
                        }
                        pstmt.close();
                        theCon.close();
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        public void insertBasketIntoOrderHistory(long userId, Basket basket) throws Exception {
                // Suppose you have a DBAccess or something similar
                // We'll do direct JDBC here as an example

                // 1) Connect
                DBAccessFactory.setAction("Create"); // Setting the action to create
                dbDriver.loadDriver();
                try (Connection con = DriverManager.getConnection(
                        dbDriver.urlOfDatabase(),
                        dbDriver.username(),
                        dbDriver.password())) {
                        // 2) Prepare an INSERT statement for OrderHistory
                        String insertSQL =
                                "INSERT INTO OrderHistory (account_id, productNo, purchase_date, status) VALUES (?, ?, CURRENT_DATE, ?)";
                        try (PreparedStatement ps = con.prepareStatement(insertSQL)) {

                                // 3) For each product in the basket:
                                for (Map.Entry<Product, Integer> entry : basket.returnProductPurchaseInfo().entrySet()) {
                                        Product p = entry.getKey();

                                        ps.setLong(1, userId);
                                        ps.setString(2, p.getProductNum());
                                        //  rely on "CURRENT_DATE" for the 3rd column
                                        ps.setString(3, "open");
                                        ps.addBatch();
                                }
                                // 4) Execute batch
                                ps.executeBatch();
                        }
                }
        }

        public void updateStockLevel(String productNo, int purchasedQty) throws Exception {
                DBAccessFactory.setAction("Create");
                dbDriver.loadDriver();

                try (Connection con = DriverManager.getConnection(
                        dbDriver.urlOfDatabase(),
                        dbDriver.username(),
                        dbDriver.password())) {
                        // 1) Fetch the current stock level
                        String selectSQL = "SELECT stockLevel FROM StockTable WHERE productNo = ?";
                        try (PreparedStatement psSel = con.prepareStatement(selectSQL)) {
                                psSel.setString(1, productNo);
                                try (ResultSet rsSel = psSel.executeQuery()) {
                                        if (!rsSel.next()) {
                                                System.out.println("No stock row found for product " + productNo);
                                                return;
                                        }
                                        int currentStock = rsSel.getInt("stockLevel");
                                        int newStock = currentStock - purchasedQty;
                                        if (newStock < 0) newStock = 0; // or handle negative differently

                                        // 2) Update the stock level
                                        String updateSQL = "UPDATE StockTable SET stockLevel = ? WHERE productNo = ?";
                                        try (PreparedStatement psUpd = con.prepareStatement(updateSQL)) {
                                                psUpd.setInt(1, newStock);
                                                psUpd.setString(2, productNo);
                                                psUpd.executeUpdate();
                                                System.out.println("Updated stock for " + productNo
                                                        + ": old=" + currentStock
                                                        + ", purchased=" + purchasedQty
                                                        + ", new=" + newStock);
                                        }
                                }
                        }
                }
        }

        public List<Map<String, Object>> fetchUnpackedOrdersFromDB() {
                List<Map<String, Object>> results = new ArrayList<>();

// 1) Open DB connection
                DBAccessFactory.setAction("Create");
                try {
                        dbDriver = new DBAccessFactory().getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );

// 2) Suppose your OrderHistory has columns:
//    orderHistoryId, account_id, productNo, purchase_date, isPacked
//    We only want rows where isPacked = false
                        String sql = "SELECT orderHistoryId, account_id, productNo, purchase_date "
                                + "FROM OrderHistory "
                                + "WHERE status = 'open'";  //

                        try (PreparedStatement ps = theCon.prepareStatement(sql)) {
                                try (ResultSet rs = ps.executeQuery()) {

                                        // 3) Iterate result set, building a map per row
                                        while (rs.next()) {
                                                Map<String, Object> row = new HashMap<>();
                                                row.put("orderHistoryId", rs.getLong("orderHistoryId"));
                                                row.put("account_id", rs.getLong("account_id"));
                                                row.put("productNo", rs.getString("productNo"));
                                                row.put("purchase_date", rs.getDate("purchase_date"));
                                                // etc. add more columns if you want
                                                results.add(row);
                                        }
                                }
                        }
                } catch (SQLException ex) {
                        // Handle or log
                        ex.printStackTrace();
                } catch (Exception e) {
                        throw new RuntimeException(e);
                } finally {
                        // 4) Clean up
                        if (theCon != null) {
                                try {
                                        theCon.close();
                                } catch (SQLException e) { /* log */ }
                        }
                }

                return results;

        }

        public void setOrderStatus(long orderId, String status) {
                DBAccessFactory.setAction("Create");
                try {
                        dbDriver = new DBAccessFactory().getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );
                        String sql = "UPDATE OrderHistory SET status = ? WHERE orderHistoryId = ?";
                        try (PreparedStatement ps = theCon.prepareStatement(sql)) {
                                ps.setString(1, status);
                        } catch (SQLException e){
                                e.printStackTrace();
                        }
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }
        public void updateProductDetails(String productId, String description, double newPrice, int stock){
                DBAccessFactory.setAction("Create");
                try {
                        dbDriver = new DBAccessFactory().getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );
                        String sql = "UPDATE ProductTable SET description = ?, price = ? WHERE productNo = ?";
                        String secondSQL = "UPDATE StockTable SET stockLevel = ? WHERE productNo = ?";
                        try (PreparedStatement ps = theCon.prepareStatement(sql)) {
                                ps.setString(1, description);
                                ps.setFloat(2, (float) newPrice);
                                ps.setString(3, productId);
                                ps.executeUpdate();

                        } catch (SQLException e){
                                e.printStackTrace();
                        }
                        try(PreparedStatement ps2 = theCon.prepareStatement(secondSQL)) {
                                ps2.setInt(1, stock);
                                ps2.setString(2, productId);
                                ps2.executeUpdate();
                        } catch (SQLException e){
                                e.printStackTrace();
                        }
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }
        public List<Object[]> searchProducts(String searchTerm){
                DBAccessFactory.setAction("Create");
                try{
                        dbDriver = new DBAccessFactory().getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );
                        String sql = "SELECT * FROM ProductTable WHERE productNo = ? OR UPPER(description) LIKE UPPER(?) OR price = ?";
                        PreparedStatement ps = theCon.prepareStatement(sql);
                        try {
                                ps.setString(1, searchTerm);
                                ps.setString(2, "%" + searchTerm + "%");
                                double numericSearchTerm = Double.parseDouble(searchTerm);
                                ps.setDouble(3, numericSearchTerm); // set the price parameter
                        } catch (NumberFormatException e) {
                                ps.setNull(3, java.sql.Types.DOUBLE); // set price to NULL if searchTerm is not numeric
                        }
                        List<Object[]> results = new ArrayList<>();
                        try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                        String secondSQL = "SELECT stockLevel FROM StockTable WHERE productNo = ?";
                                        PreparedStatement ps2 = theCon.prepareStatement(secondSQL);
                                        ps2.setString(1, rs.getString("productNo"));

                                        try (ResultSet rs2 = ps2.executeQuery()) {
                                                if (rs2.next()) {
                                                        results.add(new Object[] { rs.getString("productNo"), rs.getString("description"), rs.getFloat("price"), rs2.getInt("stockLevel") });
                                                        return results;
                                                }
                                        }
                                }
                        }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            return List.of();
        }
        public void addStock(String productNo, int stock) throws SQLException {
                DBAccessFactory.setAction("Create");
                try {
                        dbDriver = new DBAccessFactory().getNewDBAccess();
                        dbDriver.loadDriver();
                        theCon = DriverManager.getConnection(
                                dbDriver.urlOfDatabase(),
                                dbDriver.username(),
                                dbDriver.password()
                        );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                String sql = "UPDATE StockTable SET stockLevel = ? WHERE productNo = ?";
                PreparedStatement ps = theCon.prepareStatement(sql);
                ps.setInt(1, stock);
                ps.setString(2, productNo);
                ps.executeUpdate();
        }
}


