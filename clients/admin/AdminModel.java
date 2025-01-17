package clients.admin;

import clients.PosOnScrn;
import clients.accounts.AccountCreation;
import middle.MiddleFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;

/**
 * Model class for the Admin module. Contains the business logic
 * for managing admins, employees, and users.
 */
public class AdminModel extends Observable {

    private final MiddleFactory middleFactory;
    private AdminController controller;

    /**
     * Constructor that accepts a MiddleFactory, which is typically
     * used to obtain references to database or other middle-tier
     * components.
     *
     * @param middleFactory MiddleFactory object for connectivity
     */
    public AdminModel(MiddleFactory middleFactory) {
        this.middleFactory = middleFactory;
    }

    /**
     * Injects a default admin into the database for testing or initialization.
     *
     * @throws NoSuchAlgorithmException if the encryption algorithm is invalid.
     * @throws SQLException            if database operations fail.
     */

    /**
     * Sets the controller for this model.
     *
     * @param controller AdminController instance
     */
    public void setController(AdminController controller) {
        this.controller = controller;
    }

    /**
     * Opens a panel to manage employees, including the functionality
     * to promote them to managers.
     *
     * @param x X-coordinate on the screen
     * @param y Y-coordinate on the screen
     */
    public void openManagerEmployeePanel(int x, int y) {
        JFrame frame = new JFrame("Open Manager Employee Panel");
        Dimension pos = PosOnScrn.getPos();

        // Configure the JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(pos.width, pos.height));
        frame.setLocation(x, y);

        // Create and set up the view
        AdminView view = new AdminView(frame, middleFactory, pos.width, pos.height);
        view.AdminCreatePanel(frame, middleFactory, pos.width, pos.height);
        view.setController(controller);

        addObserver(view);

        frame.setVisible(true);
    }

    /**
     * Injects multiple employee accounts for testing or seeding the database.
     *
     * @throws SQLException if database operations fail
     */


    /**
     * Injects multiple user accounts for testing or seeding the database.
     *
     * @throws SQLException if database operations fail
     */


    /**
     * Promotes a given employee to a manager (admin role).
     *
     * @param employee The employee username to promote
     * @param frame    The GUI frame for showing messages
     */
    public void promoteToManager(String employee, JFrame frame) {
        if (employee != null) {
            try {
                AccountCreation creator = new AccountCreation();
                creator.newData("Accounts", "role", new String[]{"admin"}, creator.getID(employee));

                JOptionPane.showMessageDialog(frame, "Employee " + employee + " has been promoted to manager.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame,
                        "Error promoting employee: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an employee.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Locks or unlocks a user's account based on the boolean flag.
     *
     * @param username The username of the account to lock/unlock
     * @param lock     True to lock; false to unlock
     */
    public void lockUser(String username, boolean lock) {
        AccountCreation account = new AccountCreation();
        account.lockUser(account.getID(username), lock);
    }

    /**
     * Opens the security manager panel, which handles user locking/unlocking
     * and messaging.
     *
     * @param x X-coordinate on the screen
     * @param y Y-coordinate on the screen
     */
    public void openSecurityManagerPanel(int x, int y) {
        JFrame frame = new JFrame("Manager Security Panel");
        Dimension pos = PosOnScrn.getPos();

        // Configure the JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(pos.width, pos.height));
        frame.setLocation(x, y);

        // Create and set up the view
        AdminView view = new AdminView(frame, middleFactory, pos.width, pos.height);
        view.AdminSecurityPanel(frame, middleFactory, pos.width, pos.height);
        view.setController(controller);

        addObserver(view);

        frame.setVisible(true);
    }

    /**
     * Sends a text message to a particular user.
     *
     * @param username The user’s username
     * @param message  The message to send
     * @throws SQLException if database operations fail
     */
    public void sendUserMessage(String username, String message) throws SQLException {
        AccountCreation account = new AccountCreation();
        account.newData("UserDetails", "message", new String[]{message}, account.getID(username));
    }
}
