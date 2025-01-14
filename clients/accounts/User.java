package clients.accounts;

import java.time.LocalDate;

public class User extends Accounts {
    String[] basket;
    public User(long account_id, String name, String username) {
        super(account_id, name, username);
        this.role = "user";
    }

    public String[] getBasket() {
        return basket;
    }
}
