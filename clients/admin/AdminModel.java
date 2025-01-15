package clients.admin;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import clients.PosOnScrn;
import clients.accounts.AccountCreation;
import login.LoginController;
import login.RegisterView;
import middle.MiddleFactory;
import security.Encryption;

import javax.swing.*;
import java.util.Observable;

import static java.awt.SystemColor.window;


public class AdminModel extends Observable {
    MiddleFactory mlf;
    AdminController cont;
    public AdminModel(MiddleFactory mf) {
        this.mlf = mf;
    }
    public static void injectAdmin() throws NoSuchAlgorithmException, SQLException {
        AccountCreation create = new AccountCreation();
        create.createAccount("admin1", "RainyDayz49!","admin");
    }
    public void setController(AdminController cont) {
        this.cont = cont;
    }
    public void openManagerEmployeePanel(int x, int y) {
        JFrame frame = new JFrame();
        frame.setTitle("Open Manager Employee Panel");
        Dimension pos = PosOnScrn.getPos();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AdminView view = new AdminView(frame, mlf, pos.width, pos.height);
        view.AdminCreatePanel(frame, mlf, pos.width, pos.height);
        view.setController(cont);
        addObserver(view);
        frame.setVisible(true);
    }
    public static void injectEmployees() throws SQLException {
        AccountCreation create = new AccountCreation();
        for(int x = 0; x < 25; x++) {
            String name = "employee" + x;
            String password = "password" + x;
            create.createAccount(name, password, "cashier");
        }
    }
    public static void injectUsers() throws SQLException {
        AccountCreation create = new AccountCreation();
        for(int x = 0; x < 25; x++) {
            String name = "user" + x;
            String password = "password" + x;
            create.createAccount(name, password, "user");
        }
    }
    public void promoteToManager(String employee, JFrame frame) {
        if (employee != null) {
            try {
                AccountCreation create = new AccountCreation();
                create.newData("Accounts","role", new String[]{"admin"}, create.getID(employee));
                JOptionPane.showMessageDialog(frame, "Employee " + employee + " has been promoted");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            JOptionPane.showMessageDialog(frame, "Please select an employee");
    }
    }
    public void lockUser(String username, boolean lock){
        AccountCreation account = new AccountCreation();
        account.lockUser(account.getID(username), lock);
    }

    public void openSecurityManagerPanel(int x, int y) {
        JFrame frame = new JFrame();
        frame.setTitle("Manager Security Panel");
        Dimension pos = PosOnScrn.getPos();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AdminView view = new AdminView(frame, mlf, pos.width, pos.height);
        view.AdminSecurityPanel(frame, mlf, pos.width, pos.height);
        view.setController(cont);
        addObserver(view);
        frame.setVisible(true);
    }
    public void sendUserMessage(String username, String message) throws SQLException {
        AccountCreation account = new AccountCreation();
        account.newData("UserDetails", "message",new String[] {message}, account.getID(username));
    }

}
