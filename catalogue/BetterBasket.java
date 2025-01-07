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
	@Override
	  public boolean add( Product pr )
	  {     
		for(Product prInList: this) { //loop through products
			if(prInList.getProductNum().equals(pr.getProductNum())) { //if product is in basket
				int quantity = pr.getQuantity()+prInList.getQuantity();
				prInList.setQuantity(quantity); //increase quantity
				return(true);
			}
		}
	    return super.add( pr );     // if not already in basket, add new
	  }
	
	@Override
	public boolean rem( Product pr )
	  {     
		for (Product prInList : this) {
	        if (prInList.getProductNum().equals(pr.getProductNum())) {
	            int newQuantity = prInList.getQuantity() - pr.getQuantity();
	            if (newQuantity > 0) {
	                prInList.setQuantity(newQuantity);
	            } else {
	                super.remove(prInList); // Remove the product entirely
	            }
	            return true; // Product was successfully removed/modified
	        }
	    }
	    return false; // Product not found
	  }
}
