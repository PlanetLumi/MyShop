package clients.backDoor;

import java.sql.SQLException;
import java.util.List;

public class BackDoorController
{
  private BackDoorModel model;
  private BackDoorView  view;

  public BackDoorController( BackDoorModel model, BackDoorView view )
  {
    this.view  = view;
    this.model = model;
  }

  public void doQuery(String pn) { model.doQuery(pn); }
  // Search for partial name or product number
  public void doSearch(String searchTerm) throws SQLException {
    // The model can talk to DB, returning e.g. List<Object[]>,
    // then we pass to view.populateTable(...).
    List<Object[]> found = model.searchProducts(searchTerm);
    view.populateTable(found);
  }

  // Overwrite product details (desc, price, stock).
  public void doUpdate(String productNo, String newDesc, double newPrice, int newStock) {
    model.updateProduct(productNo, newDesc, newPrice, newStock);
  }

  //  Add some stock quantity
  public void doAddStock(String productNo, int addQty) {
    model.addStockQuantity(productNo, addQty);
  }
}