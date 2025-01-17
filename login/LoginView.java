package login;

import clients.Picture;
import middle.MiddleFactory;
import middle.StockReader;

import javax.swing.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

public class LoginView implements Observer{

    @Override
    public void update(Observable o, Object arg) {

    }

    class Name                              // Names of buttons
    {
        public static final String CHECK = "Check";
        public static final String CLEAR = "Clear";
    }

    private static final int H = 300;       // Height of window pixels
    private static final int W = 400;       // Width  of window pixels

    private static final JLabel pageTitle = new JLabel();
    private static final JLabel theAction = new JLabel();
    private static final JTextField usernameInput = new JTextField();
    private static JPasswordField passwordInput = new JPasswordField();
    private static final JTextField emailInput = new JTextField();
    private static final JPasswordField passwordInputRe = new JPasswordField();
    private final JTextArea theOutput = new JTextArea();
    private final JScrollPane theSP = new JScrollPane();
    private static final JButton theBtCheck = new JButton(Name.CHECK);
    private static final JButton theBtClear = new JButton(Name.CLEAR);
    private static final JButton theBtOpenPanel = new JButton();

    private Picture thePicture = new Picture(80, 80);
    private StockReader theStock = null;
    private LoginController cont = null;
    private RootPaneContainer rpc;

        /**
         * Construct the view
         *
         * @param rpc Window in which to construct
         * @param mf  Factor to deliver order and stock objects
         * @param x   x-cordinate of position of window on screen
         * @param y   y-cordinate of position of window on screen
         */

    public LoginView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
        this.rpc = rpc;
        Container cp = rpc.getContentPane();
        Container rootWindow = (Container) rpc;
        cp.setLayout(null);
        rootWindow.setSize(W, H);
        rootWindow.setLocation(x, y);
        Font f = new Font("Monospaced", Font.PLAIN, 12);
        pageTitle.setBounds(110, 0, 270, 20);
        pageTitle.setText("User Login");
        pageTitle.setFont(f);
        cp.add(pageTitle);
        usernameInput.setBounds(40, 30, 300, 20);
        usernameInput.setText("Enter Username");
        usernameInput.setFont(f);
        usernameInput.setEditable(true);
        cp.add(usernameInput);
        passwordInput.setBounds(40, 60, 300, 20);
        passwordInput.setText("Enter Password");
        passwordInput.setFont(f);
        passwordInput.setEditable(true);
        cp.add(passwordInput);
        rootWindow.setVisible(true);
        theBtCheck.setBounds(40, 80, 100, 40);
        theBtCheck.setText("Login");
        theBtCheck.setFont(f);
        theBtCheck.addActionListener(e -> {
            try {
                cont.login(usernameInput.getText(), new String(passwordInput.getPassword()));
                passwordInput.setText("");
            } catch (NoSuchAlgorithmException | SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        cp.add(theBtCheck);
        theBtOpenPanel.setBounds(40, 120, 200, 40);
        theBtOpenPanel.setText("Register New Account");
        theBtOpenPanel.setFont(f);
        theBtOpenPanel.addActionListener(e -> {
            cont.openRegisterPanel(x + 40, y);
        });
        cp.add(theBtOpenPanel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(f);
        logoutButton.setBounds(40, 170, 100, 40);
        logoutButton.addActionListener(e -> {
            cont.logout();
        });
        cp.add(logoutButton);
    }



        /**
         * The controller object, used so that an interaction can be passed to the controller
         *
         * @param a The controller
         */

        public void setController(LoginController a) {
            cont = a;
            cont.getRpc();
        }
        public Window getWindow() {
            return (Window) this.rpc;
        }

        /**
         * Update the view
         * @param modelA   The observed model
         * @param arg      Specific args
         */
    }
