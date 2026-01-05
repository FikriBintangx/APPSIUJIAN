package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.AffineTransform;

public class IconEdit extends BaseIcon {

    public IconEdit(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

 // Rotate 45 deg
        AffineTransform old = g2.getTransform();
        g2.rotate(Math.toRadians(45), x + w / 2, y + h / 2);

 g2.fillRect(x + w / 2 - w / 6, y, w / 3, (int) (h * 0.7f));

        Path2D tip = new Path2D.Double();
 tip.moveTo(x + w / 2 - w / 6, y + h * 0.7);
 tip.lineTo(x + w / 2 + w / 6, y + h * 0.7);
        tip.lineTo(x + w / 2, y + h);
        tip.closePath();
        g2.fill(tip);

        g2.setTransform(old);
    }
}
