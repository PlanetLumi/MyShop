package clients.packing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * A single-window Packing View that:
 * 1) Lists orders from OrderHistory (unpacked).
 * 2) Lets user select & confirm an order.
 * 3) Switches to a "packing step" screen in the same window via CardLayout.
 */
public class PackingView extends JFrame implements Observer
{
  // ---------------------------------------------------------------------
  // 1) Some constants to identify the cards
  // ---------------------------------------------------------------------
  private static final String CARD_ORDER_SELECTION = "ORDER_SELECTION";
  private static final String CARD_PACKING_STEP    = "PACKING_STEP";

  private PackingController controller;

  // ---------------------------------------------------------------------
  // Fields for the "Order Selection" portion
  // ---------------------------------------------------------------------
  private JTable orderTable;
  private DefaultTableModel orderTableModel;
  private JButton confirmBtn;

  // ---------------------------------------------------------------------
  // Fields for the "Packing Step" portion
  // ---------------------------------------------------------------------
  private Long   selectedOrderId;
  private JTable itemsTable;
  private DefaultTableModel itemsModel;
  private JButton packBtn;
  private JButton setDeliveryBtn;

  // This JPanel holds both “screens” in a CardLayout
  private JPanel cards;

  // ---------------------------------------------------------------------
  // Constructor
  // ---------------------------------------------------------------------
  public PackingView(PackingController ctrl) throws SQLException {
    super("Packing Client MVC");
    this.controller = ctrl;

    // (A) Create the CardLayout container
    cards = new JPanel(new CardLayout());

    // (B) Build each screen as a separate JPanel
    JPanel orderSelectionPanel = buildOrderSelectionPanel();
    JPanel packingStepPanel    = buildPackingStepPanel();

    // (C) Add them to 'cards' with distinct names
    cards.add(orderSelectionPanel, CARD_ORDER_SELECTION);
    cards.add(packingStepPanel,    CARD_PACKING_STEP);

    // (D) Put 'cards' in the frame’s content pane
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(cards, BorderLayout.CENTER);

    // Basic window setup
    setSize(600, 400);
    setLocation(300, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Show the "Order Selection" screen by default
    showCard(CARD_ORDER_SELECTION);

    setVisible(true);
  }

  public void setController(PackingController c) {
    this.controller = c;
  }


  // 2) A helper method to switch which card is visible
  private void showCard(String cardName) {
    CardLayout cl = (CardLayout) (cards.getLayout());
    cl.show(cards, cardName);
  }

  private JPanel buildOrderSelectionPanel() throws SQLException {
    // Using absolute layout as in your original code
    JPanel panel = new JPanel(null);
    panel.setSize(600, 400);

    JLabel title = new JLabel("Unpacked Orders:");
    title.setBounds(20, 10, 200, 30);
    panel.add(title);

    // Table + Model
    String[] cols = {"OrderID", "AccountID", "ProductNo", "Date"};
    orderTableModel = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int col) { return false; }
    };
    orderTable = new JTable(orderTableModel);
    JScrollPane sp = new JScrollPane(orderTable);
    sp.setBounds(20, 50, 550, 250);
    panel.add(sp);

    // Confirm Button
    confirmBtn = new JButton("Confirm");
    confirmBtn.setBounds(20, 320, 100, 30);
    confirmBtn.addActionListener(e -> doConfirmOrder());
    panel.add(confirmBtn);

    // Load data from DB
    loadUnpackedOrders();

    return panel;
  }

  // Helper to load the table data
  private void loadUnpackedOrders() throws SQLException {
    List<Map<String, Object>> unpicked = controller.fetchUnpackedOrders();

    orderTableModel.setRowCount(0);
    for (Map<String, Object> row : unpicked) {
      orderTableModel.addRow(new Object[] {
              row.get("orderHistoryId"),
              row.get("account_id"),
              row.get("productNo"),
              row.get("purchase_date")
      });
    }
  }

  // When user clicks "Confirm" on an order
  private void doConfirmOrder() {
    int sel = orderTable.getSelectedRow();
    if (sel < 0) {
      JOptionPane.showMessageDialog(this, "Please select an order first!");
      return;
    }
    selectedOrderId = (Long) orderTableModel.getValueAt(sel, 0);

    // The controller can do any logic needed
    controller.confirmOrderForPacking(selectedOrderId);

    // Also load the item details for that order
    loadOrderDetails(selectedOrderId);
    showCard(CARD_PACKING_STEP);
  }

  private JPanel buildPackingStepPanel() {
    JPanel panel = new JPanel(null);
    panel.setSize(600, 400);

    // Title
    JLabel lbl = new JLabel("Packing Step");
    lbl.setBounds(20, 10, 400, 30);
    panel.add(lbl);

    // Table for items
    String[] cols = {"ProductNo", "Quantity", "Status"};
    itemsModel = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int col) { return false; }
    };
    itemsTable = new JTable(itemsModel);
    JScrollPane sp = new JScrollPane(itemsTable);
    sp.setBounds(20, 50, 550, 200);
    panel.add(sp);

    // Pack button
    packBtn = new JButton("Pack Items");
    packBtn.setBounds(20, 300, 120, 30);
    packBtn.addActionListener(e -> doPackItems());
    panel.add(packBtn);

    // SetDelivery button
    setDeliveryBtn = new JButton("Set for Delivery");
    setDeliveryBtn.setBounds(160, 300, 150, 30);
    setDeliveryBtn.addActionListener(e -> {
      try {
        controller.doSetForDelivery(selectedOrderId);
      } catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
    });
    panel.add(setDeliveryBtn);

    return panel;
  }

  private void loadOrderDetails(Long orderHistId) {
    // Clear old data
    itemsModel.setRowCount(0);

    // For each line, add e.g. [productNo, quantity, "Unpacked"]
    itemsModel.addRow(new Object[] {"0002", "1", "Unpacked"} );
  }

  // Called when user clicks "Pack Items"
  private void doPackItems() {
    // Update DB or model that items are "packed"
    for (int r = 0; r < itemsModel.getRowCount(); r++) {
      itemsModel.setValueAt("Packed", r, 2);
    }
    JOptionPane.showMessageDialog(this, "Items packed!");
  }

  // If want to go back to the main list
  public void goBackToOrderSelection() {
    showCard(CARD_ORDER_SELECTION);
  }

  //
  // 5) Observer implementation to respond to model changes
  public void update(Observable o, Object arg) {
    System.out.println("Model changed: " + arg);
  }
}
