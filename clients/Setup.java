package clients;

import clients.admin.AdminController;
import dbAccess.DBAccess;
import dbAccess.DBAccessFactory;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * A Setup script that fully drops triggers, constraints, and tables in the correct order
 * to avoid Derby's NullPointerException issues, then recreates them.
 */
class Setup
{
  //------------------------------------------------------------------------------------
  // 1) Names of triggers to drop (if they exist)
  //------------------------------------------------------------------------------------
  private static final String[] DROP_TRIGGERS = {
          "AutoPopulateUserDetails",
          "AutoPopulateUserCardDetails",
          "AutoPopulateOrderHistory",
          "AutoPopulateBasket"
  };

  private static final String[] DROP_TABLES = {
          "UserCardDetails",
          "BasketItems",
          "Basket",
          "OrderHistory",
          "UserDetails",
          "Accounts",
          "StockTable",
          "ProductTable"
  };


  private static final String[] CREATE_AND_INSERT = {
          // 1) Create ProductTable
          "create table ProductTable (" +
                  "productNo      Char(4) Primary Key," +
                  "description    Varchar(40)," +
                  "picture        Varchar(80)," +
                  "price          Float)",

          // Insert sample rows
          "insert into ProductTable values ('0001', '40 inch LED HD TV', 'images/pic0001.jpg', 269.00)",
          "insert into ProductTable values ('0002', 'DAB Radio',         'images/pic0002.jpg', 29.99)",
          "insert into ProductTable values ('0003', 'Toaster',           'images/pic0003.jpg', 19.99)",
          "insert into ProductTable values ('0004', 'Watch',             'images/pic0004.jpg', 29.99)",
          "insert into ProductTable values ('0005', 'Digital Camera',    'images/pic0005.jpg', 89.99)",
          "insert into ProductTable values ('0006', 'MP3 player',        'images/pic0006.jpg', 7.99)",
          "insert into ProductTable values ('0007', '32Gb USB2 drive',   'images/pic0007.jpg', 6.99)",

          // 2) Create StockTable
          "create table StockTable (" +
                  "productNo      Char(4) Primary Key," +
                  "stockLevel     Integer)",

          // Insert stock
          "insert into StockTable values ('0001',  90)",
          "insert into StockTable values ('0002',  20)",
          "insert into StockTable values ('0003',  33)",
          "insert into StockTable values ('0004',  10)",
          "insert into StockTable values ('0005',  17)",
          "insert into StockTable values ('0006',  15)",
          "insert into StockTable values ('0007',   1)",

          // Example SELECT (optional)
          "select * from StockTable, ProductTable where StockTable.productNo = ProductTable.productNo",

          // 3) Create Accounts
          "create table Accounts (" +
                  "account_id BigInt Generated Always as Identity Primary Key," +
                  "username Varchar(50) Unique," +
                  "password Varchar(255)," +
                  "salt Varchar(255)," +
                  "role Varchar(50)," +
                  "locked BOOLEAN)",

          // 4) Create UserDetails
          "create table UserDetails (" +
                  "account_id BigInt Primary Key," +
                  "first_name Varchar(20)," +
                  "second_name Varchar(20)," +
                  "gender Varchar(20)," +
                  "date_of_birth DATE," +
                  "address Varchar(50)," +
                  "postcode Varchar(7)," +
                  "message Varchar(100)," +
                  "foreign key (account_id) references Accounts(account_id))",

          // 5) Create UserCardDetails
          "create table UserCardDetails (" +
                  "account_id BigInt Primary Key," +
                  "card_number Varchar(19)," +
                  "title Varchar(10)," +
                  "cardholder_name Varchar(20)," +
                  "foreign key (account_id) references Accounts(account_id))",

          // 6) Create Basket
          "create table Basket (" +
                  "basket_id BIGINT GENERATED ALWAYS AS IDENTITY Primary Key," +
                  "account_id BIGINT NOT NULL," +
                  "foreign key (account_id) references Accounts(account_id))",

          // 7) Create BasketItems
          "create table BasketItems (" +
                  "basket_id BIGINT," +
                  "productNo CHAR(4)," +
                  "quantity INT NOT NULL," +
                  "Primary Key (basket_id, productNo)," +
                  "foreign key (basket_id) references Basket(basket_id)," +
                  "foreign key (productNo) references ProductTable(productNo))",

          // 8) Create OrderHistory
          "create table OrderHistory (" +
                  "orderHistoryId BigInt Generated Always as Identity Primary Key," +
                  "account_id BigInt," +
                  "productNo Char(4)," +
                  "purchase_date DATE," +
                  "status Char(10)," +
                  "foreign key (account_id) references Accounts(account_id))",
          // 9) Create triggers
          "create trigger AutoPopulateUserDetails after insert on Accounts " +
                  "referencing new as NewAccount for each row " +
                  "insert into UserDetails (account_id) values (NewAccount.account_id)",

          "create trigger AutoPopulateUserCardDetails after insert on Accounts " +
                  "referencing new as NewAccount for each row " +
                  "insert into UserCardDetails (account_id) values (NewAccount.account_id)",


          "create trigger AutoPopulateBasket after insert on Accounts " +
                  "referencing new as NewAccount for each row " +
                  "insert into Basket (account_id) values (NewAccount.account_id)"
  };

