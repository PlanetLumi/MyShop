package clients.admin;

import clients.Picture;
import middle.MiddleFactory;
import middle.StockReader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * The AdminView class implements the GUI for the Admin panel,
 * including sub-panels for managing employee promotions and
 * security-related tasks.
 */
public class AdminView implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        // Currently not used; can be implemented if needed.
    }

    // Class storing names for buttons, for consistency
    static class Name {
        static final String CHECK = "Check";
        static final String CLEAR = "Clear";
        static final String BACK  = "Back";
    }

    // Window size constants
    private static final int H = 600;
    private static final int W = 800;

    // Common UI components
    private static final JLabel pageTitle      = new JLabel();
    private static final JLabel options        = new JLabel();
    private static final JTextField workerSearch = new JTextField();
    private static final JTextField userSearch   = new JTextField();
    private static final JButton selectEmployee  = new JButton(Name.CHECK);
    private static final JButton lockUser        = new JButton(Name.CHECK);
    private static final JButton unlockUser      = new JButton(Name.CHECK);
    private static final JButton theBtOpenPanel  = new JButton();
    private static final JButton theBtOpenSecurityPanel = new JButton();

    // “Go Back” button used in sub-panels
    private static final JButton theBtGoBack = new JButton(Name.BACK);

    // Additional UI references
    private final JTextArea theOutput = new JTextArea();
    private final JScrollPane theSP    = new JScrollPane(theOutput);

    // Optional for demonstration
    private final StockReader theStock = null;
    private final Picture thePicture   = new Picture(80, 80);

    private AdminController cont = null;

    /**
     * Constructs the AdminView with basic panel options and layout.
     *
     * @param rpc The root pane container (e.g., JFrame)
     * @param mf  The middle factory for connectivity
     * @param x   X-coordinate for the window
     * @param y   Y-coordinate for the window
     */
    public AdminView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
        Container cp = rpc.getContentPane();
        Container rootWindow = (Container) rpc;
        cp.setLayout(new BorderLayout(10, 10));

        // Set the window size and location
        rootWindow.setSize(W, H);
        rootWindow.setLocation(x, y);

        // Top panel: Title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pageTitle.setText("Manager Options");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(pageTitle);

        // Center panel: Buttons to open sub-panels
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 10, 10));

        theBtOpenPanel.setText("Manage Accounts");
        theBtOpenPanel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        theBtOpenPanel.addActionListener(e -> {
            if (cont != null) {
                try {
                    cont.openManagerEmployeePanel(x + 10, y + 10);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(rootWindow,
                            "Error opening Manager Employee Panel: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        centerPanel.add(theBtOpenPanel);

        theBtOpenSecurityPanel.setText("Manage Security");
        theBtOpenSecurityPanel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        theBtOpenSecurityPanel.addActionListener(e -> {
            if (cont != null) {
                cont.openSecurityManagerPanel(x + 10, y + 10);
            }
        });
        centerPanel.add(theBtOpenSecurityPanel);

        // Add top and center panels to the main container
        cp.add(topPanel, BorderLayout.NORTH);
        cp.add(centerPanel, BorderLayout.CENTER);

        // Add some padding around edges
        ((JComponent) cp).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Creates the panel used to promote employees to a manager role.
     *
     * @param window The frame in which the panel is rendered
     * @param mf     The MiddleFactory for connectivity
     * @param x      X-coordinate for the panel
     * @param y      Y-coordinate for the panel
     */
    public void AdminCreatePanel(JFrame window, MiddleFactory mf, int x, int y) {
        window.getContentPane().removeAll(); // Clear any previous layout
        window.setTitle("Manager Employee Panel");

        // Use a BorderLayout for overall structure
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        window.setContentPane(contentPanel);

        // Top panel with page title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pageTitle.setText("Manage Accounts");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(pageTitle);

        // Sub-panel for searching employees
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        workerSearch.setColumns(20);
        workerSearch.setText("Search Employees");
        searchPanel.add(workerSearch);

        // Sub-panel for the list + promotion button
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // List model and UI for showing employees
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        resultList.setVisibleRowCount(8);
        JScrollPane scrollPane = new JScrollPane(resultList);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Options/Info label
        options.setText("Promote Employee to Manager");
        options.setFont(new Font("SansSerif", Font.ITALIC, 14));
        centerPanel.add(options, BorderLayout.NORTH);

        // Button panel at the bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        selectEmployee.setText("Designate as Manager");
        selectEmployee.addActionListener(e -> {
            if (cont != null) {
                try {
                    cont.promoteCurrentEmployee(resultList.getSelectedValue(), window);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(window,
                            "Error promoting employee: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        bottomPanel.add(selectEmployee);

        // “Go Back” button
        theBtGoBack.addActionListener(e -> window.dispose());
        bottomPanel.add(theBtGoBack);

        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Listen for text changes in workerSearch
        workerSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void doUpdate() {
                if (cont != null) {
                    try {
                        String search = workerSearch.getText();
                        List<String> employees = cont.getEmployees(search);
                        listModel.clear();
                        for (String emp : employees) {
                            listModel.addElement(emp);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(window,
                                "Error retrieving employees: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) { doUpdate(); }
            @Override
            public void removeUpdate(DocumentEvent e) { doUpdate(); }
            @Override
            public void changedUpdate(DocumentEvent e) { doUpdate(); }
        });

        // Assemble everything
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(searchPanel, BorderLayout.WEST);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Adjust window size and location
        window.setSize(W + 200, H + 200);
        window.setLocation(x, y);

        // Add some padding around edges
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        window.setVisible(true);
    }

    /**
     * Creates the panel used to manage security, such as locking/unlocking
     * user accounts and sending messages.
     *
     * @param window The frame in which the panel is rendered
     * @param mf     The MiddleFactory for connectivity
     * @param x      X-coordinate for the panel
     * @param y      Y-coordinate for the panel
     */
    public void AdminSecurityPanel(JFrame window, MiddleFactory mf, int x, int y) {
        window.getContentPane().removeAll();
        window.setTitle("Manager Security Panel");

        // Use a BorderLayout for overall structure
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        window.setContentPane(contentPanel);

        // Top panel with page title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pageTitle.setText("Manage Security");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(pageTitle);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        userSearch.setColumns(20);
        userSearch.setText("Search All Users");
        searchPanel.add(userSearch);

        // Checkbox to filter locked-only accounts
        JCheckBox filterLocked = new JCheckBox("Show Locked Accounts Only");
        searchPanel.add(filterLocked);

        // Center panel containing the user list
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        resultList.setVisibleRowCount(8);
        JScrollPane scrollPane = new JScrollPane(resultList);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel for lock/unlock buttons
        JPanel lockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        lockUser.setText("Lock Account");
        lockUser.addActionListener(e -> {
            if (cont != null) {
                String selectedUser = resultList.getSelectedValue();
                if (selectedUser != null) {
                    try {
                        cont.lockUser(selectedUser, true);
                        JOptionPane.showMessageDialog(window,
                                "User " + selectedUser + " locked successfully.");
                        updateList(listModel, filterLocked.isSelected());
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(window,
                                "Error locking user: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(window,
                            "Please select a user to lock.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        lockPanel.add(lockUser);

        unlockUser.setText("Unlock Account");
        unlockUser.addActionListener(e -> {
            if (cont != null) {
                String selectedUser = resultList.getSelectedValue();
                if (selectedUser != null) {
                    try {
                        cont.lockUser(selectedUser, false);
                        JOptionPane.showMessageDialog(window,
                                "User " + selectedUser + " unlocked successfully.");
                        updateList(listModel, filterLocked.isSelected());
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(window,
                                "Error unlocking user: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(window,
                            "Please select a user to unlock.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        lockPanel.add(unlockUser);

        centerPanel.add(lockPanel, BorderLayout.NORTH);

        // Panel for sending a message
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JTextField sendMessageField = new JTextField("Send Message To User", 20);
        messagePanel.add(sendMessageField);

        JButton sendMessageButton = new JButton("Send Message");
        sendMessageButton.addActionListener(e -> {
            if (cont != null) {
                String selectedUser = resultList.getSelectedValue();
                if (selectedUser != null) {
                    try {
                        cont.sendUserMessage(selectedUser, sendMessageField.getText());
                        JOptionPane.showMessageDialog(window, "Message sent successfully.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(window,
                                "Error sending message: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(window,
                            "Please select a user to send a message.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        messagePanel.add(sendMessageButton);

        centerPanel.add(messagePanel, BorderLayout.SOUTH);

        // Bottom panel for "Go Back" button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        theBtGoBack.addActionListener(e -> window.dispose());
        bottomPanel.add(theBtGoBack);

        // Listen for text changes in userSearch & filterLocked
        userSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { doUpdateList(); }
            @Override
            public void removeUpdate(DocumentEvent e) { doUpdateList(); }
            @Override
            public void changedUpdate(DocumentEvent e) { doUpdateList(); }

            private void doUpdateList() {
                if (cont != null) {
                    try {
                        updateList(listModel, filterLocked.isSelected());
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(window,
                                "Error retrieving users: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        filterLocked.addActionListener(e -> {
            if (cont != null) {
                try {
                    updateList(listModel, filterLocked.isSelected());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(window,
                            "Error retrieving users: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Assemble the final layout
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(searchPanel, BorderLayout.WEST);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Adjust window size and location
        window.setSize(W + 200, H + 200);
        window.setLocation(x, y);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        window.setVisible(true);
    }

    /**
     * Helper method to update the list of users in the Security Panel
     * based on the current search text and locked/unlocked filter.
     *
     * @param listModel       The list model to populate
     * @param showLockedOnly  Whether to filter only locked accounts
     * @throws SQLException   If database operations fail
     */
    private void updateList(DefaultListModel<String> listModel, boolean showLockedOnly) throws SQLException {
        String search = userSearch.getText();
        List<String> users = cont.getUsers(search, showLockedOnly);
        listModel.clear();
        users.forEach(listModel::addElement);
    }

    /**
     * Sets the controller object for the AdminView.
     *
     * @param controller AdminController instance
     */
    public void setController(AdminController controller) {
        this.cont = controller;
    }
}
