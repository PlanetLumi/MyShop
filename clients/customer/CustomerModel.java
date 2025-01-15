package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import clients.accounts.AccountCreation;
import clients.accounts.Session;
import clients.accounts.SessionManager;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockException;
import middle.StockReader;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Observable;

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
  public void doCheck(String productNum )
  {
    theBasket.clear();
    String theAction = "";
    pn = productNum.trim();
    try {
      if (theStock.exists(pn)) {
        Product product = theStock.getDetails(pn);
        if (product.getQuantity() > 0) {
          theAction = String.format("%s : %.2f (%d available)",
                  product.getDescription(), product.getPrice(), product.getQuantity());
          theBasket.add(product);
          thePic = theStock.getImage(pn);
        } else {
          theAction = product.getDescription() + " is out of stock!";
        }
      } else {
        theAction = "Product not found: " + pn;
      }
    } catch (StockException e) {
      theAction = "Error accessing stock: " + e.getMessage();
    }
    setChanged();
    notifyObservers(theAction);
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
}

