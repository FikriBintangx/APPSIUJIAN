package id.ac.campus.antiexam.ui.icon;

import java.awt.*;

public class IconStatus extends BaseIcon {

    public IconStatus(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        g2.fillOval(x, y, width, height);
    }
}
