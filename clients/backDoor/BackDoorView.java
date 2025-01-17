package clients.backDoor;

import clients.UtilClass;
import middle.MiddleFactory;
import middle.StockReadWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.List;
import java.util.Map;

/**
 * Enhanced BackDoor view that can
 * 1) Query stock by partial name/productNo,
 * 2) Show search results in a table,
 * 3) Let user pick a product and edit fields,
 * 4) Update DB with new info (stock level, price, etc.).
 */
public class BackDoorView implements Observer
{
  private static final String SEARCH   = "Search";
  private static final String UPDATE   = "Update";
  private static final String CLEAR    = "Clear";
  private static final String ADDSTOCK = "AddStock";

  private static final int H = 500; // bigger height
  private static final int W = 550; // bigger width

  private final JLabel pageTitle  = new JLabel("Staff: Manage Stock");
  private final JLabel theAction  = new JLabel();

  // -- Searching Section
  private final JLabel searchLabel = new JLabel("Search:");
  private final JTextField searchField = new JTextField();
  private final JButton searchBtn = UtilClass.createRoundedButton(SEARCH);

  // -- Table for results
  private JTable productTable;
  private DefaultTableModel productTableModel;
  private JScrollPane productTableSP;

  // -- Edit fields
  private final JLabel lblProdNo = new JLabel("ProductNo:");
  private final JTextField txtProdNo = new JTextField();
  private final JLabel lblDesc  = new JLabel("Description:");
  private final JTextField txtDesc = new JTextField();
  private final JLabel lblPrice = new JLabel("Price:");
  private final JTextField txtPrice = new JTextField();
  private final JLabel lblStock = new JLabel("Stock:");
  private final JTextField txtStock = new JTextField();

  private final JButton updateBtn = UtilClass.createRoundedButton(UPDATE);
  private final JButton addStockBtn = UtilClass.createRoundedButton(ADDSTOCK);
  private final JButton clearBtn = UtilClass.createRoundedButton(CLEAR);

  private BackDoorController cont;

  public BackDoorView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) throws SQLException {
    UtilClass.setTurquoiseBackground(rpc);
    Container cp = rpc.getContentPane();
    Container rootWindow = (Container) rpc;
    cp.setLayout(null);
    rootWindow.setSize(W, H);
    rootWindow.setLocation(x, y);

    // Title
    pageTitle.setBounds(180, 0, 250, 30);
    pageTitle.setForeground(Color.BLACK);
    cp.add(pageTitle);

    // Action label
    theAction.setBounds(20, 35, 400, 20);
    theAction.setForeground(Color.BLACK);
    cp.add(theAction);

    // Search label + field + button
    searchLabel.setBounds(20, 60, 60, 25);
    searchLabel.setForeground(Color.BLACK);
    cp.add(searchLabel);
    searchField.setBounds(80, 60, 200, 25);
    searchField.setForeground(Color.BLACK);
    cp.add(searchField);
    searchBtn.setBounds(290, 60, 80, 25);
    cp.add(searchBtn);

    // Table for search results
    String[] colNames = { "ProductNo", "Description", "Price", "StockLevel" };
    productTableModel = new DefaultTableModel(colNames, 0) {
      @Override
      public boolean isCellEditable(int row, int col) { return false; }
    };
    productTable = new JTable(productTableModel);
    productTableSP = new JScrollPane(productTable);
    productTableSP.setBounds(20, 100, 500, 150);
    cp.add(productTableSP);

    // "Edit product" fields
    lblProdNo.setBounds(20, 270, 80, 25);
    cp.add(lblProdNo);
    txtProdNo.setBounds(100, 270, 80, 25);
    cp.add(txtProdNo);

    lblDesc.setBounds(200, 270, 80, 25);
    cp.add(lblDesc);
    txtDesc.setBounds(280, 270, 150, 25);
    cp.add(txtDesc);

    lblPrice.setBounds(20, 310, 80, 25);
    cp.add(lblPrice);
    txtPrice.setBounds(100, 310, 80, 25);
    cp.add(txtPrice);

    lblStock.setBounds(200, 310, 80, 25);
    cp.add(lblStock);
    txtStock.setBounds(280, 310, 80, 25);
    cp.add(txtStock);

    // Buttons
    updateBtn.setBounds(20, 350, 90, 30);
    cp.add(updateBtn);

    addStockBtn.setBounds(120, 350, 90, 30);
    cp.add(addStockBtn);

    clearBtn.setBounds(220, 350, 90, 30);
    cp.add(clearBtn);

    // Listeners
    searchBtn.addActionListener(e -> {
        try {
            cont.doSearch(searchField.getText());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    });
    updateBtn.addActionListener(e -> {doUpdate();
    try {
      cont.doSearch(searchField.getText());
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  } );
    addStockBtn.addActionListener(e -> {doAddStock();
        try {
            cont.doSearch(searchField.getText());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    } );
    clearBtn.addActionListener(e -> productTableModel.setRowCount(0));

    // When row is selected, populate edit fields
    productTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
          txtProdNo.setText( productTableModel.getValueAt(row, 0).toString() );
          txtDesc.setText(   productTableModel.getValueAt(row, 1).toString() );
          txtPrice.setText(  productTableModel.getValueAt(row, 2).toString() );
          txtStock.setText(  productTableModel.getValueAt(row, 3).toString() );
        }
      }
    });

    rootWindow.setVisible(true);
  }

  public void setController(BackDoorController c) {
    this.cont = c;
  }

  /**
   * Called by the controller/model to refresh the table with new data
   * or to show messages.
   */
  @Override
  public void update(Observable obs, Object arg) {
    // If the model sends a message string:
    if (arg instanceof String) {
      theAction.setText((String) arg);
    }
  }

  public void populateTable(List<Object[]> products) {
    productTableModel.setRowCount(0);
    for (Object[] row : products) {
      productTableModel.addRow(row);
    }
  }


  // "Update" wants to push new data to DB
  private void doUpdate() {
    String pNo = txtProdNo.getText().trim();
    String desc = txtDesc.getText().trim();
    String priceStr = txtPrice.getText().trim();
    String stockStr = txtStock.getText().trim();

    try {
      double newPrice = Double.parseDouble(priceStr);
      int newStock = Integer.parseInt(stockStr);
      // Pass to controller
      cont.doUpdate(pNo, desc, newPrice, newStock);
    } catch (NumberFormatException ex) {
      theAction.setText("Invalid number entered for price or stock.");
    }
  }

  // "AddStock"  just adds a certain amount to the stock (instead of overwriting).
  private void doAddStock() {
    String pNo = txtProdNo.getText().trim();
    String stockStr = txtStock.getText().trim();
    try {
      int addQty = Integer.parseInt(stockStr);
      cont.doAddStock(pNo, addQty);
    } catch (NumberFormatException ex) {
      theAction.setText("Invalid number for stock to add.");
    }
  }

}