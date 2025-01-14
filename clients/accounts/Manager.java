package clients.accounts;

public class Manager extends Accounts {
    public Manager(long account_id, String name, String username) {
        super(account_id, name, username);
        this.role = "manager";
    }

}