package clients.backDoor;

import middle.MiddleFactory;
import middle.Names;
import middle.RemoteMiddleFactory;

import javax.swing.*;

/**
 * The standalone BackDoor Client
 */
public class BackDoorClient {
    public static void main(String[] args) {
        String stockURL = args.length < 1 // URL of stock RW
                ? Names.STOCK_RW // default location
                : args[0]; // supplied location
        String orderURL = args.length < 2 // URL of order
                ? Names.ORDER // default location
                : args[1]; // supplied location

        RemoteMiddleFactory mrf = new RemoteMiddleFactory();
        mrf.setStockRWInfo(stockURL);
        mrf.setOrderInfo(orderURL);
        displayGUI(mrf); // Create GUI
    }

    private static void displayGUI(MiddleFactory mf) {
        JFrame window = new JFrame();

        window.setTitle("BackDoor Client (MVC RMI)");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackDoorModel model = new BackDoorModel(mf);
        BackDoorView view = new BackDoorView(window, mf, 0, 0);
        BackDoorController cont = new BackDoorController(model, view);
        view.setController(cont);

        // Add listener to the model - view is listener, model has PropertyChangeSupport
        model.addPropertyChangeListener(view);
        window.setVisible(true); // Display Screen
    }
}
