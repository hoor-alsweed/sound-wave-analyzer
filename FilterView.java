package soundproject.filter;

import javax.swing.*;
import java.awt.*;
import soundproject.AudioData;

public class FilterView extends JPanel {

    private JTextField cutoffField;
    private JButton lowBtn, highBtn;
    private double[] filtered;
    private AudioData audioData;

    private FilterProcessor processor = new FilterProcessor();

    public FilterView(AudioData data) {

        this.audioData = data;
        setLayout(new BorderLayout());
        setBackground(new Color(18, 28, 44)); // نفس الثيم

        // ===== Top Controls =====
        JPanel top = new JPanel(new FlowLayout());
        top.setBackground(new Color(18, 28, 44));

        top.add(new JLabel("Cutoff:"));
        cutoffField = new JTextField("500", 8);
        top.add(cutoffField);

        lowBtn = new JButton("Low-pass");
        highBtn = new JButton("High-pass");

        top.add(lowBtn);
        top.add(highBtn);

        add(top, BorderLayout.NORTH);

        // ===== Draw Panel =====
        JPanel drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int w = getWidth();
                int h = getHeight();
                int mid = h / 2;

                g.setColor(Color.GRAY);
                g.drawLine(0, mid, w, mid);

                if (audioData == null || audioData.samples == null)
                    return;

                double[] original = audioData.samples;

                // ----- Original waveform -----
                g.setColor(new Color(120, 150, 255));
                drawWave(g, original, w, h);

                // ----- Filtered waveform -----
                if (filtered != null) {
                    g.setColor(new Color(255, 120, 120));
                    drawWave(g, filtered, w, h);
                }
            }

            private void drawWave(Graphics g, double[] data, int w, int h) {

                int mid = h / 2;
                int step = Math.max(1, data.length / w);

                int prevX = 0;
                int prevY = mid;

                for (int i = 0; i < data.length; i += step) {
                    int x = (int) ((i / (double) data.length) * w);
                    int y = mid - (int) (data[i] * (h / 2 - 10));

                    g.drawLine(prevX, prevY, x, y);

                    prevX = x;
                    prevY = y;
                }
            }
        };

        add(drawPanel, BorderLayout.CENTER);

        double[] samples = audioData.samples;
        double rate = audioData.sampleRate;

        // ===== Button Actions =====
        lowBtn.addActionListener(e -> {
            double cutoff = Double.parseDouble(cutoffField.getText());
            filtered = processor.applyLowPass(samples, cutoff, rate);
            drawPanel.repaint();
        });

        highBtn.addActionListener(e -> {
            double cutoff = Double.parseDouble(cutoffField.getText());
            filtered = processor.applyHighPass(samples, cutoff, rate);
            drawPanel.repaint();
        });
    }
}
