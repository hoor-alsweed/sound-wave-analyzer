package soundproject.power;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.BasicStroke;

public class SpectrumPanel extends JPanel {

    private PowerSpectrum spectrum;

    public SpectrumPanel(PowerSpectrum spectrum) {
        this.spectrum = spectrum;
        setBackground(Color.BLACK);
    }

    public void setSpectrum(PowerSpectrum spectrum) {
        this.spectrum = spectrum;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (spectrum == null) {
            g.setColor(Color.WHITE);
            g.drawString("Load audio to view Power Spectrum...", 20, 20);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        double[] freqs = spectrum.getFrequencies();
        double[] power  = spectrum.getPower();

        int width  = getWidth();
        int height = getHeight();

        // ========== PLOT AREA ==========
        int left   = 60;
        int right  = width - 20;
        int bottom = height - 40;
        int top    = 20;

        int plotWidth  = right - left;
        int plotHeight = bottom - top;

        // ========== BACKGROUND ==========
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        // ========== GRID ==========
        g2.setColor(new Color(255, 255, 255, 40));
        for (int x = left; x < right; x += 80) {
            g2.drawLine(x, top, x, bottom);
        }
        for (int y = top; y < bottom; y += 50) {
            g2.drawLine(left, y, right, y);
        }

        // ========== AXES ==========
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(left, bottom, right, bottom); // X-axis
        g2.drawLine(left, bottom, left, top);     // Y-axis

        int n = power.length;
        if (n < 2) return;

        double maxPower = spectrum.getMaxPower();
        if (maxPower == 0) maxPower = 1;

        // ========== DRAW SPECTRUM ==========
        g2.setStroke(new BasicStroke(1.6f));

        int prevX = left;
        int prevY = bottom;

        for (int i = 0; i < n; i++) {

            double xNorm = (double) i / (n - 1);
            int x = left + (int) (xNorm * plotWidth);

            double pNorm = power[i] / maxPower; // 0..1
            int y = bottom - (int) (pNorm * plotHeight);

            // لون ديناميكي حسب الشدة
            float level = (float) pNorm;
            g2.setColor(new Color(level, 1f - level, 0.2f));

            if (i > 0) {
                g2.drawLine(prevX, prevY, x, y);
            }

            prevX = x;
            prevY = y;
        }

        // ========== LABELS ==========
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));

        g2.drawString("Power Spectrum", width / 2 - 50, 16);
        g2.drawString("Frequency (Hz)", width / 2 - 40, height - 10);
        g2.drawString("Power", 5, height / 2);

        // Peak frequency display
        double peakFreq = spectrum.getMaxFrequency();
        g2.setColor(Color.YELLOW);
        g2.drawString(
                "Peak: " + String.format("%.1f Hz", peakFreq),
                width - 170,
                16
        );
    }
}
