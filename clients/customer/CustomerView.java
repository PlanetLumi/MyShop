package clients.customer;

import catalogue.Basket;
import catalogue.BetterBasket;
import clients.Picture;
import clients.accounts.AccountCreation;
import clients.accounts.Session;
import clients.accounts.SessionManager;
import middle.MiddleFactory;
import middle.StockException;
import middle.StockReader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;



/**
 * Implements the Customer view.
 */

public class CustomerView implements Observer
{
  class Name                              // Names of buttons
  {
    public static final String CHECK  = "Check";
    public static final String CLEAR  = "Clear";
  }

  private static final int H = 600;       // Height of window pixels
  private static final int W = 700;       // Width  of window pixels

  private final JLabel      pageTitle  = new JLabel();
  private final JLabel      theAction  = new JLabel();
  private final JTextField  theInput   = new JTextField();
  private final JTextArea   theOutput  = new JTextArea();
  private final JScrollPane theSP      = new JScrollPane();
  private final JButton     theBtCheck = new JButton( Name.CHECK );
  private final JButton     theBtClear = new JButton( Name.CLEAR );
  private final JButton     addToBasket = new JButton();
  private final JButton     thePlusBtn = new JButton();
  private final JButton     theMinusBtn = new JButton();
  private final JLabel      theQuantity  = new JLabel();
  private final JButton     theViewBasketBtn = new JButton();
  private Picture thePicture = new Picture(80,80);
  private StockReader theStock   = null;
  private CustomerController cont = null;

  /**
   * Construct the view
   * @param rpc   Window in which to construct
   * @param mf    Factor to deliver order and stock objects
   * @param x     x-cordinate of position of window on screen 
   * @param y     y-cordinate of position of window on screen  
   **/

  public void setController( CustomerController c )
  {
    cont = c;
  }

  public CustomerView( RootPaneContainer rpc, MiddleFactory mf, int x, int y ) throws SQLException {
    try                                             // 
    {      
      theStock  = mf.makeStockReader();             // Database Access
    } catch ( Exception e )
    {
      System.out.println("Exception: " + e.getMessage() );
    }
    Container cp         = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    cp.setLayout(null);                             // No layout manager
    rootWindow.setSize( W, H );                     // Size of Window
    rootWindow.setLocation( x, y );

    Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is
    
    pageTitle.setBounds( 110, 0 , 270, 20 );       
    pageTitle.setText( "Search products" );                        
    cp.add( pageTitle );

    theBtCheck.setBounds( 16, 25+60*0, 80, 40 );    // Check button
    theBtCheck.addActionListener(                   // Call back code
      e -> {
          try {
              cont.doCheck( theInput.getText() );
              cont.getPicture();
          } catch (StockException | RemoteException ex) {
              throw new RuntimeException(ex);
          }
      });
    cp.add( theBtCheck );                           //  Add to canvas

    theBtClear.setBounds( 16, 25+60*1, 80, 40 );    // Clear button
    theBtClear.addActionListener(                   // Call back code
      e -> cont.doClear() );
    cp.add( theBtClear );                           //  Add to canvas

    theAction.setBounds( 110, 25 , 1000, 20 );       // Message area
    theAction.setText( " " );                       // blank
    cp.add( theAction );                            //  Add to canvas

    theInput.setBounds( 110, 50, 400, 40 );         // Product no area
    theInput.setText("");                           // Blank
    cp.add( theInput );                             //  Add to canvas
    
    theSP.setBounds( 110, 100, 400, 160 );          // Scrolling pane
    theOutput.setText( "" );                        //  Blank
    theOutput.setFont( f );                         //  Uses font  
    cp.add( theSP );                                //  Add to canvas
    theSP.getViewport().add( theOutput );           //  In TextArea

    thePicture.setBounds( 16, 25+60*2, 80, 80 );   // Picture area
    cp.add( thePicture );                           //  Add to canvas
    thePicture.clear();

    thePlusBtn.setBounds(200,60,100, 10);
    thePlusBtn.setText("+");
    thePlusBtn.addActionListener(e -> {
      if(!theOutput.getText().isEmpty()) {
        cont.plusOne();
        theQuantity.setText(String.valueOf(cont.getCurrentQuantity()));
      }else{
            JOptionPane.showMessageDialog((Component) rpc, "Please enter a product");
      }
    });
    cp.add( thePlusBtn );
    theMinusBtn.setBounds(200,80,100, 10);
    theMinusBtn.setText("-");
    theMinusBtn.addActionListener(e -> {
      if(!theOutput.getText().isEmpty() || !theInput.getText().isEmpty()) {
        cont.takeOne();
        theQuantity.setText(String.valueOf(cont.getCurrentQuantity()));
      } else{
        JOptionPane.showMessageDialog((Component) rpc, "Please enter a product");
      }
    });
    cp.add( theMinusBtn );
    addToBasket.setBounds(200, 100, 200, 40);
    addToBasket.setText("Add To Basket");
    addToBasket.setFont(f);
    addToBasket.addActionListener( e-> {
        cont.addToBasket();
        cont.clearQuantity();
        theQuantity.setText(String.valueOf(cont.getCurrentQuantity()));
    } );
    theViewBasketBtn.setBounds(200, 150, 200, 40);
    theViewBasketBtn.setText("Open Basket");
    theViewBasketBtn.setFont(f);
    theViewBasketBtn.addActionListener( e-> {
      cont.submitBasket();
      openCustomerBasketView((RootPaneContainer) theViewBasketBtn.getTopLevelAncestor(), mf,x ,y);

    });
    rootWindow.setVisible( true );                  // Make visible);
    theInput.requestFocus();

// Focus is here
  }

