package clients.admin;

import clients.Picture;
import clients.accounts.AccountCreation;
import clients.accounts.Session;
import clients.accounts.SessionManager;
import login.LoginController;
import middle.MiddleFactory;
import middle.StockReader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;


/**
 * Implements the Customer view.
 */

public class AdminView implements Observer {
    @Override
    public void update(Observable o, Object arg) {

    }

    class Name                              // Names of buttons
    {
        public static final String CHECK = "Check";
        public static final String CLEAR = "Clear";
    }

    private static final int H = 600;       // Height of window pixels
    private static final int W = 800;       // Width  of window pixels

    private static final JLabel pageTitle = new JLabel();
    private static final JLabel options = new JLabel();
    private static final JTextField workerSearch = new JTextField();
    private static final JTextField userSearch = new JTextField();
    private static final JButton selectEmployee = new JButton(Name.CHECK);
    private static final JButton lockUser = new JButton(Name.CHECK);
    private static final JButton unlockUser = new JButton(Name.CHECK);
    private static final JLabel theAction = new JLabel();
    private static final JTextField usernameInput = new JTextField();
    private static JPasswordField passwordInput = new JPasswordField();
    private final JTextArea theOutput = new JTextArea();
    private final JScrollPane theSP = new JScrollPane();
    private static final JButton theBtCheck = new JButton(Name.CHECK);
    private static final JButton theBtClear = new JButton(Name.CLEAR);
    private static final JButton theBtOpenPanel = new JButton();
    private static final JButton theBtOpenSecurityPanel = new JButton();
    private Picture thePicture = new Picture(80, 80);
    private StockReader theStock = null;
    private AdminController cont = null;

    /**
     * Construct the view
     *
     * @param rpc Window in which to construct
     * @param mf  Factor to deliver order and stock objects
     * @param x   x-cordinate of position of window on screen
     * @param y   y-cordinate of position of window on screen
     */

    public AdminView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
        Container cp = rpc.getContentPane();    // Content Pane
        Container rootWindow = (Container) rpc;         // Root Window
        cp.setLayout(null);                             // No layout manager
        rootWindow.setSize(W, H);                     // Size of Window
        rootWindow.setLocation(x, y);

        Font f = new Font("Monospaced", Font.PLAIN, 12);  // Font f is

