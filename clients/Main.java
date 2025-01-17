package clients;

import clients.admin.AdminController;
import clients.admin.AdminModel;
import clients.admin.AdminView;
import clients.backDoor.BackDoorClient;
import clients.backDoor.BackDoorController;
import clients.backDoor.BackDoorModel;
import clients.backDoor.BackDoorView;

import clients.customer.CustomerController;
import clients.customer.CustomerModel;
import clients.customer.CustomerView;
import clients.packing.PackingClient;
import clients.packing.PackingController;
import clients.packing.PackingModel;
import clients.packing.PackingView;
import login.LoginController;
import login.LoginModel;
import login.LoginView;
import middle.LocalMiddleFactory;
import middle.MiddleFactory;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import static clients.customer.CustomerModel.getMessage;

/**
 * Starts all the clients (user interface)  as a single application.
 * Good for testing the system using a single application.
 * @author  Mike Smith University of Brighton
 * @version 2.0
 * @author  Shine University of Brighton
 * @version year-2024
 */

public class Main
{
  JOptionPane popup = null;
  public static void main (String args[]) throws Exception {
    new Main().begin();
  }

  /**
   * Starts the system (Non distributed)
   */
  public void begin() throws Exception {
    //DEBUG.set(true); /* Lots of debug info */
    MiddleFactory mlf = new LocalMiddleFactory();  // Direct access
    startLoginGUI_MVC( mlf );
  }
  public void userOpen() throws SQLException {
    MiddleFactory mlf = new LocalMiddleFactory();
    startCustomerGUI_MVC( mlf );
  }
  public void cashierOpen() throws SQLException {
    MiddleFactory mlf = new LocalMiddleFactory();
    startPackingGUI_MVC( mlf );
    startBackDoorGUI_MVC(mlf);
  }
  public void managerOpen(MiddleFactory mlf) throws SQLException {
    startAdminGUI_MVC(mlf);
  }
  /**
  * start the Customer client, -search product
  * @param mlf A factory to create objects to access the stock list
  */
  public void startCustomerGUI_MVC(MiddleFactory mlf ) throws SQLException {
    JFrame  window = new JFrame();
    window.setTitle( "Customer Client MVC");
    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    Dimension pos = PosOnScrn.getPos();
    
    CustomerModel model      = new CustomerModel(mlf);
    CustomerView view        = new CustomerView( window, mlf, pos.width, pos.height );
    CustomerController cont  = new CustomerController( model, view );
    view.setController( cont );

    model.addObserver( view );       // Add observer to the model, ---view is observer, model is Observable
    if(!getMessage().isEmpty() && !getMessage().equals("[]") && !getMessage().equals("[null]")){
      JOptionPane.showMessageDialog(window,getMessage());
      model.clearMessage();
    }
    window.setVisible(true);         // start Screen
  }

  /**
   * start the cashier client - customer check stock, buy product
   * @param mlf A factory to create objects to access the stock list
   */

  /**
   * start the Packing client - for warehouse staff to pack the bought order for customer, one order at a time
   * @param mlf A factory to create objects to access the stock list
   */
  
  public void startPackingGUI_MVC(MiddleFactory mlf) throws SQLException {
    PackingClient.displayGUI(mlf);
  }
  
  /**
   * start the BackDoor client - store staff to check and update stock
   * @param mlf A factory to create objects to access the stock list
   */
  public void startBackDoorGUI_MVC(MiddleFactory mlf ) throws SQLException {
    BackDoorClient.displayGUI(mlf);
  }
  public void startLoginGUI_MVC(MiddleFactory mlf)  {
    JFrame  window = new JFrame();
    window.setTitle( "Login Client MVC");
    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    Dimension pos = PosOnScrn.getPos();
    LoginModel model      = new LoginModel(mlf);
    LoginView view        = new LoginView( window, mlf, pos.width, pos.height );
    LoginController cont  = new LoginController( model, view );
    view.setController(cont);
    model.addObserver(view);
    window.setVisible(true);
  }
  public void startAdminGUI_MVC(MiddleFactory mlf)  {
    JFrame  window = new JFrame();
    window.setTitle( "Admin Client MVC");
    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    Dimension pos = PosOnScrn.getPos();
    AdminModel model      = new AdminModel(mlf);
    AdminView view        = new AdminView( window, mlf, pos.width, pos.height );
    AdminController cont  = new AdminController( model, view );
    view.setController( cont );
    model.addObserver( view );
    window.setVisible(true);
  }
  
}
