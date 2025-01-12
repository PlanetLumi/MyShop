package admin;

import javax.swing.*;
import java.util.Arrays;

import java.security.NoSuchAlgorithmException;

import static java.awt.Color.orange;
import static java.awt.SystemColor.window;

public class AdminController {
    public static void OpenAdminPanel() throws NoSuchAlgorithmException {
        JFrame window = new JFrame();
        AdminView.OpenPanel(window, 0, 0);
    }
    public static void CreateAdminTable(){
        AdminModel.callCreate();
    }
    public static void InjectAdmin() throws NoSuchAlgorithmException {
        AdminModel.populateAccount();
    }
    public static void checkAdminDetails(String username, char[] password){
        AdminModel.loginAdmin(username, new String(password));
        password = null;
    }
}
