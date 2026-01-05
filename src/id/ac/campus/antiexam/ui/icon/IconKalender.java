package id.ac.campus.antiexam.ui.icon;

import java.awt.*;

public class IconKalender extends BaseIcon {

    public IconKalender(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

 g2.setStroke(new BasicStroke(w * 0.1f));

 // Body
 g2.drawRoundRect(x, y + h / 4, w, h * 3 / 4, w / 5, w / 5);

 // header fill
        g2.fillRect(x, y + h / 4, w, h / 6);

 // Rings
        g2.fillOval(x + w / 4 - w / 10, y, w / 5, w / 5);
 g2.fillOval(x + w * 3 / 4 - w / 10, y, w / 5, w / 5);
    }
}
