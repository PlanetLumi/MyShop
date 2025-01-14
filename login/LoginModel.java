package login;

import clients.accounts.AccountCreation;
import middle.MiddleFactory;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class LoginModel extends Observable {
    private AccountCreation create;
    private LoginController cont;

    public LoginModel(MiddleFactory mf) {
        this.create = new AccountCreation();
    }

    public String findRole(long userID) throws SQLException {
        if (Objects.equals(create.getRole(userID), "admin")) {

            return "admin";
        }
        if (Objects.equals(create.getRole(userID), "cashier")) {
            return "cashier";
        } else {
            return "user";
        }
    }
    public void decidePanel(String role){
        if(Objects.equals(role, "admin")){
            cont.adminLoginNext();
        }
        if(Objects.equals(role, "cashier")){
            cont.cashierLoginNext();
        }
        if(Objects.equals(role, "user")){
            cont.userLoginNext();
        }
    }
    public void setController(LoginController cont) {
        this.cont = cont;
    }
}
