package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import java.awt.geom.Path2D;

public class IconUjian extends BaseIcon {

    public IconUjian(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

 // outline kertas
        Path2D path = new Path2D.Double();
 path.moveTo(x + w * 0.2, y);
 path.lineTo(x + w * 0.8, y);
 path.lineTo(x + w, y + h * 0.2); // lipatan
        path.lineTo(x + w, y + h);
 path.lineTo(x + w * 0.2, y + h);
        path.closePath();

 g2.setStroke(new BasicStroke(w * 0.08f));
        g2.draw(path);

        // garis-garis di dalem
 int lineX = (int) (x + w * 0.35);
 int lineW = (int) (w * 0.4);
 g2.fillRect(lineX, (int) (y + h * 0.4), lineW, (int) (h * 0.1));
 g2.fillRect(lineX, (int) (y + h * 0.6), lineW, (int) (h * 0.1));
    }
}
