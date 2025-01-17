package login;

import clients.UtilClass;
import clients.Picture;
import clients.UtilClass;
import middle.MiddleFactory;
import middle.StockReader;

import javax.swing.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

public class RegisterView implements Observer {

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
    private static final JButton theBtCheck = UtilClass.createRoundedButton(Name.CHECK);
    private static final JButton theBtClear = UtilClass.createRoundedButton(Name.CLEAR);
    private static final JButton theBtOpenPanel = UtilClass.createRoundedButton("");

    private Picture thePicture = new Picture(80, 80);
    private StockReader theStock = null;
    private LoginController cont = null;

    public RegisterView(JFrame window, MiddleFactory mf, int x, int y) {
        Container cp = window.getContentPane();
        cp.setLayout(null);


        // Set the size and position of the frame
        window.setSize(W, H);
        window.setLocation(x, y);
        UtilClass.setTurquoiseBackground(window);

        Font f = new Font("Monospaced", Font.PLAIN, 12);
        pageTitle.setBounds(110, 0, 270, 20);
        pageTitle.setForeground(Color.BLACK);
        pageTitle.setText("User Register");
        pageTitle.setFont(f);
        cp.add(pageTitle);
        usernameInput.setBounds(40, 30, 300, 20);
        usernameInput.setText("Enter Username");
        usernameInput.setFont(f);
        usernameInput.setEditable(true);
        usernameInput.setForeground(Color.BLACK);
        cp.add(usernameInput);
        passwordInput.setBounds(40, 80, 400, 20);
        passwordInput.setText("Enter Password");
        passwordInput.setFont(f);
        passwordInput.setEditable(true);
        cp.add(passwordInput);
        passwordInputRe.setBounds(40, 100, 400, 20);
        passwordInputRe.setText("Please confirm password");
        passwordInputRe.setFont(f);
        passwordInputRe.setEditable(true);
        cp.add(passwordInputRe);
        cp.setVisible(true);
        theBtCheck.setBounds(40, 120, 200, 40);
        theBtCheck.setText("Create Account");
        theBtCheck.setFont(f);
        theBtCheck.addActionListener(e -> {
            try {
                cont.createAccount(usernameInput.getText(), new String(passwordInput.getPassword()), new String(passwordInputRe.getPassword()), "user");
                passwordInput.setText("");
            } catch (NoSuchAlgorithmException | SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        cp.add(theBtCheck);
    }
        /**
         * The controller object, used so that an interaction can be passed to the controller
         *
         * @param a The controller
         */

        public void setController(LoginController a) {
            cont = a;
        }

        /**
         * Update the view
         * @param modelA   The observed model
         * @param arg      Specific args
         */
}
