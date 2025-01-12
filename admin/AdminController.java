package admin;

import javax.swing.*;

import static java.awt.SystemColor.window;

public class AdminController {
    public static void OpenAdminPanel() {
        JFrame window = new JFrame();
        AdminView.OpenPanel(window, 0, 0);
    }
    public static void checkAdminDetails(String username, char[] password) {

    }
}
