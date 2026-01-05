package id.ac.campus.antiexam.ui.icon;

import java.awt.*;

public class IconDashboard extends BaseIcon {

    public IconDashboard(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int gap = width / 5;
        int size = (width - gap) / 2;

 // atas kiri
        g2.fillRect(x, y, size, size);
 // atas kanan
        g2.fillRect(x + size + gap, y, size, size);
 // bawah kiri
        g2.fillRect(x, y + size + gap, size, size);
 // bawah kanan
        g2.fillRect(x + size + gap, y + size + gap, size, size);
    }
}
