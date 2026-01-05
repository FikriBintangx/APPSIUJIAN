package id.ac.campus.antiexam.ui.icon;

import java.awt.*;

public class IconInfo extends BaseIcon {

    public IconInfo(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

 g2.setStroke(new BasicStroke(w * 0.1f));
        g2.drawOval(x, y, w - 1, h - 1);

 // i dot
        g2.fillRect(x + w / 2 - w / 10, y + h / 5, w / 5, w / 5);

 // i body
        g2.fillRect(x + w / 2 - w / 12, y + h / 2, w / 6, h / 3);
    }
}
