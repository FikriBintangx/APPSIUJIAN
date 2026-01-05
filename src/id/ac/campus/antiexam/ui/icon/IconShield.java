package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import java.awt.geom.Path2D;

public class IconShield extends BaseIcon {

    public IconShield(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

        Path2D shield = new Path2D.Double();
 shield.moveTo(x + w * 0.1, y + h * 0.1);
 shield.lineTo(x + w * 0.9, y + h * 0.1);
 shield.lineTo(x + w * 0.9, y + h * 0.4);
 shield.curveTo(x + w * 0.9, y + h * 0.8, x + w * 0.5, y + h, x + w * 0.5, y + h);
 shield.curveTo(x + w * 0.5, y + h, x + w * 0.1, y + h * 0.8, x + w * 0.1, y + h * 0.4);
        shield.closePath();

        g2.fill(shield);
    }
}
