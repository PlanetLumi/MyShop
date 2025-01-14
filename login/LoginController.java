package login;
import clients.Main;
import clients.accounts.AccountCreation;
import clients.accounts.Session;
import clients.accounts.SessionManager;
import middle.LocalMiddleFactory;
import middle.MiddleFactory;

import javax.swing.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class LoginController {
    private LoginModel model;
    private LoginView view;
    private Main main;
    MiddleFactory mlf = new LocalMiddleFactory();
    public LoginController(LoginModel model, LoginView view) {
        this.model = model;
        this.view = view;
        main = new Main();
        model.setController(this);
    }
    public void login(String username, String password) throws NoSuchAlgorithmException, SQLException {
        AccountCreation create = new AccountCreation();
        SessionManager sm = SessionManager.getInstance();
        Session s = sm.getSession(create.loginAccount(username, password));
        s.getAccount();
        model.decidePanel(model.findRole());
        System.out.println(s.getSessionId());
        System.out.println(s.getAccount());
        System.out.println(s.getCreationTime());
    }
    public void userLoginNext(){
        main.userOpen();

    }
    public void adminLoginNext(){
        main.managerOpen(mlf);
    }
    public void cashierLoginNext(){
        main.cashierOpen();
    }
}
