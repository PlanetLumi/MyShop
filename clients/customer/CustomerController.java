package clients.customer;

import catalogue.Basket;
import catalogue.BasketRW;
import catalogue.Product;
import middle.StockException;

import javax.swing.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

/**
 * The Customer Controller
 */

public class CustomerController
{
  private CustomerModel model = null;
  private CustomerView  view  = null;
  private Basket basket = null;
  private BasketRW basketRW = null;
  /**
   * Constructor
   * @param model The model 
   * @param view  The view from which the interaction came
   */
  public CustomerController( CustomerModel model, CustomerView view )
  {
    this.view  = view;
    this.model = model;
  }

  /**
   * Check interaction from view
   * @param pn The product number to be checked
   */
  public void doCheck( String pn ) throws StockException, RemoteException {
    model.doCheck(pn);
  }

  /**
   * Clear interaction from view
   */
  public void doClear()
  {
    model.doClear();
  }

  public static String displayMessage() throws SQLException {
    return CustomerModel.getMessage();
  }
  public void clearMessage() throws SQLException {
    model.clearMessage();
  }
  public ImageIcon getPicture() {
    return model.getPicture();
  }
  public void plusOne(){
    model.plusOne();
  }
  public void takeOne(){
    model.takeOne();
  }

  public int getCurrentQuantity() {
    return model.getCurrentQuantity();
  }
  public void addToBasket (){
    basket.addIn(getCurrentProduct(),getCurrentQuantity());
  }
  public void submitBasket() {
    basketRW.saveBasket();
  }
  public Product getCurrentProduct() {
    return model.getProduct();
  }
  public void clearQuantity(){
    model.clearQuantity();
  }
  public List getBskItmByContent(String description){
     return model.getBskItmByContent(description);
  }
  public void dropBskItem(String inp){
    model.dropBskItem(inp);
  }
}