        pageTitle.setBounds(110, 0, 270, 20);
        pageTitle.setText("Manager Options");
        cp.add(pageTitle);
        theBtOpenPanel.setBounds(110, 120, 270, 40);
        theBtOpenPanel.setText("Manage Accounts");
        theBtOpenPanel.addActionListener(e -> {
            try {
                cont.openManagerEmployeePanel(x + 10, y + 10);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        cp.add(theBtOpenPanel);
        theBtOpenSecurityPanel.setBounds(110, 200, 270, 40);
        theBtOpenSecurityPanel.setText("Manage Security");
        theBtOpenSecurityPanel.addActionListener(e -> {
                cont.openSecurityManagerPanel(x + 10, y + 10);
        });
        cp.add(theBtOpenSecurityPanel);
    }

    public void AdminCreatePanel(JFrame window, MiddleFactory mf, int x, int y) {
        workerSearch.setBounds(110, 200, 270, 30);
        workerSearch.setText("Search Employees");
        Container cp = window.getContentPane();
        cp.setLayout(null);
        cp.add(workerSearch);
        // Set the size and position of the frame
        window.setSize(W+200, H+200);
        window.setLocation(x, y);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBounds(50, 80, 300, 150);
        window.add(scrollPane);

        workerSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    updateList();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    updateList();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    updateList();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            private void updateList() throws SQLException {
                String search = userSearch.getText();
                List<String> user = getEmployees(search);
                listModel.clear();
                user.forEach(listModel::addElement);
            }
        });


        Font f = new Font("Monospaced", Font.PLAIN, 12);
        pageTitle.setBounds(110, 0, 270, 20);
        pageTitle.setText("Manage Accounts");
        cp.add(pageTitle);
        selectEmployee.setBounds(30, 200, 270, 40);
        selectEmployee.setText("Designate New Manager");
        selectEmployee.addActionListener(e -> {
            try {
                cont.promoteCurrentEmployee(resultList.getSelectedValue(), window);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        cp.add(selectEmployee);
        options.setBounds(110, 80, 270, 20);
        options.setText("Promote Account");
        cp.add(options);
    }

    public void AdminSecurityPanel(JFrame window, MiddleFactory mf, int x, int y) {
        userSearch.setBounds(110, 50, 500, 30);
        userSearch.setText("Search All Users");
        Container cp = window.getContentPane();
        cp.setLayout(null);
        cp.add(userSearch);

        // Filter for locked accounts
        JCheckBox filterLocked = new JCheckBox("Show Locked Accounts Only");
        filterLocked.setBounds(110, 90, 300, 30);
        cp.add(filterLocked);

        // Set the size and position of the frame
        window.setSize(W + 200, H + 200);
        window.setLocation(x, y);

        // List to display users
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBounds(50, 130, 500, 150);
        cp.add(scrollPane);

        userSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    updateList(listModel, filterLocked.isSelected());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    updateList(listModel, filterLocked.isSelected());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    updateList(listModel, filterLocked.isSelected());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        Font f = new Font("Monospaced", Font.PLAIN, 12);
        pageTitle.setBounds(110, 0, 270, 20);
        pageTitle.setText("Manage Security");
        cp.add(pageTitle);

        // Lock User Button
        lockUser.setBounds(30, 300, 270, 40);
        lockUser.setText("Lock Account");
        lockUser.addActionListener(e -> {
            String selectedUser = resultList.getSelectedValue();
            if (selectedUser != null) {
                try {
                    cont.lockUser(selectedUser, true);
                    JOptionPane.showMessageDialog(window, "User " + selectedUser + " locked successfully.");
                    updateList(listModel, filterLocked.isSelected());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(window, "Error locking user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(window, "Please select a user to lock.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        cp.add(lockUser);

        // Unlock User Button
        unlockUser.setBounds(30, 350, 270, 40);
        unlockUser.setText("Unlock Account");
        unlockUser.addActionListener(e -> {
            String selectedUser = resultList.getSelectedValue();
            if (selectedUser != null) {
                try {
                    cont.lockUser(selectedUser, false);
                    JOptionPane.showMessageDialog(window, "User " + selectedUser + " unlocked successfully.");
                    updateList(listModel, filterLocked.isSelected());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(window, "Error unlocking user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(window, "Please select a user to unlock.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        cp.add(unlockUser);

        JTextField sendMessage = new JTextField();
        sendMessage.setBounds(30, 500, 400, 40);
        sendMessage.setText("Send Message To User");
        cp.add(sendMessage);
        JButton sendMessageButton = new JButton("Send Message");
        sendMessageButton.setBounds(30, 600, 200, 40);
        sendMessageButton.addActionListener(e -> {
            try {
                cont.sendUserMessage(resultList.getSelectedValue(), sendMessage.getText());
                System.out.println(resultList.getSelectedValue());
                System.out.println(sendMessage.getText());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(window, "Message sent successfully.");
        });
        cp.add(sendMessageButton);
    }

    // Make updateList a method of the AdminView class
    private void updateList(DefaultListModel<String> listModel, boolean showLockedOnly) throws SQLException {
        String search = userSearch.getText();
        List<String> users = getUsers(search, showLockedOnly);
        listModel.clear();
        users.forEach(listModel::addElement);
    }
    private List<String> getUsers(String query, boolean showLockedOnly) throws SQLException {
        return cont.getUsers(query, showLockedOnly);
    }

    private List getEmployees(String query) throws SQLException {
        return cont.getEmployees(query);
    }



    /**
     * The controller object, used so that an interaction can be passed to the controller
     *
     * @param a The controller
     */

    public void setController(AdminController a) {
        cont = a;
    }

    /**
     * Update the view
     * @param modelA   The observed model
     * @param arg      Specific args
     */
}
