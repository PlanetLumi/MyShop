package catalogue;

import java.io.Serializable;
import java.util.Collections;

/**
 * Write a description of class BetterBasket here.
 * 
 * @author  Your Name 
 * @version 1.0
 */
public class BetterBasket extends Basket implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  @Override
  public boolean add( Product pr )
  {                 
	  for(Product prInList: this) { // if product is the same as a product already in list then merge together
		  if(prInList.getProductNum().equals(pr.getProductNum())) {
			  int quantity = pr.getQuantity()+prInList.getQuantity();
			  prInList.setQuantity(quantity);
			  return (true);
		  }
	  }
	  
    super.add( pr );     // Call add in ArrayList
    Collections.sort(this);  //sorts by num
    return (true);  	     // return
  }
 
}
