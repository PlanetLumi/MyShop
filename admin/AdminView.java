package admin;

import clients.Picture;
import middle.MiddleFactory;
import middle.StockReader;

import javax.swing.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import java.util.Observer;

import static admin.AdminController.checkAdminDetails;

/**
 * Implements the Customer view.
 */

public class AdminView implements Observer
{
    @Override
    public void update(Observable o, Object arg) {

    }

    class Name                              // Names of buttons
    {
        public static final String CHECK  = "Check";
        public static final String CLEAR  = "Clear";
    }

    private static final int H = 300;       // Height of window pixels
    private static final int W = 400;       // Width  of window pixels

    private static final JLabel      pageTitle  = new JLabel();
    private static final JLabel      theAction  = new JLabel();
    private static final JTextField  usernameInput   = new JTextField();
    private static JPasswordField  passwordInput   = new JPasswordField();
    private final JTextArea   theOutput  = new JTextArea();
    private final JScrollPane theSP      = new JScrollPane();
    private static final JButton     theBtCheck = new JButton( Name.CHECK );
    private static final JButton     theBtClear = new JButton( Name.CLEAR );
    private static final JButton     theBtOpenPanel = new JButton();

    private Picture thePicture = new Picture(80,80);
    private StockReader theStock   = null;
    private AdminController cont= null;

    /**
     * Construct the view
     * @param rpc   Window in which to construct
     * @param mf    Factor to deliver order and stock objects
     * @param x     x-cordinate of position of window on screen
     * @param y     y-cordinate of position of window on screen
     */

    public AdminView( RootPaneContainer rpc, MiddleFactory mf, int x, int y )
    {
        Container cp         = rpc.getContentPane();    // Content Pane
        Container rootWindow = (Container) rpc;         // Root Window
        cp.setLayout(null);                             // No layout manager
        rootWindow.setSize( W, H );                     // Size of Window
        rootWindow.setLocation( x, y );

        Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is

        pageTitle.setBounds( 110, 0 , 270, 20 );
        pageTitle.setText( "Admin Options" );
        cp.add( pageTitle );
        theBtOpenPanel.setBounds(110, 120, 270, 40);
        theBtOpenPanel.setText( "Open Login Panel" );
        theBtOpenPanel.addActionListener( e -> {
            try {
                AdminController.OpenAdminPanel();
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        });
        cp.add( theBtOpenPanel );

        //  Add to canvas
        rootWindow.setVisible( true );                  // Make visible);

    }
    public static void OpenPanel(RootPaneContainer rpc, int x, int y) throws NoSuchAlgorithmException {
        Container cp = rpc.getContentPane();
        Container rootWindow = (Container) rpc;
        cp.setLayout(null);
        rootWindow.setSize( W, H );
        rootWindow.setLocation( x, y );
        Font f = new Font("Monospaced",Font.PLAIN,12);
        pageTitle.setBounds( 110, 0 , 270, 20 );
        pageTitle.setText( "Admin Login" );
        pageTitle.setFont(f);
        cp.add(pageTitle);
        usernameInput.setBounds(40,30,300,20);
        usernameInput.setText("Enter Username");
        usernameInput.setFont(f);
        usernameInput.setEditable(true);
        cp.add(usernameInput);
        passwordInput.setBounds(40,60,300,20);
        passwordInput.setText("Enter Password");
        passwordInput.setFont(f);
        passwordInput.setEditable(true);
        cp.add(passwordInput);
        rootWindow.setVisible( true );
        theBtCheck.setBounds(40,80,40,40);
        theBtCheck.setText( "Check Admin Login" );
        theBtCheck.setFont(f);
        theBtCheck.addActionListener( e -> { checkAdminDetails(usernameInput.getText(), passwordInput.getPassword()); passwordInput.setText(""); } );
        cp.add(theBtCheck);
        AdminController.CreateAdminTable();
        AdminController.InjectAdmin();
    }

    /**
     * The controller object, used so that an interaction can be passed to the controller
     * @param a   The controller
     */

    public void setController( AdminController a )
    {
        cont = a;
    }

    /**
     * Update the view
     * @param modelA   The observed model
     * @param arg      Specific args
     */
}
