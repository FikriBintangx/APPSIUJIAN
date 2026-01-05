package id.ac.campus.antiexam.ui.ux;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PDFViewerPanel extends JPanel {
    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage = 0;
    private final JLabel lblImage;

    public PDFViewerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 41, 59));
        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setBackground(new Color(30, 41, 59));
        lblImage.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(lblImage);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(30, 41, 59));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                document = Loader.loadPDF(file);
                renderer = new PDFRenderer(document);
                renderPage();
            } else {
                lblImage.setText(
                        "<html><center><font color='white' size='4'>File PDF tidak ditemukan</font><br><font color='#94a3b8'>"
                                + filePath + "</font></center></html>");
                lblImage.setHorizontalAlignment(SwingConstants.CENTER);
                lblImage.setVerticalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            lblImage.setText(
                    "<html><center><font color='white' size='4'>Gagal memuat PDF</font><br><font color='#94a3b8'>"
                            + e.getMessage() + "</font></center></html>");
            lblImage.setHorizontalAlignment(SwingConstants.CENTER);
            lblImage.setVerticalAlignment(SwingConstants.CENTER);
        }
    }

    private void renderPage() {
        if (document == null)
            return;
        try {
            BufferedImage image = renderer.renderImageWithDPI(currentPage, 120, ImageType.RGB);
            lblImage.setIcon(new ImageIcon(image));
            lblImage.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextPage() {
        if (document != null && currentPage < document.getNumberOfPages() - 1) {
            currentPage++;
            renderPage();
        }
    }

    public void prevPage() {
        if (document != null && currentPage > 0) {
            currentPage--;
            renderPage();
        }
    }

    public int getPageCount() {
        return document == null ? 0 : document.getNumberOfPages();
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
