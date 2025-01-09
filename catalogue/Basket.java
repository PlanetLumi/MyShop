package catalogue;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Formatter;
import java.util.Locale;

/**
 * A collection of products,
 * used to record the products that are to be wished to be purchased.
 * @author  Mike Smith University of Brighton
 * @version 2.2
 */
public class Basket extends ArrayList<Product> implements Serializable
{
  private static final long serialVersionUID = 1;
  private int    theOrderNum = 0;          // Order number
  
  /**
   * Constructor for a basket which is
   *  used to represent a customer order/ wish list
   */
  public Basket()
  {
    theOrderNum  = 0;
  }
  
  /**
   * Set the customers unique order number
   * Valid order Numbers 1 .. N
   * @param anOrderNum A unique order number
   */
  public void setOrderNum( int anOrderNum )
  {
    theOrderNum = anOrderNum;
  }

  /**
   * Returns the customers unique order number
   * @return the customers order number
   */
  public int getOrderNum()
  {
    return theOrderNum;
  }
  
  /**
   * Add a product to the Basket.
   * Product is appended to the end of the existing products
   * in the basket.
   * @param pr A product to be added to the basket
   * @return true if successfully adds the product
   */
  @Override
  public boolean add(Product pr) {
      boolean found = false;
      for (Product existingProduct : this) {
          if (existingProduct.getProductNum().equals(pr.getProductNum())) {
              existingProduct.mergeProduct(pr);
              found = true;
              break;
          }
      }
      if (!found) {
          return super.add(pr);  
      }
      return true; 
  }

  /**
   * Returns the total price of all items in the basket.
   * @return total price of the items
   */
  public double getTotalPrice() {
    double total = 0.0;
    for (Product product : this) {  // Using 'this' to reference the products in the basket
        total += product.getPrice() * product.getQuantity();
    }
    return total;
  }

  /**
   * Applies a discount on the total price. Returns the discounted total.
   * @param discountPercentage The percentage discount to apply (e.g., 10 for 10%)
   * @return the total price after discount
   */
  public double applyDiscount(double discountPercentage) {
    double total = getTotalPrice();
    double discountAmount = total * (discountPercentage / 100);
    return total - discountAmount;
  }

  /**
   * Returns a description of the products in the basket suitable for printing.
   * @return a string description of the basket products
   */
  public String getDetails()
  {
    Locale uk = Locale.UK;
    StringBuilder sb = new StringBuilder(256);
    Formatter     fr = new Formatter(sb, uk);
    String csign = (Currency.getInstance( uk )).getSymbol();
    double total = 0.00;
    if ( theOrderNum != 0 )
      fr.format( "Order number: %03d\n", theOrderNum );
      
    if ( this.size() > 0 )
    {
      for ( Product pr: this )
      {
        int number = pr.getQuantity();
        fr.format("%-7s",       pr.getProductNum() );
        fr.format("%-14.14s ",  pr.getDescription() );
        fr.format("(%3d) ",     number );
        fr.format("%s%7.2f",    csign, pr.getPrice() * number );
        fr.format("\n");
        total += pr.getPrice() * number;
      }
      fr.format("----------------------------\n");
      fr.format("Total                       ");
      fr.format("%s%7.2f\n",    csign, total );
      fr.close();
    }
    return sb.toString();
  }
}
