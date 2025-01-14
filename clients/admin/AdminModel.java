package clients.admin;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

import clients.accounts.AccountCreation;
import middle.MiddleFactory;
import security.Encryption;
import java.util.Observable;


public class AdminModel extends Observable {
    public AdminModel(MiddleFactory mf) {
    }
    public static void injectAdmin() throws NoSuchAlgorithmException, SQLException {
        String salt = Encryption.generateSalt();
        AccountCreation creation = new AccountCreation();
        creation.createAccount("admin1", Encryption.hashPassword("RainyDayz49!", salt), salt, "admin");
    }
}
