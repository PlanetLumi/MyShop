package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import clients.accounts.ProductDB;
import clients.accounts.Session;
import clients.accounts.SessionManager;
import middle.StockException;
import javax.swing.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * The controller that mediates between CustomerView and the Model (Basket, etc.)
 */
public class CustomerController {

  private CustomerModel model;
  private CustomerView  view;

  // For storing currently selected product from the search result
  private String  currentProductNo;
  private String  currentDesc;
  private double  currentPrice;
  private String  currentImg;
  private int     currentStockLevel;


  // Database access object for product searches
  private ProductDB productDB = new ProductDB();

  public CustomerController(CustomerModel m, CustomerView v) throws SQLException {
    this.model = m;
    this.view  = v;
    this.view.setController(this);
  }

  /**
   * User clicks the Check (search) button
   *
   */

  public void doCheck(String searchTerm) throws StockException, RemoteException {
    try {
      //  DB returns raw rows via model or direct DB call
      List<Object[]> data = productDB.getAllProductInfo(searchTerm);

      // Convert each row -> Product
      List<Product> productList = new ArrayList<>();
      for (Object[] row : data) {
        String productNo = row[0].toString();
        String desc = row[1].toString();
        String imagePath = row[2].toString();
        double price = Double.parseDouble(row[3].toString());
        int stockLevel = Integer.parseInt(row[4].toString());
        model.setPicture(getImg());
        Product p = new Product(productNo, desc, price, imagePath, stockLevel);
        productList.add(p);
      }

      view.populateProductTable(productList);
      model.setMessage("Found " + productList.size() + " products for '" + searchTerm + "'");
    } catch (Exception e) {
      e.printStackTrace();
      model.setMessage("Error searching for product: " + e.getMessage());
    }
  }



  /**
   * Set the currently selected product from the JTable
   */
  public void setSelectedProduct(String productNo, String desc, double price, int stockLevel, String img) {
    this.currentProductNo   = productNo;
    this.currentDesc        = desc;
    this.currentPrice       = price;
    this.currentStockLevel  = stockLevel;
    this.currentImg         = img;
  }

  /**
   * Called by the plus/minus approach or the user typing a quantity.
   * This method is invoked by "Add To Basket".
   */
  public void addToBasket(int quantity) {
    if (currentProductNo == null || currentProductNo.isEmpty()) {
      JOptionPane.showMessageDialog(null, "Please select a product first.");
      return;
    }

    if (quantity <= 0) {
      JOptionPane.showMessageDialog(null, "Invalid quantity.");
      return;
    }
    Product p = new Product(currentProductNo, currentDesc, currentPrice, currentImg, quantity);

    model.getBasket().addIn(p, quantity);

    model.setMessage("Added " + quantity + " of " + currentDesc + " to basket.");
  }

  /**
   * Clears the search text or results.
   */
  public void doClear() {
    model.setMessage("Cleared search.");

  }

  /**
   * Called when user hits "Open Basket"
   */
  public void submitBasket() {
    model.setMessage("Opening Basket...");
  }

  /**
   * In your Basket panel, if user wants to drop an item
   */
  public void dropBskItem(String productNo) {
    Product toRemove = null;
    for (Product p : model.getBasket().returnProductPurchaseInfo().keySet()) {
      if (Objects.equals(p.getProductNum(), productNo)) {
        toRemove = p;
        break;
      }
    }
    if (toRemove != null) {
      model.getBasket().returnProductPurchaseInfo().remove(toRemove);
      model.setMessage("Item dropped: " + productNo);
    }
  }

  /**
   * Utility method to pass search results back to the view's table
   */


  public Basket getBasket() {
    return model.getBasket();
  }
  public ImageIcon getImg() {
    return model.getPicture();
  }

  public void purchaseBasket() {
    try {
      if (model.getBasket().returnProductPurchaseInfo().isEmpty()) {
        JOptionPane.showMessageDialog(null,
                "Your basket is empty. Nothing to purchase.",
                "Empty Basket", JOptionPane.WARNING_MESSAGE);
        return;
      }

      long userId = SessionManager.getInstance()
              .getCurrentSession()
              .getAccount()
              .getAccount_id();

      // 1) Insert the basket items into OrderHistory
      productDB.insertBasketIntoOrderHistory(userId, model.getBasket());

      // 2) Decrement the stock levels
      for (Map.Entry<Product, Integer> entry : model.getBasket().returnProductPurchaseInfo().entrySet()) {
        Product p = entry.getKey();
        int purchasedQty = entry.getValue();
        // Decrement stock for that product
        productDB.updateStockLevel(p.getProductNum(), purchasedQty);
      }

      // 3) Clear the BasketItems rows in DB
      int basketId = productDB.getBasketId((int) userId);
      for (Product p : model.getBasket().returnProductPurchaseInfo().keySet()) {
        int productID = Integer.parseInt(p.getProductNum());
        productDB.drpBskItem(basketId, productID);
      }
      insertBasketIntoOrderHistory(userId, model.getBasket());

      // 4) Clear the in-memory basket
      model.getBasket().returnProductPurchaseInfo().clear();
      model.setMessage("Thank you! Your order has been placed.");
    } catch (Exception e) {
      e.printStackTrace();
      model.setMessage("Error submitting order: " + e.getMessage());
    }
  }
  public void insertBasketIntoOrderHistory(long userId, Basket basket) throws Exception {
    ProductDB productDB = new ProductDB();
    productDB.insertBasketIntoOrderHistory(userId, basket);
  }
}