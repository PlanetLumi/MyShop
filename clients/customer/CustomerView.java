package clients.customer;

import clients.UtilClass;
import catalogue.Basket;
import catalogue.Product;
import clients.Picture;
import jdk.jshell.execution.Util;
import middle.MiddleFactory;
import middle.StockException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class CustomerView implements Observer
{
  private static final int W = 800;
  private static final int H = 600;

  private final JLabel pageTitle      = new JLabel("Search Products");
  private final JLabel theAction      = new JLabel("");

  private final JTextField theInput   = new JTextField();
  private final JButton theBtCheck    = UtilClass.createRoundedButton("Check");
  private final JButton theBtClear    = UtilClass.createRoundedButton("Clear");
  private final JLabel theQuantity    = new JLabel("Quantity:");
  private final JTextField theQtyField= new JTextField("1");
  private final JButton thePlusBtn    = UtilClass.createRoundedButton("+");
  private final JButton theMinusBtn   = UtilClass.createRoundedButton("-");
  private final JButton addToBasket   = UtilClass.createRoundedButton("Add To Basket");
  private final JButton theViewBasketBtn = UtilClass.createRoundedButton("Open Basket");
  private final JButton theSubmitBtn  = UtilClass.createRoundedButton("Purchase");
  private final Picture thePicture    = new Picture(80,80);

  private JTable productTable;
  private DefaultTableModel productTableModel;
  private JScrollPane productTableSP;

  private CustomerController cont;

  // We'll keep a reference to the RootPaneContainer, MiddleFactory, x, y
  // so we can re-build the main screen from a helper method:
  private RootPaneContainer rootRPC;
  private MiddleFactory     rootMF;
  private int               rootX, rootY;

  // Keep track of products from the last search
  private List<Product> lastSearchResults;

  public CustomerView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) throws SQLException
  {
    this.rootRPC = rpc;
    this.rootMF  = mf;
    this.rootX   = x;
    this.rootY   = y;

    buildMainScreen(rpc, mf, x, y);
  }

  private void buildMainScreen(RootPaneContainer rpc, MiddleFactory mf, int x, int y)
  {
    UtilClass.setTurquoiseBackground(rpc);
    Container cp         = rpc.getContentPane();
    Container rootWindow = (Container) rpc;
    cp.removeAll();
    cp.setLayout(null);
    rootWindow.setSize(W, H);
    rootWindow.setLocation(x, y);

    pageTitle.setBounds(110, 0, 270, 20);
    pageTitle.setForeground(Color.BLACK);

    cp.add(pageTitle);


    // Check & Clear
    theBtCheck.setBounds(16, 25, 80, 40);
    theBtClear.setBounds(16, 70, 80, 40);
    cp.add(theBtCheck);
    cp.add(theBtClear);

    // Action label
    theAction.setBounds(110, 25, 400, 20);
    cp.add(theAction);

    // Search input
    theInput.setBounds(110, 50, 400, 40);
    theInput.setForeground(Color.BLACK);
    cp.add(theInput);

    // Picture box
    thePicture.setBounds(16, 120, 80, 80);
    cp.add(thePicture);
    thePicture.clear();

    // The product table
    String[] columnNames = { "Product No", "Description", "Price", "Stock Level" };
    productTableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    productTable = new JTable(productTableModel);
    productTableSP = new JScrollPane(productTable);
    productTableSP.setBounds(110, 100, 400, 180);
    cp.add(productTableSP);

    // Quantity
    theQuantity.setBounds(520, 100, 60, 20);
    theQuantity.setForeground(Color.BLACK);
    cp.add(theQuantity);

    theQtyField.setBounds(580, 95, 60, 30);
    theQtyField.setForeground(Color.BLACK);
    cp.add(theQtyField);

    thePlusBtn.setBounds(650, 95, 45, 30);
    cp.add(thePlusBtn);
    theMinusBtn.setBounds(700, 95, 45, 30);
    cp.add(theMinusBtn);

    // Add to basket
    addToBasket.setBounds(520, 140, 150, 40);
    cp.add(addToBasket);

    // View basket
    theViewBasketBtn.setBounds(520, 190, 150, 40);
    cp.add(theViewBasketBtn);

    // Listeners
    theBtCheck.addActionListener(e -> {
      try {
        cont.doCheck(theInput.getText());
        cont.getImg();
      } catch (StockException | RemoteException ex) {
        ex.printStackTrace();
        throw new RuntimeException(ex);
      }
    });
    theBtClear.addActionListener(e -> cont.doClear());

    thePlusBtn.addActionListener(e -> {
      try {
        int val = Integer.parseInt(theQtyField.getText());
        theQtyField.setText(String.valueOf(val + 1));
      } catch (NumberFormatException ex) {
        theQtyField.setText("1");
      }
    });
    theMinusBtn.addActionListener(e -> {
      try {
        int val = Integer.parseInt(theQtyField.getText());
        if (val > 1) {
          theQtyField.setText(String.valueOf(val - 1));
        }
      } catch (NumberFormatException ex) {
        theQtyField.setText("1");
      }
    });
    addToBasket.addActionListener(e -> {
      int quantity;
      try {
        quantity = Integer.parseInt(theQtyField.getText());
      } catch (NumberFormatException ex) {
        quantity = 1;
      }
      cont.addToBasket(quantity);
    });
    theViewBasketBtn.addActionListener(e -> {
      cont.submitBasket();
      openCustomerBasketView(rpc, mf, x, y);
    });

    // If user selects a row in the table, show that product’s image
    productTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
      if (!e.getValueIsAdjusting()) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1 && lastSearchResults != null
                && selectedRow < lastSearchResults.size()) {
          Product p = lastSearchResults.get(selectedRow);
          if (p.getImg() != null && !p.getImg().isEmpty()) {
            thePicture.set(new ImageIcon(p.getImg()));
          } else {
            thePicture.clear();
          }
          cont.setSelectedProduct(
                  p.getProductNum(),
                  p.getDescription(),
                  p.getPrice(),
                  p.getQuantity(),
                  p.getImg()
          );
        }
      }
    });

    rootWindow.setVisible(true);
    cp.repaint();
  }

  /**
   * Show the user's basket in a new panel (still within the same JFrame).
   * Now includes a second image box for the selected basket item.
   */
  public void openCustomerBasketView(RootPaneContainer rpc, MiddleFactory mf, int x, int y)
  {
    UtilClass.setTurquoiseBackground(rpc);
    Container cp         = rpc.getContentPane();
    Container rootWindow = (Container) rpc;
    cp.removeAll();
    cp.setLayout(null);
    rootWindow.setSize(W, H);
    rootWindow.setLocation(x, y);

    Font f = new Font("Monospaced", Font.PLAIN, 12);

    JLabel pageTitle = new JLabel("Your Basket");
    pageTitle.setBounds(20, 0, 270, 20);
    cp.add(pageTitle);

    // Table for basket
    String[] basketCols = { "Product No", "Description", "Unit Price", "Quantity", "Subtotal" };
    DefaultTableModel basketModel = new DefaultTableModel(basketCols, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    JTable basketTable = new JTable(basketModel);
    JScrollPane basketSP = new JScrollPane(basketTable);
    basketSP.setBounds(20, 50, 500, 300);
    cp.add(basketSP);

    // A second image area for basket items
    Picture basketPicture = new Picture(80, 80);
    basketPicture.setBounds(540, 50, 80, 80);
    basketPicture.clear();
    cp.add(basketPicture);

    // Fill table from the model's basket
    Map<catalogue.Product, Integer> basketData = cont.getBasket().returnProductPurchaseInfo();
    double totalPrice = 0.0;
    java.util.List<Product> basketProducts = new java.util.ArrayList<>(basketData.keySet());

    for (Product p : basketProducts) {
      int qty = basketData.get(p);
      double subtotal = p.getPrice() * qty;
      totalPrice += subtotal;
      basketModel.addRow(new Object[] {
              p.getProductNum(),
              p.getDescription(),
              p.getPrice(),
              qty,
              subtotal
      });
    }

    // Listen for selection in the basket table to show item image
    basketTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        int selRow = basketTable.getSelectedRow();
        if (selRow != -1 && selRow < basketProducts.size()) {
          Product selectedP = basketProducts.get(selRow);
          if (selectedP.getImg() != null && !selectedP.getImg().isEmpty()) {
            basketPicture.set(new ImageIcon(selectedP.getImg()));
          } else {
            basketPicture.clear();
          }
        }
      }
    });

    // Show total
    JLabel totalLabel = new JLabel("Total: " + totalPrice);
    totalLabel.setBounds(20, 360, 200, 30);
    cp.add(totalLabel);

    // Drop item button
    JButton dropItemButton = UtilClass.createRoundedButton("Drop Selected Item");
    dropItemButton.setBounds(20, 400, 160, 30);
    dropItemButton.setFont(f);
    cp.add(dropItemButton);

    dropItemButton.addActionListener(evt -> {
      int selectedRow = basketTable.getSelectedRow();
      if (selectedRow != -1) {
        Product toRemove = basketProducts.get(selectedRow);
        cont.dropBskItem(toRemove.getProductNum());
        basketModel.removeRow(selectedRow);
        basketProducts.remove(selectedRow);
        basketPicture.clear();
      }
    });

    // A Purchase button
    theSubmitBtn.setBounds(200, 400, 160, 30);
    theSubmitBtn.setFont(f);
    cp.add(theSubmitBtn);
    theSubmitBtn.addActionListener(evt -> {
      cont.purchaseBasket();
      basketModel.setRowCount(basketProducts.size());  // Just to visually clear rows
      basketPicture.clear();
    });

    // A "Return" button to go back to the main screen
    JButton backBtn = UtilClass.createRoundedButton("Return");
    backBtn.setBounds(400, 400, 120, 30);
    backBtn.setFont(f);
    cp.add(backBtn);
    backBtn.addActionListener(evt -> {
      // Rebuild the main purchase screen
      buildMainScreen(rpc, mf, x, y);
    });

    rootWindow.setVisible(true);
    cp.repaint();
  }

  // Let the controller be assigned
  public void setController(CustomerController c) {
    this.cont = c;
  }

  @Override
  public void update(Observable modelC, Object arg)
  {
    CustomerModel model = (CustomerModel) modelC;
    String msg = (String) arg;
    theAction.setText(msg);
    theAction.setForeground(Color.BLACK);

    // If the model has a new "main" image to show, do so
    ImageIcon icon = model.getPicture();
    if (icon != null) {
      thePicture.set(icon);
    } else {
      thePicture.clear();
    }
  }

  /**
   * Called by the controller to fill the table with search results.
   * Also store a local list of Product objects so we can easily reference them
   * for selection.
   */
  public void populateProductTable(List<Product> products) {
    productTableModel.setRowCount(0);
    this.lastSearchResults = products;
    for (Product p : products) {
      productTableModel.addRow(new Object[] {
              p.getProductNum(),
              p.getDescription(),
              p.getPrice(),
              p.getQuantity()
      });
    }
  }
}
