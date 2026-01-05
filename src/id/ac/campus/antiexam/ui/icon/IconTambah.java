package id.ac.campus.antiexam.ui.icon;

import java.awt.*;

public class IconTambah extends BaseIcon {

    public IconTambah(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;
        int thickness = w / 5;

        g2.fillRect(x + (w - thickness) / 2, y, thickness, h);
        g2.fillRect(x, y + (h - thickness) / 2, w, thickness);
    }
}
