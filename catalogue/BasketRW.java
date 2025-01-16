package catalogue;

import clients.accounts.ProductDB;
import clients.accounts.Session;
import clients.accounts.SessionManager;

public class BasketRW {
    private Basket basket;
    private SessionManager sessionManager = SessionManager.getInstance();
    private Session session = sessionManager.getCurrentSession();
    public BasketRW(Basket basket) {
        this.basket = basket;
    }
    public void saveBasket(){
        ProductDB db = new ProductDB();
        db.addBasket(basket.returnProductPurchaseInfo(), (int) session.getAccount().getAccount_id());
    }
    public String[] getBskItmByContent(String content){
        ProductDB db = new ProductDB();
        return db.getBskItmByContent(content, db.getBasketId((int) session.getAccount().getAccount_id()));

    }
    public void drpBskItem(int productId){
        ProductDB db = new ProductDB();
        db.drpBskItem(productId, (int) session.getAccount().getAccount_id());

    }
}
