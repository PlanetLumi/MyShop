package clients.customer;
import java.util.Map;

/**
 * The Customer Controller
 */

public class CustomerController
{
  private CustomerModel model = null;
  private CustomerView  view  = null;

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
  public void doCheck( String pn )
  {
	try {
		Integer.parseInt(pn);
	} catch(NumberFormatException e){
		  SearchName nameSearch = new SearchName();
		  pn = nameSearch.getNumFromName(nameSearch,pn);
	}
    if (pn != null) {
    	model.doCheck(pn);
    }
  }

  //public void doCheckByName(String name) {
//	  SearchName nameSearch = new SearchName();
//	  String pn = nameSearch.getNumFromName(nameSearch,name);
//	  model.doCheck(pn);
//  }
  
  /**
   * Clear interaction from view
   */
  public void doClear()
  {
    model.doClear();
  }

  
}

