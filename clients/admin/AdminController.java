package clients.admin;

import clients.accounts.AccountCreation;
import middle.MiddleFactory;

import javax.swing.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller class for the Admin module. Communicates between
 * the AdminModel (business logic) and the AdminView (GUI).
 */
public class AdminController {

    private final AdminModel model;
    private final AdminView view;

    /**
     * Constructs the AdminController with a given model and view.
     *
     * @param model The AdminModel instance
     * @param view  The AdminView instance
     */
    public AdminController(AdminModel model, AdminView view) {
        this.model = model;
        this.view = view;
        this.model.setController(this);  // Link back to the controller in the model
    }

    /**
     * Injects a default admin into the database, for test or initial setup.
     *
     * @throws NoSuchAlgorithmException if the password hashing algorithm is invalid
     * @throws SQLException            if database operations fail
     */


    /**
     * Retrieves employees matching a certain query (e.g., partial username).
     *
     * @param query The search string
     * @return A list of matching employees (as Strings)
     * @throws SQLException if database operations fail
     */
    public List<String> getEmployees(String query) throws SQLException {
        AccountCreation account = new AccountCreation();
        return account.getUser(query, "cashier", false);
    }

    /**
     * Tells the model to open the Manager Employee Panel.
     *
     * @param x X-coordinate of the new panel
     * @param y Y-coordinate of the new panel
     * @throws SQLException if database operations fail
     */
    public void openManagerEmployeePanel(int x, int y) throws SQLException {
        model.openManagerEmployeePanel(x, y);
    }

    /**
     * Tells the model to open the Security Manager Panel.
     *
     * @param x X-coordinate of the new panel
     * @param y Y-coordinate of the new panel
     */
    public void openSecurityManagerPanel(int x, int y) {
        model.openSecurityManagerPanel(x, y);
    }


    /**
     * Promotes the selected employee to manager.
     *
     * @param employee The username of the employee
     * @param frame    The GUI frame for displaying messages
     * @throws SQLException if database operations fail
     */
    public void promoteCurrentEmployee(String employee, JFrame frame) throws SQLException {
        model.promoteToManager(employee, frame);
    }

    /**
     * Retrieves normal users matching a certain query, optionally filtered by locked status.
     *
     * @param query           The search string
     * @param showLockedOnly  Whether to retrieve only locked accounts
     * @return A list of matching users (as Strings)
     * @throws SQLException   if database operations fail
     */
    public List<String> getUsers(String query, boolean showLockedOnly) throws SQLException {
        AccountCreation account = new AccountCreation();
        return account.getUser(query, "user", showLockedOnly);
    }

    /**
     * Locks or unlocks a user account through the model.
     *
     * @param username The username to lock or unlock
     * @param lock     True to lock; false to unlock
     * @throws SQLException if database operations fail
     */
    public void lockUser(String username, boolean lock) throws SQLException {
        model.lockUser(username, lock);
    }

    /**
     * Sends a message to the specified user.
     *
     * @param username The user’s username
     * @param message  The message text
     * @throws SQLException if database operations fail
     */
    public void sendUserMessage(String username, String message) throws SQLException {
        model.sendUserMessage(username, message);
    }
}
