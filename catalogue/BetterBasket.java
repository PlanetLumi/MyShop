package catalogue;

import java.io.Serializable;
import java.util.Collections;

/**
 * Write a description of class BetterBasket here.
 * 
 * @author  Your Name 
 * @version 1.0
 */
public class BetterBasket extends Basket
{
  private static final long serialVersionUID = 1L;
  // You need to add code here
  // merge the items for same product,
  // or sort the item based on the product number
  @Override
  public boolean add( Product pr ){           
	  for (Product prInList:this){
		  if(prInList.getProductNum().equals(pr.getProductNum())) {
			  int newQuantity = pr.getQuantity() + prInList.getQuantity();
			  prInList.setQuantity(newQuantity);
			  return(true);
			  
		  }		  
	  }
	  return super.add( pr );     // Call add in ArrayList
  }
}
