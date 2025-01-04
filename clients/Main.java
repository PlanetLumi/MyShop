package clients;

import middle.LocalMiddleFactory;
import middle.MiddleFactory;
import clients.backDoor.BackDoorController;
import clients.backDoor.BackDoorModel;
import clients.backDoor.BackDoorView;
import clients.cashier.CashierController;
import clients.cashier.CashierModel;
import clients.cashier.CashierView;
import clients.customer.CustomerController;
import clients.customer.CustomerModel;
import clients.customer.CustomerView;
import clients.packing.PackingController;
import clients.packing.PackingModel;
import clients.packing.PackingView;

import javax.swing.*;
import java.awt.*;

/**
 * Main Menu Client to pick and run a specific client (Customer, Cashier, Packing, BackDoor).
 */
public class Main {

    private final MiddleFactory mlf; // Factory object for shared logic

    /**
     * Constructor - Initializes the MiddleFactory
     */
    public Main() {
        this.mlf = new LocalMiddleFactory(); // Shared factory instance
    }

    /**
     * Entry point to display the menu.
     */
    public static void main(String[] args) {
        new Main().showMenu();
    }

    /**
     * Display the main GUI menu to choose a client.
     */
    public void showMenu() {
        // Create menu frame
        JFrame menuFrame = new JFrame("Client Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400, 300);
        menuFrame.setLayout(new BorderLayout());
        menuFrame.setLocationRelativeTo(null); // Center the frame on the screen

        // Create header
        JLabel headerLabel = new JLabel("Choose a Client to Start", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10)); // 5 rows (4 clients + 1 exit button)
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add buttons for each client
        JButton customerButton = new JButton("Start Customer Client");
        JButton cashierButton = new JButton("Start Cashier Client");
        JButton packingButton = new JButton("Start Packing Client");
        JButton backDoorButton = new JButton("Start BackDoor Client");
        JButton exitButton = new JButton("Exit Menu");

        // Add action listeners for each button
        customerButton.addActionListener(_ -> startCustomerClient());
        cashierButton.addActionListener(_ -> startCashierClient());
        packingButton.addActionListener(_ -> startPackingClient());
        backDoorButton.addActionListener(_ -> startBackDoorClient());
        exitButton.addActionListener(_ -> exitApplication(menuFrame));

        // Add buttons to panel
        buttonPanel.add(customerButton);
        buttonPanel.add(cashierButton);
        buttonPanel.add(packingButton);
        buttonPanel.add(backDoorButton);
        buttonPanel.add(exitButton); // Exit button at the bottom

        // Add components to frame
        menuFrame.add(headerLabel, BorderLayout.NORTH);
        menuFrame.add(buttonPanel, BorderLayout.CENTER);

        // Show the frame
        menuFrame.setVisible(true);
    }

    /**
     * Start the Customer Client.
     */
    private void startCustomerClient() {
        JFrame window = new JFrame();
        window.setTitle("Customer Client MVC");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        Dimension pos = PosOnScrn.getPos();

        CustomerModel model = new CustomerModel(mlf);
        CustomerView view = new CustomerView(window, mlf, pos.width, pos.height);
        CustomerController controller = new CustomerController(model, view);
        view.setController(controller);

        model.addObserver(view);
        window.setVisible(true);
    }

    /**
     * Start the Cashier Client.
     */
    private void startCashierClient() {
        JFrame window = new JFrame();
        window.setTitle("Cashier Client MVC");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension pos = PosOnScrn.getPos();

        CashierModel model = new CashierModel(mlf);
        CashierView view = new CashierView(window, mlf, pos.width, pos.height);
        CashierController controller = new CashierController(model, view);
        view.setController(controller);

        model.addObserver(view);
        window.setVisible(true);
        model.askForUpdate();
    }

    /**
     * Start the Packing Client.
     */
    private void startPackingClient() {
        JFrame window = new JFrame();
        window.setTitle("Packing Client MVC");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension pos = PosOnScrn.getPos();

        PackingModel model = new PackingModel(mlf);
        PackingView view = new PackingView(window, mlf, pos.width, pos.height);
        PackingController controller = new PackingController(model, view);
        view.setController(controller);

        model.addObserver(view);
        window.setVisible(true);
    }

    /**
     * Start the BackDoor Client.
     */
    private void startBackDoorClient() {
        JFrame window = new JFrame();
        window.setTitle("BackDoor Client MVC");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension pos = PosOnScrn.getPos();

        BackDoorModel model = new BackDoorModel(mlf);
        BackDoorView view = new BackDoorView(window, mlf, pos.width, pos.height);
        BackDoorController controller = new BackDoorController(model, view);
        view.setController(controller);

        model.addObserver(view);
        window.setVisible(true);
    }

    /**
     * Exit the application when the "Exit" button is clicked.
     *
     * @param menuFrame The main menu frame to close.
     */
    private void exitApplication(JFrame menuFrame) {
        int confirm = JOptionPane.showConfirmDialog(
                menuFrame,
                "Are you sure you want to exit the application?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Terminate the program
        }
    }
}