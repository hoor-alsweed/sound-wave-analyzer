package soundproject.fft;

import java.awt.*;
import javax.swing.*;
import soundproject.AudioData;

public class FFTPanel extends JPanel {

    // Use FFTProcessor for spectrum data
    private final FFTProcessor processor;

    // Animation shift value and timer
    private int shift = 0;
    private Timer timer;

    // Initialize FFT and start animation
    public FFTPanel(AudioData audioData) {
        processor = new FFTProcessor(audioData);
        setPreferredSize(new Dimension(800, 300));
        setBackground(Color.BLACK);

        timer = new Timer(40, e -> {
            shift++;
            repaint();
        });
        timer.start();
    }

    // Swing drawing callback
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSpectrum((Graphics2D) g);
    }

    // Draw FFT spectrum with gradient background and animated curve
    private void drawSpectrum(Graphics2D g2) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        double[] magnitude  = processor.getMagnitudes();
        double[] frequencies = processor.getFrequencies();

        int width  = getWidth();
        int height = getHeight();

        // Background gradient
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(10, 10, 10),
                0, height, new Color(40, 40, 40)
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, width, height);

        // Grid
        g2.setColor(new Color(255, 255, 255, 30));
        for (int x = 0; x < width; x += 50) g2.drawLine(x, 0, x, height);
        for (int y = 0; y < height; y += 50) g2.drawLine(0, y, width, y);

        if (magnitude == null || magnitude.length == 0 || frequencies == null) return;

        // Limit spectrum to 0–5000 Hz
        int pointsCount = 0;
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] <= 5000) pointsCount = i;
            else break;
        }
        if (pointsCount <= 1) return;

        double xStep = width / (double) pointsCount;

        g2.setStroke(new BasicStroke(1.5f));

        // Starting animation index
        int firstIdx = shift % pointsCount;
        double prevX = 0;
        double prevY = (height * 0.9) - magnitude[firstIdx] * (height * 0.3);

        // Draw animated spectrum line
        for (int i = 1; i < pointsCount; i++) {

            int idx = (i + shift) % pointsCount;
            float intensity = (float) magnitude[idx];

            // Color based on intensity level
            if (intensity < 0.3)
                g2.setColor(new Color(0, 255, 100));
            else if (intensity < 0.6)
                g2.setColor(new Color(255, 200, 0));
            else
                g2.setColor(new Color(255, 80, 80));

            double x = i * xStep;
            double y = (height * 0.9) - magnitude[idx] * (height * 0.3);

            g2.drawLine((int) prevX, (int) prevY, (int) x, (int) y);

            prevX = x;
            prevY = y;
        }

        // Border
        g2.setColor(new Color(80, 80, 80));
        g2.drawRect(0, 0, width - 1, height - 1);

        // Title
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString("FFT Spectrum (0–5000 Hz)", 10, 20);
    }
}
