package clients.accounts;

import java.time.LocalDate;

public class Accounts {
    private static Accounts instance;
    long account_id;
    String name;
    String username;
    String role;

    public Accounts(long account_id, String name, String username) {
        this.account_id = account_id;
        this.name = name;
        this.username = username;
    }

    public long getAccount_id() {
        return account_id;
    }
    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}

