package clients;

import clients.admin.AdminModel;
import dbAccess.DBAccess;
import dbAccess.DBAccessFactory;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

import clients.admin.AdminController;

/**
 * Repopulate the database with test data
 * @author  Mike Smith University of Brighton
 * @version 3.0 Derby
 */

class Setup 
{
  private static String[] sqlStatements = {

//  " SQL code to set up database tables",

//  "drop table ProductList",
//  "drop table StockList",
    "drop trigger AutoPopulateUserDetails",
    "drop trigger AutoPopulateUserCardDetails",
    "drop trigger AutoPopulateOrderHistory",
    "drop table UserCardDetails",
    "drop table OrderHistory",
    "drop table UserDetails",
    "drop table Accounts",
    "drop table StockTable",
    "drop table ProductTable",
  "create table ProductTable ("+
      "productNo      Char(4)," +
      "description    Varchar(40)," +
      "picture        Varchar(80)," +
      "price          Float)",

  "insert into ProductTable values " +
     "('0001', '40 inch LED HD TV', 'images/pic0001.jpg', 269.00)",
  "insert into ProductTable values " +
     "('0002', 'DAB Radio',         'images/pic0002.jpg', 29.99)",
  "insert into ProductTable values " +
     "('0003', 'Toaster',           'images/pic0003.jpg', 19.99)",
  "insert into ProductTable values " +
     "('0004', 'Watch',             'images/pic0004.jpg', 29.99)",
  "insert into ProductTable values " +
     "('0005', 'Digital Camera',    'images/pic0005.jpg', 89.99)",
  "insert into ProductTable values " +
     "('0006', 'MP3 player',        'images/pic0006.jpg', 7.99)",
  "insert into ProductTable values " +
     "('0007', '32Gb USB2 drive',   'images/pic0007.jpg', 6.99)",
//  "select * from ProductTable",


  "create table StockTable ("+
      "productNo      Char(4)," +
      "stockLevel     Integer)",

  "insert into StockTable values ( '0001',  90 )",
  "insert into StockTable values ( '0002',  20 )",
  "insert into StockTable values ( '0003',  33 )",
  "insert into StockTable values ( '0004',  10 )",
  "insert into StockTable values ( '0005',  17 )",
  "insert into StockTable values ( '0006',  15 )",
  "insert into StockTable values ( '0007',  01 )",

  "select * from StockTable, ProductTable " +
          " where StockTable.productNo = ProductTable.productNo",


  "create table Accounts (" +
      "account_id BigInt Generated Always as Identity Primary Key," +
      "username Varchar(50) Unique," +
      "password Varchar(255)," +
      "salt Varchar(255)," +
      "role Varchar(50) ," +
      "locked BOOLEAN)",

  "create table UserDetails (" +
       "account_id BigInt Generated Always as Identity Primary Key," +
       "first_name Varchar(20)," +
       "second_name Varchar(20)," +
       "gender Varchar(20)," +
       "date_of_birth DATE," +
       "address Varchar(50)," +
       "postcode Varchar(7)," +
       "message Varchar(100)," +
       "foreign key (account_id) references Accounts(account_id))",


  "create table UserCardDetails (" +
        "account_id BigInt Primary Key," +
        "card_number Varchar(19)," +
        "title Varchar(10)," +
        "cardholder_name Varchar(20)," +
        "foreign key (account_id) references Accounts(account_id))",

  "create table OrderHistory (" +
        "account_id BigInt Primary Key," +
        "productNo Char(4)," +
        "purchase_date DATE," +
        "foreign key (account_id) references Accounts(account_id))",
        "Create Trigger AutoPopulateUserDetails After Insert On Accounts Referencing New As NewAccount for Each Row Insert Into UserDetails (account_id) Values (NewAccount.account_id)",
        "Create Trigger AutoPopulateUserCardDetails After Insert On Accounts Referencing New As NewAccount for Each Row Insert Into UserCardDetails (account_id) Values (NewAccount.account_id)",
        "Create Trigger AutoPopulateOrderHistory After Insert On Accounts Referencing New As NewAccount for Each Row Insert Into OrderHistory (account_id) Values (NewAccount.account_id)"
 };


