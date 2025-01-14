package clients.admin;
import middle.MiddleFactory;
import middle.RemoteMiddleFactory;

import javax.swing.*;

/**
 * The standalone BackDoor Client
 */


public class AdminClient
{
    public static void main (String args[])
    {
        RemoteMiddleFactory mrf = new RemoteMiddleFactory();
        displayGUI(mrf);                       // Create GUI
    }

    private static void displayGUI(MiddleFactory mf)
    {
        JFrame  window = new JFrame();

        window.setTitle("Admin Client");
        window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        AdminModel model = new AdminModel(mf);
        AdminView view  = new AdminView( window, mf, 0, 0 );
        AdminController cont  = new AdminController(model, view);
        view.setController( cont );

        model.addObserver(view);       // Add observer to the model - view is observer, model is Observable
        window.setVisible(true);         // Display Screen
    }
}
