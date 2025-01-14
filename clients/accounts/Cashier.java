package clients.accounts;

public class Cashier extends Accounts {
    public Cashier(long account_id, String name, String username) {
        super(account_id, name, username);
        this.role = "cashier";
    }
}
