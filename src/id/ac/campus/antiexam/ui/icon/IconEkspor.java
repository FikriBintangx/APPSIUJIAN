package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import java.awt.geom.Path2D;

public class IconEkspor extends BaseIcon {

    public IconEkspor(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

 // Bracket bawah
 g2.setStroke(new BasicStroke(w * 0.15f));
        g2.drawLine(x, y + h / 2, x, y + h);
        g2.drawLine(x, y + h, x + w, y + h);
        g2.drawLine(x + w, y + h, x + w, y + h / 2);

 // Arrow
        Path2D arrow = new Path2D.Double();
 arrow.moveTo(x + w * 0.2, y + h * 0.3);
 arrow.lineTo(x + w * 0.5, y);
 arrow.lineTo(x + w * 0.8, y + h * 0.3);

        g2.draw(arrow);

 g2.drawLine(x + w / 2, y, x + w / 2, (int) (y + h * 0.6));
    }
}
