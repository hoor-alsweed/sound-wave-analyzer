package soundproject.amplitude;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AmplitudePanel extends JPanel {

    private AmplitudeAnalyzer analyzer;

    private double zoom = 1.0;           // Initial zoom level
    private double offsetSamples = 0.0;  // Initial offset for horizontal panning
    private int lastMouseX = 0;          // Track last X position of mouse

    public AmplitudePanel(AmplitudeAnalyzer analyzer) {
        this.analyzer = analyzer;
        setPreferredSize(new Dimension(900, 350)); // Set preferred size of the panel
        setBackground(Color.BLACK); // Set background color to black

        // Mouse wheel zoom
        addMouseWheelListener(e -> {
            if (e.getPreciseWheelRotation() < 0) {
                zoom *= 1.2; // Zoom in
            } else {
                zoom /= 1.2; // Zoom out
            }

            zoom = Math.max(0.2, Math.min(zoom, 20.0)); // Limit zoom range
            repaint(); // Repaint the panel
        });

        // Start of dragging (horizontal pan)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX(); // Store initial mouse X position
            }
        });

        // Dragging motion for horizontal panning
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX; // Calculate horizontal distance moved
                lastMouseX = e.getX(); // Update the last X position

                double[] samples = analyzer.getAmplitudeData(); // Get the amplitude data
                if (samples == null || samples.length == 0) return; // If no data, do nothing

                int width = getWidth(); // Get the width of the panel
                double xScale = (width - 60) / (double) samples.length * zoom; // Calculate scale for the X axis
                if (xScale <= 0) return; // Prevent division by zero

                offsetSamples -= dx / xScale; // Adjust the offset based on mouse movement

                // Ensure the offset stays within the bounds of the sample data
                if (offsetSamples < 0) offsetSamples = 0;
                if (offsetSamples > samples.length - 1) offsetSamples = samples.length - 1;

                repaint(); // Repaint the panel
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Enable anti-aliasing

        int width = getWidth();  // Get the width of the panel
        int height = getHeight();  // Get the height of the panel
        int midY = height / 2; // Middle Y position for waveform baseline

        double[] samples = analyzer.getAmplitudeData(); // Get amplitude data from analyzer
        if (samples == null || samples.length == 0) {
            g2.setColor(Color.WHITE);
            g2.drawString("No audio loaded...", 20, 20); // Show message if no data
            return;
        }

        // ===== GRID =====
        g2.setColor(new Color(255, 255, 255, 30)); // Semi-transparent grid color
        for (int x = 50; x < width; x += 100) {
            g2.drawLine(x, 0, x, height); // Draw vertical grid lines
        }
        for (int y = 0; y < height; y += 50) {
            g2.drawLine(50, y, width - 10, y); // Draw horizontal grid lines
        }

        // ===== AXES =====
        g2.setColor(Color.WHITE);  // White color for axes
        g2.setStroke(new BasicStroke(2));  // Set stroke for the axes
        g2.drawLine(50, 10, 50, height - 10);     // Y-axis line
        g2.drawLine(50, midY, width - 10, midY); // X-axis line

        // ===== Y LABELS =====
        double[] yValues = {1.0, 0.5, 0.0, -0.5, -1.0};  // Amplitude levels for Y-axis
        g2.setFont(new Font("Arial", Font.PLAIN, 12));  // Set font for Y labels

        for (double val : yValues) {
            int y = midY - (int) (val * (height / 2 - 20)); // Calculate Y position based on value
            g2.setColor(new Color(255, 255, 255, 60)); // Semi-transparent Y grid lines
            g2.drawLine(50, y, width - 10, y); // Draw horizontal grid lines at Y levels
            g2.setColor(Color.WHITE); // Set color to white for the label text
            g2.drawString(String.valueOf(val), 10, y + 5); // Draw the label
        }

        // ===== X LABELS =====
        int sampleRate = analyzer.getSampleRate(); // Get the sample rate of the audio
        int totalSamples = samples.length; // Get the total number of samples
        double duration = totalSamples / (double) sampleRate; // Calculate the duration in seconds

        double step;
        if (duration < 1) step = 0.1;
        else if (duration < 5) step = 0.5;
        else if (duration < 30) step = 1.0;
        else if (duration < 120) step = 5.0;
        else step = 10.0; // Set the step size for X labels based on the duration

        g2.setColor(Color.WHITE); // Set color for X labels
        for (double t = 0; t <= duration; t += step) {
            int sampleIndex = (int) (t * sampleRate); // Calculate the sample index for the current time

            double xScale = (width - 60) / (double) totalSamples * zoom; // X scale for zooming
            int x = 50 + (int) ((sampleIndex - offsetSamples) * xScale); // Calculate X position for the label

            if (x >= 50 && x <= width - 10) {
                g2.drawString(String.format("%.2f s", t), x - 12, midY + 25); // Draw the time label
                g2.drawLine(x, midY - 5, x, midY + 5); // Draw tick mark at the label
            }
        }

        g2.drawString("Amplitude", 5, 20); // Label for Y-axis
        g2.drawString("Time →", width - 70, midY - 5); // Label for X-axis

        // ===== TITLE =====
        g2.setFont(new Font("Arial", Font.BOLD, 16)); // Set font for title
        g2.drawString("Waveform (Time Domain)", width / 2 - 120, 25); // Title of the waveform
        g2.setFont(new Font("Arial", Font.PLAIN, 12)); // Reset font for other text

        // ===== DRAW WAVEFORM =====
        g2.setColor(Color.GREEN); // Set color for the waveform
        g2.setStroke(new BasicStroke(1.5f)); // Set stroke for drawing waveform

        double xScale = (width - 60) / (double) totalSamples * zoom; // Calculate X scale based on zoom
        if (xScale <= 0) return; // Prevent drawing if X scale is zero or negative

        int startIndex = (int) Math.max(0, offsetSamples); // Start drawing at the offset
        int endIndex = (int) Math.min(totalSamples - 1, offsetSamples + (width - 60) / xScale); // End index based on the panel width

        int prevX = 50;
        int prevY = midY;

        // Draw the waveform
        for (int i = startIndex + 1; i <= endIndex; i++) {
            int x = 50 + (int) ((i - offsetSamples) * xScale); // Calculate X position for the sample
            int y = midY - (int) (samples[i] * (height / 2 - 20)); // Calculate Y position based on amplitude
            g2.drawLine(prevX, prevY, x, y); // Draw a line between previous and current sample points

            prevX = x; // Update previous X position
            prevY = y; // Update previous Y position
        }
    }
}