  public void openCustomerBasketView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
    try {
      theStock = mf.makeStockReader(); // Database Access
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
    }
    Container cp = rpc.getContentPane(); // Content Pane
    Container rootWindow = (Container) rpc; // Root Window
    cp.removeAll(); // Clear previous components
    cp.setLayout(null); // No layout manager
    rootWindow.setSize(W, H); // Set window size
    rootWindow.setLocation(x, y);

    Font f = new Font("Monospaced", Font.PLAIN, 12); // Font setup

// Page Title
    pageTitle.setBounds(110, 0, 270, 20);
    pageTitle.setText("Your Basket");
    cp.add(pageTitle);

// Input Field for Searching Basket
    theInput.setBounds(50, 50, 400, 30);
    theInput.setText("Search Basket...");
    cp.add(theInput);

// JList for Displaying Basket Items
    DefaultListModel<String> listModel = new DefaultListModel<>();
    JList<String> theOutput = new JList<>(listModel);
    theOutput.setFont(f);

// Scroll Pane for the JList
    theSP.setBounds(50, 100, 400, 400);
    theSP.getViewport().add(theOutput);
    cp.add(theSP);

// Add Document Listener to Input Field
    theInput.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent e) {
        try {
          updateList();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        try {
          updateList();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        try {
          updateList();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }

      private void updateList() throws SQLException {
        String search = theInput.getText(); // Get the search input
        List<String> userItems = getBskItmByContent(search); // Fetch basket items
        listModel.clear(); // Clear current list items
        userItems.forEach(listModel::addElement); // Add new items to the list
      }
    });

    JButton dropItmBtn = new JButton("Drop Item");
    dropItmBtn.setBounds(500, 40, 100, 30);
    dropItmBtn.setFont(f);
    dropItmBtn.setText("Drop Item");
    dropItmBtn.addActionListener(e -> {
      String selected = theInput.getText();
      if(selected != null && !selected.isEmpty()) {
        try {
          cont.dropBskItem(theOutput.getSelectedValue());
          listModel.removeElement(selected);
        } catch (Exception e1) {
          JOptionPane.showMessageDialog((Component) rpc, "Error dropping item");
        }
      }
    });
    cp.add(dropItmBtn);
    thePlusBtn.setBounds(200, 150, 200, 40);
    thePlusBtn.setText("+");
    thePlusBtn.addActionListener(e -> {
      cont.
    })


// Make the root window visible
    rootWindow.setVisible(true);
    theInput.requestFocus();


  }
  private List getBskItmByContent(String query) throws SQLException {
    return cont.getBskItmByContent(query);
  }


  /**
  * The controller object, used so that an interaction can be passed to the controller
  * @param c   The controller
  */


  /**
   * Update the view
   * @param modelC   The observed model
   * @param arg      Specific args 
   */
  public void displayMessage(RootPaneContainer rpc) throws SQLException {
    String message = CustomerController.displayMessage();
    if(!Objects.equals(message, "[]") && !Objects.equals(message, null)) {
      JOptionPane.showMessageDialog((Component) rpc, message);
      cont.clearMessage();
    }
  }

  public void update(Observable modelC, Object arg) {
    CustomerModel model = (CustomerModel) modelC;
    String message = (String) arg;
    theAction.setText(message);
    ImageIcon image = model.getPicture();
    if (image != null) {
      thePicture.set(image);
    } else {
      thePicture.clear();
    }
    theOutput.setText(model.getBasket().getDetails());
  }

}
