package clients.packing;

import catalogue.Basket;
import clients.accounts.ProductDB;
import debug.DEBUG;
import middle.OrderProcessing;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * The Packing Controller
 */
public class PackingController
{
  private PackingModel model;
  private PackingView  view;

  public PackingController(PackingModel model) {
    this.model  = model;
  }

  /**
   * Called from packing view's "Packed" button (existing code).
   */
  public void doPacked() {
    model.doPacked();
  }

  /**
   * Called by the selection view to list all un-packed orders.
   */
  public List<Map<String,Object>> fetchUnpackedOrders() throws SQLException {
    return model.fetchUnpackedOrdersFromDB();
  }

  /**
   * Confirm the selected order, then open a new screen to "pack" it.
   */
  public void confirmOrderForPacking(Long orderHistId) {

    openPackingStep(orderHistId);
  }

  /**
   * Example: show the "Pack" screen for that order
   */
  private void openPackingStep(Long orderHistId) {

  }

  /**
   * Called by the next screen’s "Set for Delivery" button
   */
  public void doSetForDelivery(Long orderHistId) throws SQLException {
    ProductDB productDB = new ProductDB();
    productDB.setOrderStatus(orderHistId, "packed");
    JOptionPane.showMessageDialog(null,
            "Order " + orderHistId + " is now set for delivery!");
  }
}
