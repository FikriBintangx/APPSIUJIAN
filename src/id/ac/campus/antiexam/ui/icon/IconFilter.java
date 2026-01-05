package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import java.awt.geom.Path2D;

public class IconFilter extends BaseIcon {

    public IconFilter(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

        Path2D p = new Path2D.Double();
        p.moveTo(x, y);
        p.lineTo(x + w, y);
 p.lineTo(x + w * 0.6, y + h * 0.6);
 p.lineTo(x + w * 0.6, y + h);
 p.lineTo(x + w * 0.4, y + h);
 p.lineTo(x + w * 0.4, y + h * 0.6);
        p.closePath();

        g2.fill(p);
    }
}
