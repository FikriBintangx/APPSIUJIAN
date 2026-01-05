package id.ac.campus.antiexam.ui.icon;

import java.awt.*;

public class IconDelete extends BaseIcon {

    public IconDelete(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

 // Lid
        g2.fillRect(x, y, w, h / 6);
 g2.fillRect(x + w / 3, y - h / 6, w / 3, h / 6); // handle

 // Body
 g2.fillRect(x + w / 6, y + h / 5, w * 2 / 3, h * 3 / 4);

 // Lines
        g2.setColor(Color.WHITE);
        g2.fillRect(x + w / 3, y + h / 3, w / 10, h / 2);
 g2.fillRect(x + w * 2 / 3 - w / 10, y + h / 3, w / 10, h / 2);
    }
}
