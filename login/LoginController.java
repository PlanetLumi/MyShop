package login;
import clients.Main;
import clients.PosOnScrn;
import clients.accounts.AccountCreation;
import clients.accounts.Session;
import clients.accounts.SessionManager;
import middle.LocalMiddleFactory;
import middle.MiddleFactory;

import javax.swing.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import static java.awt.SystemColor.window;

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
    public void userLoginNext() throws SQLException {
        main.userOpen();
    }
    public void openRegisterPanel(int x, int y) {
        model.openRegisterPanel(x, y);
    }
    public void adminLoginNext(){
        main.managerOpen(mlf);
    }
    public void cashierLoginNext(){
        main.cashierOpen();
    }
    public void createAccount(String username, String password, String verify, String role) throws SQLException, NoSuchAlgorithmException {
        model.createAccount(username, password, verify, role);
    }
}
