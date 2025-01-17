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
    private Window rpc;  // Store a reference to the JFrame

    public void getRpc() {
        this.rpc = view.getWindow();
    }

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
    public void adminLoginNext() throws SQLException {
        main.managerOpen(mlf);
    }
    public void cashierLoginNext() throws SQLException {
        main.cashierOpen();
    }
    public void createAccount(String username, String password, String verify, String role) throws SQLException, NoSuchAlgorithmException {
        model.createAccount(username, password, verify, role);
    }
    public void logout() {
        try {
            model.logout(); // clear session data
            closeAllOtherWindows();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void closeAllOtherWindows() {
        // “keepOpen” is the reference to the main login window/view

        Window loginWindow = SwingUtilities.getWindowAncestor(this.rpc);

        for (Window w : Window.getWindows()) {
            // If it's not the login window, dispose it
            if (w != null && w.isShowing() && w != loginWindow) {
                w.dispose();
            }
        }

        if(loginWindow != null) {
            loginWindow.setVisible(true);
            loginWindow.toFront();
        }
    }
}


