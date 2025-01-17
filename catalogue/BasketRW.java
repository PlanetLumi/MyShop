package catalogue;

import clients.accounts.ProductDB;
import clients.accounts.Session;
import clients.accounts.SessionManager;

import java.sql.SQLException;

/**
 * A class responsible for reading and writing the user's current basket
 * from/to the database.
 */
public class BasketRW {

    private final ProductDB productDB;
    private final SessionManager sessionManager;

    public BasketRW() throws SQLException {
        this.productDB = new ProductDB();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * Save (persist) the in-memory basket to the DB
     */
    public void saveBasket(Basket basket) {
        Session session = sessionManager.getCurrentSession();
        if (session == null) {
            System.out.println("No current session found. Cannot save basket.");
            return;
        }
        int userID = (int) session.getAccount().getAccount_id();

        productDB.addBasket(basket.returnProductPurchaseInfo(), userID);
    }

    /**
     * Load the user's basket from DB into the in-memory Basket object
     * (if you want to restore a previously saved basket).
     */
    public void loadBasket(Basket basket) {
        Session session = sessionManager.getCurrentSession();
        if (session == null) {
            System.out.println("No current session found. Cannot load basket.");
            return;
        }
        int userID = (int) session.getAccount().getAccount_id();
    }

    /**
     * Remove an item from the DB side of the basket
     * (usually after removing it from the in-memory basket).
     */
    public void dropBasketItem(int productId) {
        Session session = sessionManager.getCurrentSession();
        if (session == null) {
            System.out.println("No current session found. Cannot drop basket item.");
            return;
        }
        int userID = (int) session.getAccount().getAccount_id();

        int basketID = productDB.getBasketId(userID);
        productDB.drpBskItem(basketID, productId);
    }

    /**
     * Example: search for items in the DB basket matching a string
     * (like partial product name).
     */
    public String[] getBskItmByContent(String content) {
        Session session = sessionManager.getCurrentSession();
        if (session == null) {
            return null;
        }
        int userID = (int) session.getAccount().getAccount_id();
        int basketID = productDB.getBasketId(userID);

        return productDB.getBskItmByContent(content, basketID);
    }
}
