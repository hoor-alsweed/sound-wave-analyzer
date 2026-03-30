package soundproject.beat;

import java.util.ArrayList;
import java.util.List;

public class BeatDetector {

    private double[] samples;
    private int sampleRate;

    public BeatDetector(double[] samples, int sampleRate) {
        this.samples = samples;
        this.sampleRate = sampleRate;
    }

    private double[] computeEnergy(int frameSize, int hopSize) {
        int frames = Math.max(1, 1 + (samples.length - frameSize) / hopSize);
        double[] energy = new double[frames];

        for (int f = 0; f < frames; f++) {
            int start = f * hopSize;
            int end = Math.min(start + frameSize, samples.length);
            double sum = 0.0;
            for (int i = start; i < end; i++) {
                double s = samples[i];
                sum += s * s;
            }
            energy[f] = sum / (end - start);
        }
        return energy;
    }


    private double[] smooth(double[] energy, int window) {
        int n = energy.length;
        double[] result = new double[n];
        int half = window / 2;

        for (int i = 0; i < n; i++) {
            int from = Math.max(0, i - half);
            int to   = Math.min(n - 1, i + half);
            double sum = 0.0;
            int count = 0;
            for (int j = from; j <= to; j++) {
                sum += energy[j];
                count++;
            }
            result[i] = sum / count;
        }
        return result;
    }

    /**
     * كشف النبضات:
     * @param frameSize حجم النافذة
     * @param hopSize التقدم بين كل نافذة والتي بعدها
     * @param thresholdMultiplier معامل العتبة
     * @return قائمة أوقات النبضات بالثواني
     */
    public List<Double> detectBeats(int frameSize, int hopSize, double thresholdMultiplier) {
        double[] energy = computeEnergy(frameSize, hopSize);
        double[] envelope = smooth(energy, 5);

        double mean = 0.0;
        for (double e : envelope) {
            mean += e;
        }
        mean /= envelope.length;

        double threshold = mean * thresholdMultiplier;

        List<Double> beats = new ArrayList<>();

        for (int i = 1; i < envelope.length - 1; i++) {
            boolean isPeak = envelope[i] > envelope[i - 1] && envelope[i] > envelope[i + 1];
            if (envelope[i] > threshold && isPeak) {
                int centerSampleIndex = i * hopSize;
                double timeSeconds = centerSampleIndex / (double) sampleRate;
                beats.add(timeSeconds);
            }
        }
        return beats;
    }
}
