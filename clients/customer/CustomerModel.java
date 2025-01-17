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
import java.util.ArrayList;
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
  private BasketRW basketRW;
  public int currentQuantity              = 0;
  private ImageIcon picture;
  private String message;
  private Basket basket;

  /*
   * Construct the model of the Customer
   * @param mf The factory to create the connection objects
   */

  public CustomerModel(MiddleFactory mf) throws SQLException {
    try {
      theStock = mf.makeStockReader();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Initialize the in-memory basket
    basket = new Basket();
    // The RW class for reading/writing
    basketRW = new BasketRW();
  }

  /**
   * return the Basket of products
   * @return the basket of products
   */
  /**
   * Check if the product is in Stock
   * @param productNum The product number
   */

  /**
   * Clear the products from the basket
   */

  /**
   * Return a picture of the product
   * @return An instance of an ImageIcon
   */

  /**
   * ask for update of view callled at start
   */

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


  public Basket getBasket() {
    return basket;
  }

  public ImageIcon getPicture() {
    return picture;
  }

  public void setPicture(ImageIcon pic) {
    this.picture = pic;
    setChanged();
    notifyObservers("Picture Updated");
  }
  public void setMessage(String msg) {
    this.message = msg;
    setChanged();
    notifyObservers(msg);
  }
  public void addToBasket(Product p, int qty) {
    basket.addIn(p, qty);
    setChanged();
    notifyObservers("Added product " + p.getProductNum());
  }

  public void removeFromBasket(Product p) {
    // remove from in-memory basket
    basket.returnProductPurchaseInfo().remove(p);
    setChanged();
    notifyObservers("Removed product " + p.getProductNum());
  }

  public void saveBasketToDB() {
    basketRW.saveBasket(basket);
  }

  public void loadBasketFromDB() {
    basket.clear();
    basketRW.loadBasket(basket);
    setChanged();
    notifyObservers("Basket loaded from DB");
  }

  }

