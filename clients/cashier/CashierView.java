package clients.cashier;

import catalogue.Basket;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockReadWriter;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;


/**
 * View of the model 
 */
public class CashierView implements Observer
{
  private static final int H = 400;       // Height of window pixels
  private static final int W = 400;       // Width  of window pixels
  
  private static final String CHECK  = "Check";
  private static final String ADD    = "Add";
  private static final String REMOVE    = "Remove";
  private static final String CLEAR    = "Clear";
  private static final String BOUGHT = "PAY";

  private final JLabel      pageTitle  = new JLabel();
  private final JLabel      theAction  = new JLabel();
  private final JTextField  theInput   = new JTextField();
  private final JTextArea   theOutput  = new JTextArea();
  private final JScrollPane theSP      = new JScrollPane();
  private final JButton     theBtCheck = new JButton( CHECK );
  private final JButton     theBtadd   = new JButton( ADD );
  private final JButton     theBtremove   = new JButton( REMOVE );
  private final JButton     theBtclear   = new JButton( CLEAR );
  private final JButton     theBtBought= new JButton( BOUGHT );

  private StockReadWriter theStock     = null;
  private OrderProcessing theOrder     = null;
  private CashierController cont       = null;
  
  /**
   * Construct the view
   * @param rpc   Window in which to construct
   * @param mf    Factor to deliver order and stock objects
   * @param x     x-coordinate of position of window on screen 
   * @param y     y-coordinate of position of window on screen  
   */
          
  public CashierView(  RootPaneContainer rpc,  MiddleFactory mf, int x, int y  )
  {
    try                                           // 
    {      
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch ( Exception e )
    {
      System.out.println("Exception: " + e.getMessage() );
    }
    Container cp         = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    cp.setLayout(null);                             // No layout manager
    rootWindow.setSize( W, H );                     // Size of Window
    rootWindow.setLocation( x, y );
    
    cp.setBackground(new Color(240, 248, 255)); //set background colour

    Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is
    Font boldf = new Font("Monospaced", Font.BOLD, 12); //Bold font

    pageTitle.setOpaque(true);
    pageTitle.setHorizontalAlignment(SwingConstants.CENTER); // Centre align the text
    pageTitle.setBounds(0, 0, W, 20);     // Centre horizontally within the window
    pageTitle.setFont(boldf);  //Bold font
    pageTitle.setForeground(Color.BLACK); //text colour
    pageTitle.setBackground(new Color(255, 223, 186)); //background colour
    pageTitle.setText( "Thank You for Shopping at Mini Store" ); //title
    
    cp.add( pageTitle, BorderLayout.NORTH);
    
    theBtCheck.setBounds( 16, 50, 80, 40 );    // Check Button
    theBtCheck.addActionListener(                   // Call back code
      e -> cont.doCheck( theInput.getText() ) );
    cp.add( theBtCheck );                           //  Add to canvas

    theBtadd.setBounds( 16, 100, 80, 40 );      // Add button 
    theBtadd.addActionListener(                     // Call back code
      e -> cont.doBuy() );
    cp.add( theBtadd );                             //  Add to canvas
    
    theBtremove.setBounds( 16, 100+60*1, 80, 40 );      // Remove button 
    theBtremove.addActionListener(                     // Call back code
      e -> cont.doBuy() );
    cp.add( theBtremove );                             //  Add to canvas
    
    theBtclear.setBounds( 16, 100+60*2, 80, 40 );      // Clear button 
    theBtclear.addActionListener(                     // Call back code
      e -> cont.doBuy() );
    cp.add( theBtclear );                             //  Add to canvas

    theBtBought.setBounds( 300, 320, 80, 40 );   // Pay Button
    theBtBought.addActionListener(                  // Call back code
      e -> cont.doBought() );
    cp.add( theBtBought );                          //  Add to canvas

    theAction.setBounds( 110, 25 , 270, 20 );       // Message area
    theAction.setText( "" );                        // Blank
    cp.add( theAction );                            //  Add to canvas

    theInput.setBounds( 110, 50, 270, 40 );         // Input Area
    theInput.setText("");                           // Blank
    theInput.setBackground(new Color(255, 223, 186)); //set colour
    cp.add( theInput );                             //  Add to canvas

    theSP.setBounds( 110, 100, 270, 220 );          // Scrolling pane
    theOutput.setText( "" );                        //  Blank
    theOutput.setFont( f );                         //  Uses font  
    cp.add( theSP );                                //  Add to canvas
    theSP.getViewport().add( theOutput );           //  In TextArea
    rootWindow.setVisible( true );                  // Make visible
    theInput.requestFocus();                        // Focus is here
  }

  /**
   * The controller object, used so that an interaction can be passed to the controller
   * @param c   The controller
   */

  public void setController( CashierController c )
  {
    cont = c;
  }

  /**
   * Update the view
   * @param modelC   The observed model
   * @param arg      Specific args 
   */
  @Override
  public void update( Observable modelC, Object arg )
  {
    CashierModel model  = (CashierModel) modelC;
    String      message = (String) arg;
    theAction.setText( message );
    Basket basket = model.getBasket();
    if ( basket == null )
      theOutput.setText( "Customers order" );
    else
      theOutput.setText( basket.getDetails() );
    
    theInput.requestFocus();               // Focus is here
  }

}
