package id.ac.campus.antiexam.ui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.Timer;

public class ToastNotification extends JComponent {
    private final String message;
    private final JFrame parent;
    private int alpha = 0;
    private boolean fadingIn = true;
    private Timer timer;
    private int yPos;

    public static final Color SUCCESS = new Color(16, 185, 129);
    public static final Color INFO = new Color(59, 130, 246);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color ERROR = new Color(239, 68, 68);

    private final Color bgColor;

    public ToastNotification(JFrame parent, String message, Color color) {
        this.parent = parent;
        this.message = message;
        this.bgColor = color;

        setOpaque(false);
        setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Calculate size
        int width = getFontMetrics(getFont()).stringWidth(message) + 40;
        int height = 40;

        setSize(width, height);
        yPos = parent.getHeight() - 80;
        setLocation((parent.getWidth() - width) / 2, yPos + 20); // Start slightly lower
    }

    public void showToast() {
        JLayeredPane glass = parent.getLayeredPane();
        glass.add(this, JLayeredPane.POPUP_LAYER);

        timer = new Timer(20, e -> {
            if (fadingIn) {
                alpha += 15;
                if (yPos > parent.getHeight() - 100)
                    yPos -= 1; // Slide up effect
                setLocation(getX(), yPos);

                if (alpha >= 240) { // Max opacity
                    alpha = 240;
                    fadingIn = false;
                    ((Timer) e.getSource()).setDelay(2000); // Hold for 2 seconds
                }
            } else {
                ((Timer) e.getSource()).setDelay(20);
                alpha -= 10;
                if (alpha <= 0) {
                    alpha = 0;
                    ((Timer) e.getSource()).stop();
                    parent.getLayeredPane().remove(this);
                    parent.repaint();
                }
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), alpha));
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        g2.setColor(new Color(255, 255, 255, alpha));
        g2.setFont(getFont());

        int textX = (getWidth() - g2.getFontMetrics().stringWidth(message)) / 2;
        int textY = ((getHeight() - g2.getFontMetrics().getHeight()) / 2) + g2.getFontMetrics().getAscent();

        g2.drawString(message, textX, textY);
        g2.dispose();
    }
}
