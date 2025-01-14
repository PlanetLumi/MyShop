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
        model.login(username, password);
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
