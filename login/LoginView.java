package login;

import middle.MiddleFactory;
import clients.UtilClass; // <-- import our style utility
import javax.swing.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

public class LoginView implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        // ...
    }

    // We no longer need local color references, because we store them in StyleUtil
    private static final int H = 300;
    private static final int W = 400;

    private static final JLabel pageTitle = new JLabel();
    private static final JTextField usernameInput = new JTextField();
    private static final JPasswordField passwordInput = new JPasswordField();
    private static final JButton theBtCheck = UtilClass.createRoundedButton("Login");  // use custom method
    private static final JButton theBtOpenPanel = UtilClass.createRoundedButton("Register New Account");
    private static final JButton logoutButton = UtilClass.createRoundedButton("Logout");

    private LoginController cont = null;
    private RootPaneContainer rpc;

    public LoginView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
        this.rpc = rpc;
        Container cp = rpc.getContentPane();
        Container rootWindow = (Container) rpc;
        cp.setLayout(null);

        UtilClass.setTurquoiseBackground(rpc);

        // Size and position
        rootWindow.setSize(W, H);
        rootWindow.setLocation(x, y);
        rootWindow.setVisible(true);

        Font f = new Font("Monospaced", Font.PLAIN, 12);

        // Title
        pageTitle.setBounds(110, 0, 270, 20);
        pageTitle.setText("User Login");
        pageTitle.setFont(f);
        pageTitle.setForeground(Color.BLACK);
        cp.add(pageTitle);

        // Username
        usernameInput.setBounds(40, 30, 300, 20);
        usernameInput.setText("Enter Username");
        usernameInput.setFont(f);
        usernameInput.setEditable(true);
        cp.add(usernameInput);

        // Password
        passwordInput.setBounds(40, 60, 300, 20);
        passwordInput.setText("Enter Password");
        passwordInput.setFont(f);
        passwordInput.setEditable(true);
        cp.add(passwordInput);

        // Login button (already has a custom style from StyleUtil)
        theBtCheck.setBounds(40, 80, 100, 40);
        theBtCheck.addActionListener(e -> {
            try {
                cont.login(usernameInput.getText(), new String(passwordInput.getPassword()));
                passwordInput.setText("");
            } catch (NoSuchAlgorithmException | SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        cp.add(theBtCheck);

        // Register button
        theBtOpenPanel.setBounds(40, 120, 200, 40);
        theBtOpenPanel.addActionListener(e -> {
            cont.openRegisterPanel(x + 40, y);
        });
        cp.add(theBtOpenPanel);

        // Logout button
        logoutButton.setBounds(40, 170, 100, 40);
        logoutButton.addActionListener(e -> {
            cont.logout();
        });
        cp.add(logoutButton);
    }

    public void setController(LoginController a) {
        cont = a;
        cont.getRpc();
    }

    public Window getWindow() {
        return (Window) this.rpc;
    }
}
