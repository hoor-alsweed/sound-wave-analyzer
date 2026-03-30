package soundproject.beat;

import soundproject.AudioData;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

public class BeatPanel extends JPanel {

    private AudioData audio;
    private BeatDetector detector;
    private List<Double> beats;

    public BeatPanel(AudioData audio) {
        this.audio = audio;
        this.detector = new BeatDetector(audio.samples, audio.sampleRate);
        this.beats = detector.detectBeats(1024, 512, 1.5);

        setBackground(new Color(20, 22, 28));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Anti-Aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // خلفية متدرجة
        GradientPaint bg = new GradientPaint(
                0, 0, new Color(25, 28, 35),
                0, h, new Color(10, 12, 18)
        );
        g2.setPaint(bg);
        g2.fillRect(0, 0, w, h);

        // Grid خفيف
        g2.setColor(new Color(255, 255, 255, 20));
        for (int i = 0; i < w; i += 100) {
            g2.drawLine(i, 0, i, h);
        }
        for (int i = 0; i < h; i += 80) {
            g2.drawLine(0, i, w, i);
        }

        // خط المنتصف
        g2.setColor(new Color(180, 180, 180, 60));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(0, h / 2, w, h / 2);

        // رسم الموجة
        double[] samples = audio.samples;
        int total = samples.length;
        if (total <= 1) return;

        double xScale = w / (double) total;
        int step = 20;

        GradientPaint waveGradient = new GradientPaint(
                0, h / 2f, new Color(0, 180, 255),
                w, h / 2f, new Color(0, 255, 150)
        );
        g2.setPaint(waveGradient);
        g2.setStroke(new BasicStroke(2.0f));

        int prevX = 0;
        int prevY = h / 2;

        for (int i = 0; i < total; i += step) {
            int x = (int) (i * xScale);
            int y = (int) (h / 2 - samples[i] * (h / 2 - 15));

            if (i > 0) {
                g2.draw(new Line2D.Float(prevX, prevY, x, y));
            }

            prevX = x;
            prevY = y;
        }

        // رسم الـ Beats
        if (beats != null) {
            double duration = samples.length / (double) audio.sampleRate;

            for (double t : beats) {
                if (t < 0 || t > duration) continue;

                int x = (int) ((t / duration) * w);

                // خط شفاف
                g2.setColor(new Color(255, 80, 80, 120));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawLine(x, 0, x, h);

                // نقطة مضيئة
                g2.setColor(new Color(255, 120, 120));
                g2.fillOval(x - 5, h / 2 - 5, 10, 10);
            }
        }

        // عنوان أعلى اللوحة
        g2.setColor(new Color(220, 220, 220));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("Beat Detection Visualization", 16, 24);
    }
}
