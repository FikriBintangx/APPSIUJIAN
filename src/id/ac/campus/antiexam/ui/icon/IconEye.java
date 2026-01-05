package id.ac.campus.antiexam.ui.icon;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Ellipse2D;

public class IconEye extends BaseIcon {

    public IconEye(int size, Color color) {
        super(size, size, color);
    }

    @Override
    protected void paintIcon(Graphics2D g2, int x, int y) {
        int w = width;
        int h = height;

 g2.setStroke(new BasicStroke(w * 0.08f));

 // Eye shape
        Path2D eye = new Path2D.Double();
        eye.moveTo(x, y + h / 2.0);
        eye.quadTo(x + w / 2.0, y, x + w, y + h / 2.0);
        eye.quadTo(x + w / 2.0, y + h, x, y + h / 2.0);
        g2.draw(eye);

 // Pupil
 g2.fill(new Ellipse2D.Double(x + w * 0.35, y + h * 0.35, w * 0.3, h * 0.3));
    }
}
