package soundproject.power;

import soundproject.fft.FFTProcessor;

public class PowerSpectrum {

    private final double[] frequencies;
    private final double[] power;

    // ================= CONSTRUCTOR =================
    public PowerSpectrum(FFTProcessor fft) {


        double[] magnitudes = fft.getMagnitudes();
        double[] freqs      = fft.getFrequencies();


        this.frequencies = freqs.clone();


        this.power = new double[magnitudes.length];
        for (int i = 0; i < magnitudes.length; i++) {
            power[i] = magnitudes[i] * magnitudes[i];   // Power = |FFT|^2
        }

        normalizePower();
    }

    // ================= NORMALIZATION =================
    private void normalizePower() {
        double max = 0.0;
        for (double p : power) {
            if (p > max) max = p;
        }
        if (max == 0) return;

        for (int i = 0; i < power.length; i++) {
            power[i] /= max;
        }
    }

    // ================= ANALYSIS METHODS =================
    public double getMaxPower() {
        double max = 0;
        for (double p : power) {
            if (p > max) max = p;
        }
        return max;
    }

    public int getMaxPowerIndex() {
        double max = getMaxPower();
        for (int i = 0; i < power.length; i++) {
            if (power[i] == max) return i;
        }
        return 0;
    }

    public double getMaxFrequency() {
        return frequencies[getMaxPowerIndex()];
    }

    public double getAveragePower() {
        if (power == null || power.length == 0) return 0.0;

        double sum = 0.0;
        for (double p : power) sum += p;

        return sum / power.length;
    }

    // ================= GETTERS =================
    public double[] getFrequencies() {
        return frequencies;
    }

    public double[] getPower() {
        return power;
    }
}
