package catalogue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

/**
 * A collection of products representing the user's in-memory basket
 * before they finalize (submit) the purchase.
 */
public class Basket extends ArrayList<Product> implements Serializable {
  private static final long serialVersionUID = 1L;

  private int orderNum = 0;
  private HashMap<Product, Integer> productQuantities = new HashMap<>();

  public Basket() {
    // For now, we just keep an empty basket
  }

  /**
   * Optional: store an order/basket ID if you want
   */
  public int getOrderNum() {
    return orderNum;
  }
  public void setOrderNum(int orderNum) {
    this.orderNum = orderNum;
  }

  /**
   * Add a product to the Basket's ArrayList (for any legacy usage).
   * Also optional: track it in the productQuantities map
   */
  @Override
  public boolean add(Product pr) {
    return super.add(pr);
  }

  /**
   * Adds or increments an item in the productQuantities map
   */
  public void addIn(Product pr, int quantity) {
    productQuantities.merge(pr, quantity, Integer::sum);
  }

  /**
   * Returns the internal mapping of Product -> quantity
   */
  public HashMap<Product, Integer> returnProductPurchaseInfo() {
    return productQuantities;
  }

  /**
   * Clears both the ArrayList and the productQuantities map
   */
  @Override
  public void clear() {
    super.clear();
    productQuantities.clear();
  }

  /**
   * Returns a textual summary of items in the basket (for debugging)
   */
  public String getDetails() {
    Locale uk = Locale.UK;
    StringBuilder sb = new StringBuilder();
    Formatter fr = new Formatter(sb, uk);
    double total = 0.0;
    String csign = (java.util.Currency.getInstance(uk)).getSymbol();

    for (Product p : productQuantities.keySet()) {
      int qty = productQuantities.get(p);
      double lineTotal = p.getPrice() * qty;
      total += lineTotal;

      fr.format("%-7s %-14.14s (%3d) %s%7.2f\n",
              p.getProductNum(),
              p.getDescription(),
              qty,
              csign,
              lineTotal);
    }
    fr.format("----------------------------\n");
    fr.format("Total: %s%.2f\n", csign, total);
    fr.close();

    return sb.toString();
  }
}
