package clients.packing;


import middle.MiddleFactory;
import middle.Names;
import middle.RemoteMiddleFactory;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Observer;

/**
 * The standalone warehouse Packing Client. warehouse staff to pack the bought order
 * @author  Mike Smith University of Brighton
 * @version 2.0
 * @author  Shine University of Brighton
 * @version year 2024
 */
public class PackingClient
{
    public static void main (String args[]) throws SQLException {
        String stockURL = args.length < 1     // URL of stock RW
                ? Names.STOCK_RW      //  default  location
                : args[0];            //  supplied location
        String orderURL = args.length < 2     // URL of order
                ? Names.ORDER         //  default  location
                : args[1];            //  supplied location

        RemoteMiddleFactory mrf = new RemoteMiddleFactory();
        mrf.setStockRWInfo( stockURL );
        mrf.setOrderInfo  ( orderURL );        //
        displayGUI(mrf);                       // Create GUI
    }

    public static void displayGUI(MiddleFactory mf) throws SQLException {
        JFrame  window = new JFrame();

        window.setTitle( "Packing Client (RMI MVC)");
        window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PackingModel      model = new PackingModel(mf);
        PackingController cont  = new PackingController( model );
        PackingView       view  = new PackingView(cont);
        view.setController( cont );

        model.addObserver((Observer) view);       // Add observer to the model
        window.setVisible(true);         // Display Screen
    }
}

