package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import java.awt.geom.Path2D;

public class IconPeringatan extends BaseIcon {

    public IconPeringatan(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

        Path2D p = new Path2D.Double();
        p.moveTo(x + w / 2, y);
        p.lineTo(x + w, y + h);
        p.lineTo(x, y + h);
        p.closePath();

        g2.draw(p);

        g2.fillRect(x + w / 2 - w / 12, y + h / 3, w / 6, h / 3);
 g2.fillOval(x + w / 2 - w / 12, y + h * 3 / 4, w / 6, w / 6);
    }
}
