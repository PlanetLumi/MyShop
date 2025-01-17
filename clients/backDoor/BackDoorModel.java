package clients.backDoor;

import catalogue.Basket;
import clients.accounts.ProductDB;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.StockReadWriter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class BackDoorModel extends Observable
{
  private Basket theBasket;
  private StockReadWriter theStock;

  public BackDoorModel(MiddleFactory mf)
  {
    try {
      theStock = mf.makeStockReadWriter(); // existing
    } catch (Exception e) {
      DEBUG.error("BackDoorModel.constructor\n%s", e.getMessage());
    }
    theBasket = new Basket();
  }
  public void doClear(){
  }
  

  public List<Object[]> searchProducts(String searchTerm) throws SQLException {
    ProductDB db = new ProductDB();
    return db.searchProducts(searchTerm);
  }

  // Overwrite product details
  public void updateProduct(String productNo, String newDesc, double newPrice, int newStock) {
    try {
      ProductDB db = new ProductDB();
      db.updateProductDetails(productNo, newDesc, newPrice, newStock);
      setChanged();
      notifyObservers("Updated " + productNo + " successfully.");
    } catch (Exception ex) {
      setChanged();
      notifyObservers("Error updating: " + ex.getMessage());
    }
  }

  //Add a quantity to the existing stock
  public void addStockQuantity(String productNo, int addQty) {
    try {
      ProductDB db = new ProductDB();
      db.addStock(productNo, addQty);
      setChanged();
      notifyObservers("Added " + addQty + " to stock of " + productNo);
    } catch (Exception ex) {
      setChanged();
      notifyObservers("Error adding stock: " + ex.getMessage());
    }
  }

  public void doQuery(String pn) {
  }
}