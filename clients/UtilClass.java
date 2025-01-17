package clients;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

public class UtilClass {

    public static final Color TURQUOISE = new Color(64, 224, 208);

    public static void setTurquoiseBackground(RootPaneContainer rpc) {
        if (rpc instanceof JFrame) {
            ((JFrame) rpc).getContentPane().setBackground(TURQUOISE);
        } else if (rpc instanceof JApplet) {
            ((JApplet) rpc).getContentPane().setBackground(TURQUOISE);
        } else if (rpc instanceof JDialog) {
            ((JDialog) rpc).getContentPane().setBackground(TURQUOISE);
        }
        // ...
    }

    /**
     * Creates a rounded button with black text and a dark gray border.
     */
    public static JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);

        // Black text
        button.setForeground(Color.BLACK);

        // Turquoise background
        button.setBackground(TURQUOISE);

        // Dark gray border
        button.setBorder(new RoundedBorder(20, new Color(64, 64, 64)));

        return button;
    }

    private static class RoundedBorder implements Border {
        private int radius;
        private Color borderColor;

        public RoundedBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 2, radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c,
                                Graphics g,
                                int x,
                                int y,
                                int width,
                                int height) {
            // Save the old color
            Color oldColor = g.getColor();
            // Set the custom border color
            g.setColor(borderColor);
            // Draw the round rect border
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            // Reset the graphics object's color
            g.setColor(oldColor);
        }
    }
}
