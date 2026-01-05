package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import java.awt.geom.Path2D;

public class IconDirektori extends BaseIcon {

    public IconDirektori(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

        Path2D path = new Path2D.Double();
 // Tab
 path.moveTo(x, y + h * 0.2);
 path.lineTo(x + w * 0.4, y + h * 0.2);
 path.lineTo(x + w * 0.5, y + h * 0.4); // Slant
 path.lineTo(x + w, y + h * 0.4);
        path.lineTo(x + w, y + h);
        path.lineTo(x, y + h);
        path.closePath();

        g2.fill(path);

 // belakang tab outline potentially? Simpler adalah filled.
    }
}
