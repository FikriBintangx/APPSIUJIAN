package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import javax.swing.Icon;

public abstract class BaseIcon implements Icon {
    protected int width;
    protected int height;
    protected Color color;

    public BaseIcon(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        paintIcon(g2, x, y);
        g2.dispose();
    }

    protected abstract void paintIcon(Graphics2D g2, int x, int y);
}
