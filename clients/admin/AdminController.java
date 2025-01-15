package clients.admin;

import clients.accounts.AccountCreation;

import javax.swing.*;
import java.sql.SQLException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static java.awt.SystemColor.window;

public class AdminController {
    private AdminModel model;
    private AdminView view;

    public AdminController(AdminModel model, AdminView adminView) {
        this.model = model;
        this.view = adminView;
        model.setController(this);
    }

    public static void injectAdmin() throws NoSuchAlgorithmException, SQLException {
        AdminModel.injectAdmin();
    }

    public List getEmployees(String query) throws SQLException {
        AccountCreation account = new AccountCreation();
        return account.getUser(query, "cashier", false);
    }
    public void openManagerEmployeePanel(int x, int y) throws SQLException {
        model.openManagerEmployeePanel(x, y);
    }
    public void openSecurityManagerPanel(int x, int y){
        model.openSecurityManagerPanel(x, y);
    }
    public static void injectEmployees() throws SQLException {
        AdminModel.injectEmployees();
    }
    public static void injectUsers() throws SQLException {
        AdminModel.injectUsers();
    }
    public void promoteCurrentEmployee(String employee, JFrame frame) throws SQLException {
        model.promoteToManager(employee, frame);
    }
    public List getUsers(String query, boolean showLockedOnly) throws SQLException {
        AccountCreation account = new AccountCreation();
        return account.getUser(query, "user", showLockedOnly);
    }
    public void lockUser(String username, boolean lock) throws SQLException {
            model.lockUser(username, lock);
    }
    public void sendUserMessage(String username, String message) throws SQLException {
        System.out.println(message);
        model.sendUserMessage(username, message);
    }
}

