package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import java.awt.geom.Path2D;

public class IconGraduation extends BaseIcon {

    public IconGraduation(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

 // Cap atas (Rhombus)
        Path2D top = new Path2D.Double();
 top.moveTo(x + w / 2.0, y + h * 0.1);
 top.lineTo(x + w * 0.9, y + h * 0.3);
 top.lineTo(x + w / 2.0, y + h * 0.5);
 top.lineTo(x + w * 0.1, y + h * 0.3);
        top.closePath();
        g2.fill(top);

 // Cap Base
        Path2D base = new Path2D.Double();
 base.moveTo(x + w * 0.2, y + h * 0.35); // kiri intersect
 base.lineTo(x + w * 0.2, y + h * 0.6); // Down
 base.curveTo(x + w * 0.2, y + h * 0.75, x + w * 0.8, y + h * 0.75, x + w * 0.8, y + h * 0.6); // Curve bawah
 base.lineTo(x + w * 0.8, y + h * 0.35); // Up buat kanan intersect
        // tutup dengan atas shape logika roughly, atau cuma fill
        g2.fill(base);

 // Tassel
 g2.setStroke(new BasicStroke(w * 0.02f));
 g2.drawLine(x + w / 2, (int) (y + h * 0.1), (int) (x + w * 0.9), (int) (y + h * 0.3)); // Line dari tengah buat
 // corner
 g2.drawLine((int) (x + w * 0.9), (int) (y + h * 0.3), (int) (x + w * 0.9), (int) (y + h * 0.6)); // Drop down
 g2.fillOval((int) (x + w * 0.85), (int) (y + h * 0.6), (int) (w * 0.1), (int) (w * 0.1)); // Ball
    }
}
