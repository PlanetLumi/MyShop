import javax.swing.*;
import java.awt.*;

public class CustomerClientMVC {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Customer Client MVC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        // Set background color for the frame
        frame.getContentPane().setBackground(new Color(250, 167, 22)); // Light blue

        // Create the top label
        JLabel titleLabel = new JLabel("Search products", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(242, 125, 102)); // Light orange
        titleLabel.setForeground(Color.BLACK);
        frame.add(titleLabel, BorderLayout.NORTH);

        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(250, 167, 22)); // Light blue
        frame.add(mainPanel, BorderLayout.CENTER);

        // Center panel for search and output
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding to the edges
        centerPanel.setBackground(new Color(250, 167, 22)); // Light blue

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(new Color(250, 167, 22)); // Light blue

        JLabel productInfoLabel = new JLabel("40 inch LED HD TV : 269.00 (83)");
        JTextField searchField = new JTextField();
        searchField.setBackground(new Color(242, 125, 102)); // Light orange
        searchField.setOpaque(true);

        searchPanel.add(Box.createVerticalStrut(5));
        searchPanel.add(productInfoLabel);
        searchPanel.add(Box.createVerticalStrut(5));
        searchPanel.add(searchField);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Output panel
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setText("0001    40 inch LED HD    (   1)   £ 269.00\n------------------------------------------\nTotal                                    £ 269.00");
        JScrollPane scrollPane = new JScrollPane(outputArea);

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add the image box below the main text box with padding
        JPanel imageBoxPanel = new JPanel();
        imageBoxPanel.setPreferredSize(new Dimension(150, 75)); // Half the width
        imageBoxPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageBoxPanel.setBackground(Color.WHITE);
        imageBoxPanel.setOpaque(true);

        JPanel imageContainerPanel = new JPanel();
        imageContainerPanel.setBackground(new Color(250, 167, 22)); // Light blue
        imageContainerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        imageContainerPanel.add(imageBoxPanel);

        centerPanel.add(imageContainerPanel, BorderLayout.SOUTH);

        // Add the buttons below the small text box, side by side
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Center with spacing
        buttonPanel.setBackground(new Color(250, 167, 22)); // Light blue

        JButton checkButton = new JButton("Check");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(checkButton);
        buttonPanel.add(clearButton);

        searchPanel.add(Box.createVerticalStrut(10)); // Spacer
        searchPanel.add(buttonPanel);

        // Remove the left panel and adjust layout to ensure no dead space
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around main panel

        // Show the frame
        frame.setVisible(true);
    }
}
