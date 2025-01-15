package login;

import clients.PosOnScrn;
import clients.accounts.AccountCreation;
import clients.accounts.Accounts;
import clients.accounts.Session;
import clients.accounts.SessionManager;
import middle.MiddleFactory;
import security.Encryption;

import javax.swing.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import static java.awt.SystemColor.window;

public class LoginModel extends Observable {
    private AccountCreation create;
    private LoginController cont;
    private MiddleFactory mlf;

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

    public void decidePanel(String role) throws SQLException {
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
    public void createAccount(String username, String password, String verify, String role ) throws NoSuchAlgorithmException, SQLException {
        AccountCreation create = new AccountCreation();
        if(!create.checkAccount(username)) {
            if(password.equals(verify)) {
                create.createAccount(username, password, role);
            } else{
                JOptionPane.showMessageDialog(null, "Passwords don't match");
            }
        } else{
            System.out.println("Account already exists");
        }

    }
    public void openRegisterPanel(int x, int y) {
        JFrame frame = new JFrame();
        frame.setTitle("Reister Client MVC");
        Dimension pos = PosOnScrn.getPos();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RegisterView view = new RegisterView(frame, mlf, pos.width, pos.height);
        view.setController(cont);
        addObserver(view);
        frame.setVisible(true);
    }
}