  public static void main(String[] args) throws NoSuchAlgorithmException, SQLException {
    Connection theCon = null;      // Connection to database
    DBAccess dbDriver = null;
    DBAccessFactory.setAction("Create");
    System.out.println("Setup all databases");
    try {
      dbDriver = (new DBAccessFactory()).getNewDBAccess();
      dbDriver.loadDriver();
      theCon = DriverManager.getConnection
              (dbDriver.urlOfDatabase(),
                      dbDriver.username(),
                      dbDriver.password());
    } catch (SQLException e) {
      System.err.println("Problem with connection to " +
              dbDriver.urlOfDatabase());
      System.out.println("SQLException: " + e.getMessage());
      System.out.println("SQLState:     " + e.getSQLState());
      System.out.println("VendorError:  " + e.getErrorCode());
      System.exit(-1);
    } catch (Exception e) {
      System.err.println("Can not load JDBC/ODBC driver.");
      System.exit(-1);
    }

    Statement stmt = null;
    try {
      stmt = theCon.createStatement();
    } catch (Exception e) {
      System.err.println("problems creating statement object");
    }
    // execute SQL commands to create table, insert data
    for (String sqlStatement : sqlStatements) {
      try {
        System.out.println(sqlStatement);
        switch (sqlStatement.charAt(0)) {
          case '/':
            System.out.println("------------------------------");
            break;
          case 's':
          case 'f':
            query(stmt, dbDriver.urlOfDatabase(), sqlStatement);
            break;
          case '*':
            if (sqlStatement.length() >= 2)
              switch (sqlStatement.charAt(1)) {
                case 'c':
                  theCon.commit();
                  break;
                case 'r':
                  theCon.rollback();
                  break;
                case '+':
                  theCon.setAutoCommit(true);
                  break;
                case '-':
                  theCon.setAutoCommit(false);
                  break;
              }
            break;
          default:
            try {
              stmt.execute(sqlStatement);
            } catch (SQLException e) {
              throw new RuntimeException(e);
            }
        }
        //System.out.println();
      } catch (Exception e) {
        System.out.println("problems with SQL sent to " +
                dbDriver.urlOfDatabase() +
                "\n" + sqlStatement + "\n" + e.getMessage());
      }
    }
    AdminController.injectAdmin();
    AdminController.injectEmployees();
    AdminController.injectUsers();
  }
  private static void query( Statement stmt, String url, String stm )
  {
    try
    {
      ResultSet res = stmt.executeQuery( stm );
      
      ArrayList<String> names = new ArrayList<>(10);

      ResultSetMetaData md = res.getMetaData();
      int cols = md.getColumnCount();

      for ( int j=1; j<=cols; j++ )
      {
        String name = md.getColumnName(j);
        System.out.printf( "%-14.14s ", name );
        names.add( name );
      }
      System.out.println();

      for ( int j=1; j<=cols; j++ )
      {
        System.out.printf( "%-14.14s ",  md.getColumnTypeName(j)  );
      }
      System.out.println();

      while ( res.next() )
      {
        for ( int j=0; j<cols; j++ )
        {
          String name = names.get(j);
          System.out.printf( "%-14.14s ", res.getString( name )  );
        }
        System.out.println();
      }


    } catch (Exception e)
    {
      System.err.println("problems with SQL sent to "+url+
                         "\n" + e.getMessage());
    }
  }
  
  private static String m( int len, String s )
  {
    if ( s.length() >= len )
    {
      return s.substring( 0, len-1 ) + " ";
    }
    else
    {
      StringBuilder res = new StringBuilder( len );
      res.append( s );
      for ( int i = s.length(); i<len; i++ )
        res.append( ' ' );
      return res.toString();
    }
  }

}
