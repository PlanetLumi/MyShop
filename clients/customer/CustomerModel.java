package clients.customer;

import catalogue.Basket;
import catalogue.BasketRW;
import catalogue.Product;
import clients.accounts.AccountCreation;
import clients.accounts.Session;
import clients.accounts.SessionManager;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockException;
import middle.StockReader;
import remote.R_StockR;

import javax.swing.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;

import static java.lang.Integer.parseInt;

/**
 * Implements the Model of the customer client
 */
public class CustomerModel extends Observable
{
  private Product     theProduct = null;          // Current product
  private Basket      theBasket  = null;          // Bought items

  private String      pn = "";                    // Product being processed

  private StockReader     theStock     = null;
  private OrderProcessing theOrder     = null;
  private ImageIcon       thePic       = null;
  private R_StockR stockR      = null;
  private BasketRW basketRW =              null;
  public int currentQuantity              = 0;


  /*
   * Construct the model of the Customer
   * @param mf The factory to create the connection objects
   */
  public CustomerModel(MiddleFactory mf)
  {
    try                                          // 
    {  
      theStock = mf.makeStockReader();           // Database access
    } catch ( Exception e )
    {
      DEBUG.error("CustomerModel.constructor\n" +
                  "Database not created?\n%s\n", e.getMessage() );
    }
    theBasket = makeBasket();                    // Initial Basket
    basketRW = new BasketRW(theBasket); //Initialise basket Table Access
  }
  
  /**
   * return the Basket of products
   * @return the basket of products
   */
  public Basket getBasket()
  {
    return theBasket;
  }

  /**
   * Check if the product is in Stock
   * @param productNum The product number
   */
  public void doCheck(String pn) throws StockException, RemoteException {
    String message;
    System.out.println("Searching for product: " + pn);
    try {
      List<Object[]> data = theStock.findProduct(pn.trim());
      if (data != null && !data.isEmpty()) {
        Object[] row = data.getFirst(); // Get the first matching row
        if (row.length < 5) { // Ensure row has enough columns
          message = "Error: Product data is incomplete.";
          System.out.println(message);
          setChanged();
          notifyObservers(message);
          return;
        }

        theProduct = new Product(
                String.valueOf(row[0]),               // Product Number
                String.valueOf(row[1]),               // Description
                Double.parseDouble(String.valueOf(row[3])), // Image Path
                String.valueOf(row[2]),               // Price
                Integer.parseInt(String.valueOf(row[4])));    // Quantity

        String imagePath = theProduct.getImg();
        if (imagePath != null && !imagePath.isEmpty()) {
          thePic = new ImageIcon(imagePath);
        } else {
          thePic = null; // Handle missing image
        }

        message = String.format("Product found: %s (%d in stock)",
                theProduct.getDescription(),
                theProduct.getQuantity());
        theBasket.clear();
        theBasket.add(theProduct);
      } else {
        message = "Product not found: " + pn;
      }
    } catch (Exception e) {
      message = "Error searching for product: " + e.getMessage();
      e.printStackTrace();
    }
    setChanged();
    notifyObservers(message);
  }
  /**
   * Clear the products from the basket
   */
  public void doClear()
  {
    String theAction = "";
    theBasket.clear();                        // Clear s. list
    theAction = "Enter Product Number";       // Set display
    thePic = null;                            // No picture
    setChanged(); notifyObservers(theAction);
  }
  
  /**
   * Return a picture of the product
   * @return An instance of an ImageIcon
   */ 
  public ImageIcon getPicture()
  {
    return thePic;
  }
  
  /**
   * ask for update of view callled at start
   */
  private void askForUpdate()
  {
    setChanged(); notifyObservers("START only"); // Notify
  }

  /**
   * Make a new Basket
   * @return an instance of a new Basket
   */
  protected Basket makeBasket()
  {
    return new Basket();
  }
  public static String getMessage() throws SQLException {
    AccountCreation account = new AccountCreation();
    SessionManager sessionManager = SessionManager.getInstance();
    Session session = sessionManager.getCurrentSession();
    return account.readData("UserDetails", new String[] {"message"}, session.getAccount().getAccount_id()).toString();

  }

  public void clearMessage() throws SQLException {
    AccountCreation account = new AccountCreation();
    SessionManager sessionManager = SessionManager.getInstance();
    Session session = sessionManager.getCurrentSession();
    account.newData("UserDetails","message",new String[] {""}, session.getAccount().getAccount_id());
  }

  public void plusOne() {
     currentQuantity ++;
  }
  public void takeOne() {
    currentQuantity --;
  }
  public int getCurrentQuantity() {
    return (currentQuantity);
  }
  public Product getProduct() {
    return theProduct;
  }
  public void clearQuantity() {
    currentQuantity = 0;
  }
  public List getBskItmByContent(String content){
    return List.of(basketRW.getBskItmByContent(content));
  }
  public void dropBskItem(String product){
    basketRW.drpBskItem(extractProductNumber(product));
  }
  public int extractProductNumber(String product) {
    return Integer.parseInt(product.split(" ")[0]);
  }
}