  public static void main(String[] args)
  {
    System.out.println("Setup all databases");
    Connection theCon = null;

    //-------------------------------------------------------------------------
    // 0) Connect to Derby
    //-------------------------------------------------------------------------
    try {
      DBAccessFactory.setAction("Create");
      DBAccess dbDriver = (new DBAccessFactory()).getNewDBAccess();
      dbDriver.loadDriver();

      theCon = DriverManager.getConnection(
              dbDriver.urlOfDatabase(),
              dbDriver.username(),
              dbDriver.password()
      );
      System.out.println("Connected to " + dbDriver.urlOfDatabase());
    }
    catch (Exception e) {
      System.err.println("Cannot connect to DB: " + e.getMessage());
      return;  // Stop if no DB connection
    }

    //-------------------------------------------------------------------------
    // 1) Create a statement
    //-------------------------------------------------------------------------
    Statement stmt;
    try {
      stmt = theCon.createStatement();
    } catch (SQLException e) {
      System.err.println("Problems creating statement: " + e.getMessage());
      return;
    }

    //-------------------------------------------------------------------------
    // 2) Drop triggers if they exist (check in SYS.SYSTRIGGERS)
    //-------------------------------------------------------------------------
    for (String trig : DROP_TRIGGERS) {
      if (triggerExists(theCon, trig)) {
        dropTrigger(stmt, trig);
      } else {
        System.out.println("Trigger " + trig + " does not exist, skipping drop.");
      }
    }

    //-------------------------------------------------------------------------
    // 3) For each table: drop constraints, then drop the table
    //-------------------------------------------------------------------------
    for (String table : DROP_TABLES) {
      if (tableExists(theCon, table)) {
        dropAllConstraintsForTable(theCon, table);
        dropTable(stmt, table);
      } else {
        System.out.println("Table " + table + " does not exist, skipping drop.");
      }
    }

    //-------------------------------------------------------------------------
    // 4) Run CREATE and INSERT statements
    //-------------------------------------------------------------------------
    for (String sql : CREATE_AND_INSERT) {
      System.out.println(sql);
      try {
        stmt.execute(sql);
      } catch (SQLException e) {
        System.err.println("Error executing:\n" + sql + "\n" + e.getMessage());
        return;
      }
    }

    //-------------------------------------------------------------------------
    // 5) Now call Admin injection logic
    //-------------------------------------------------------------------------
    try {
      AdminController.injectAdmin();
      AdminController.injectEmployees();
      AdminController.injectUsers();
    } catch (Exception e) {
      throw new RuntimeException("Error in AdminController injection: ", e);
    }

    //-------------------------------------------------------------------------
    // 6) Close resources
    //-------------------------------------------------------------------------
    try {
      stmt.close();
      theCon.close();
      System.out.println("Setup complete.");
    } catch (SQLException e) {
      System.err.println("Error closing connection: " + e.getMessage());
    }
  }

  //----------------------------------------------------------------------------
  // Checks if a trigger with given name exists in SYS.SYSTRIGGERS
  //----------------------------------------------------------------------------
  private static boolean triggerExists(Connection con, String triggerName) {
    final String sql =
            "SELECT TRIGGERNAME FROM SYS.SYSTRIGGERS WHERE UPPER(TRIGGERNAME) = UPPER(?)";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, triggerName);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next(); // If a row is returned, it exists
      }
    } catch (SQLException e) {
      System.err.println("Error checking trigger " + triggerName + ": " + e.getMessage());
      return false;
    }
  }

  //----------------------------------------------------------------------------
  // Drops the given trigger if it exists
  //----------------------------------------------------------------------------
  private static void dropTrigger(Statement stmt, String trigName) {
    String sql = "DROP TRIGGER " + trigName;
    System.out.println(sql);
    try {
      stmt.execute(sql);
      System.out.println("Dropped trigger: " + trigName);
    } catch (SQLException e) {
      System.err.println("Error dropping trigger " + trigName + ": " + e.getMessage());
    }
  }

  //----------------------------------------------------------------------------
  // Check if a table with given name exists in SYS.SYSTABLES
  //----------------------------------------------------------------------------
  private static boolean tableExists(Connection con, String tableName) {
    final String sql =
            "SELECT TABLENAME FROM SYS.SYSTABLES WHERE UPPER(TABLENAME) = UPPER(?)";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, tableName);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      System.err.println("Error checking table " + tableName + ": " + e.getMessage());
      return false;
    }
  }

  //----------------------------------------------------------------------------
  // Drops the table
  //----------------------------------------------------------------------------
  private static void dropTable(Statement stmt, String tableName) {
    String sql = "DROP TABLE " + tableName;
    System.out.println(sql);
    try {
      stmt.execute(sql);
      System.out.println("Dropped table: " + tableName);
    } catch (SQLException e) {
      System.err.println("Error dropping table " + tableName + ": " + e.getMessage());
    }
  }

  //----------------------------------------------------------------------------
  // **Key**: drop all constraints (FK, PK, unique, check, etc.) from a table
  //----------------------------------------------------------------------------
  private static void dropAllConstraintsForTable(Connection con, String tableName) {
    final String sql =
            "SELECT c.constraintname, c.type " +
                    "FROM sys.sysconstraints c " +
                    "JOIN sys.systables t ON c.tableid = t.tableid " +
                    "WHERE UPPER(t.tablename) = UPPER(?)";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, tableName);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          String constraintName = rs.getString("constraintname");
          dropConstraint(con, tableName, constraintName);
        }
      }
    } catch (SQLException e) {
      System.err.println("Error dropping constraints for table " + tableName + ": " + e.getMessage());
    }
  }

  private static void dropConstraint(Connection con, String tableName, String constraintName) {
    String sql = "ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName;
    System.out.println(sql);
    try (Statement stmt = con.createStatement()) {
      stmt.execute(sql);
      System.out.println("Dropped constraint: " + constraintName + " from " + tableName);
    } catch (SQLException e) {
      System.err.println("Error dropping constraint " + constraintName + ": " + e.getMessage());
    }
  }
}
