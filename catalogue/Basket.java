package catalogue;

import java.io.Serializable;
import java.util.*;

/**
 * A collection of products representing the user's in-memory basket
 * before they finalize (submit) the purchase.
 */
public class Basket extends ArrayList<Product> implements Serializable
{
  private static final long serialVersionUID = 1L;

  // Unique ID that might correspond to an 'orderHistoryId' in DB.
  private long orderHistoryId = 0;

  // A field to represent the status, e.g. "Pending", "Packed", "Delivered"...
  private String status = "Pending";

  // This map tracks each Product and the quantity the user wants to buy.
  private final Map<Product, Integer> productQuantities = new HashMap<>();

  public Basket() {
    // For now, just keep the defaults
  }

  public Basket(long orderHistoryId, String status) {
    this.orderHistoryId = orderHistoryId;
    this.status = status;
  }

  /**

   */
  public long getOrderHistoryId() {
    return orderHistoryId;
  }

  public void setOrderHistoryId(long orderHistoryId) {
    this.orderHistoryId = orderHistoryId;
  }

  /**
   * Return or set the Basket’s overall status,
   * e.g. "Pending", "Packed", "Shipped", "Delivered", etc.
   */
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  public int getOrderNum(){
    return (int) orderHistoryId;
  }

  /**
   * Overrides the add(Product) from ArrayList if you still want
   * to keep that logic, or you can just rely on addIn(...) below.
   */
  @Override
  public boolean add(Product pr) {
    return super.add(pr);
  }

  /**
   * Add or increment an item in the productQuantities map
   */
  public void addIn(Product pr, int quantity) {
    productQuantities.merge(pr, quantity, Integer::sum);
    // If you also want them in the underlying ArrayList
    // (for any legacy code), do that:
    super.add(pr);
  }

  /**
   * Returns the internal mapping of Product -> quantity
   */
  public HashMap<Product, Integer> returnProductPurchaseInfo() {
    return (HashMap<Product, Integer>) productQuantities;
  }

  /**
   * Clears both the ArrayList and the productQuantities map
   */
  @Override
  public void clear() {
    super.clear();
    productQuantities.clear();
    orderHistoryId = 0;
    status = "Pending";
  }

  /**
   * Returns a textual summary (debugging).
   */
  public String getDetails() {
    Locale uk = Locale.UK;
    StringBuilder sb = new StringBuilder();
    Formatter fr = new Formatter(sb, uk);
    double total = 0.0;
    String csign = (java.util.Currency.getInstance(uk)).getSymbol();

    // Possibly show orderHistoryId and status
    fr.format("OrderHistoryId: %d  Status: %s\n", orderHistoryId, status);

    for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
      Product p = entry.getKey();
      int qty = entry.getValue();
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

  public Object get() {
      return null;
  }
}
