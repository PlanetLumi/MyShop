package login;

import clients.accounts.AccountCreation;
import clients.accounts.Accounts;
import clients.accounts.Session;
import clients.accounts.SessionManager;
import middle.MiddleFactory;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

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

    public void decidePanel(String role) {
        if (Objects.equals(role, "admin")) {
            cont.adminLoginNext();
        }
        if (Objects.equals(role, "cashier")) {
            cont.cashierLoginNext();
        }
        if (Objects.equals(role, "user")) {
            cont.userLoginNext();
        }
    }

    public void setController(LoginController cont) {
        this.cont = cont;
    }

    public void login(String username, String password) throws NoSuchAlgorithmException, SQLException {
    AccountCreation create = new AccountCreation();
    SessionManager sm = SessionManager.getInstance();
    Object[] userDetails = create.loginAccount(username, password);
    Accounts account = new Accounts((long) userDetails[0], username, password);
    sm.login((UUID) userDetails[1], account);
    Session s = sm.getSession((UUID)userDetails[1]);
    sm.login(s.getSessionId(),s.getAccount());
    s.setRole(findRole((s.getAccount().getAccount_id())));
    decidePanel(s.getRole());
    }
}
