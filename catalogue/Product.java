package catalogue;

import java.io.Serializable;

/**
 * Used to hold the following information about
 * a product: Product number, Description, Price, Stock level.
 * @author  Mike Smith University of Brighton
 * @version 2.0
 */

public class Product {
  private String productNum;
  private String description;
  private double price;
  private String imgPath;    // <== store image path here
  private int quantity;

  // Example constructor that matches your code's usage
  public Product(String productNo, String desc, double price, String imgPath, int qty) {
    this.productNum  = productNo;
    this.description = desc;
    this.price       = price;
    this.imgPath     = imgPath;
    this.quantity    = qty;
  }


  // other getters...
  public String getProductNum()   { return this.productNum; }
  public String getDescription()  { return this.description; }
  public double getPrice()        { return this.price; }
  public int getQuantity()        { return this.quantity; }
  
  public void setProductNum( String aProductNum )
  {
    productNum = aProductNum;
  }
  
  public void setDescription( String aDescription )
  {
    description = aDescription;
  }
  
  public void setPrice( double aPrice )
  {
    price = aPrice;
  }
  
  public void setQuantity( int aQuantity )
  {
    quantity = aQuantity;
  }
  public String getImg()   { return imgPath; }


}
