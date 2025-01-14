package clients.admin;

import javax.swing.*;
import java.sql.SQLException;

import java.security.NoSuchAlgorithmException;

public class AdminController {
    private AdminModel model;
    private AdminView view;
    public AdminController( AdminModel model, AdminView adminView) {
        this.model = model;
        this.view = adminView;
    }
    public void InjectAdmin() throws NoSuchAlgorithmException, SQLException {
        AdminModel.injectAdmin();
    }
}
