package id.ac.campus.antiexam.ui.ux;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfPanel extends JPanel {
    private PDDocument doc;
    private PDFRenderer renderer;
    private int totalPages = 0;
    private int currentPage = 0;
    private float scale = 1.25f; // default scale
    private JLabel lblPage;
    private JLabel lblInfo;

    public PdfPanel(String filePath) {
        setLayout(new BorderLayout());
        setBackground(new Color(82, 82, 82)); // Dark grey background like standard PDF viewers

        lblPage = new JLabel();
        lblPage.setHorizontalAlignment(SwingConstants.CENTER);

        // Scroll pane buat page
        JScrollPane scroll = new JScrollPane(lblPage);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(82, 82, 82));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controls.setBackground(Color.WHITE);
        controls.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton btnPrev = createNavButton("<", "Halaman Sebelumnya");
        JButton btnNext = createNavButton(">", "Halaman Selanjutnya");
        JButton btnZoomIn = createNavButton("Zoom +", "Perbesar");
        JButton btnZoomOut = createNavButton("Zoom -", "Perkecil");

        lblInfo = new JLabel("Loading...");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        controls.add(btnPrev);
        controls.add(lblInfo);
        controls.add(btnNext);
        controls.add(new JSeparator(SwingConstants.VERTICAL));
        controls.add(btnZoomOut);
        controls.add(btnZoomIn);

        add(controls, BorderLayout.SOUTH);

        // load PDF in background to avoid freezing UI
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    doc = Loader.loadPDF(new File(filePath));
                    renderer = new PDFRenderer(doc);
                    totalPages = doc.getNumberOfPages();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                return null;
            }

            @Override
            protected void done() {
                if (doc != null) {
                    renderPage();
                    btnPrev.addActionListener(e -> {
                        if (currentPage > 0) {
                            currentPage--;
                            renderPage();
                        }
                    });
                    btnNext.addActionListener(e -> {
                        if (currentPage < totalPages - 1) {
                            currentPage++;
                            renderPage();
                        }
                    });
                    btnZoomIn.addActionListener(e -> {
                        if (scale < 3.0f) {
                            scale += 0.25f;
                            renderPage();
                        }
                    });
                    btnZoomOut.addActionListener(e -> {
                        if (scale > 0.5f) {
                            scale -= 0.25f;
                            renderPage();
                        }
                    });
                } else {
                    lblInfo.setText("Gagal memuat PDF");
                    lblPage.setText("X File tidak dapat dibaca atau rusak.");
                    lblPage.setForeground(Color.WHITE);
                }
            }
        }.execute();
    }

    private void renderPage() {
        if (renderer == null)
            return;

        lblInfo.setText("Halaman " + (currentPage + 1) + " / " + totalPages);

        // render di worker biar UI smooth
        new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                return renderer.renderImage(currentPage, scale);
            }

            @Override
            protected void done() {
                try {
                    BufferedImage img = get();
                    lblPage.setIcon(new ImageIcon(img));
                    lblPage.revalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private JButton createNavButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setToolTipText(tooltip);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
