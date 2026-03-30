package soundproject.fft;

import soundproject.AudioData;

public class FFTProcessor {

    // Store frequencies and their magnitudes
    private final double[] frequencies;
    private final double[] magnitudes;

    // Compute FFT (using simple DFT)
    public FFTProcessor(AudioData audioData) {

        int maxSamples = 2048;
        int fftSize = Math.min(audioData.samples.length, maxSamples);

        double[] samples = new double[fftSize];
        System.arraycopy(audioData.samples, 0, samples, 0, fftSize);

        frequencies = new double[fftSize / 2];
        magnitudes = new double[fftSize / 2];

        double sampleRate = audioData.sampleRate;

        // For each frequency bin k, compute magnitude
        for (int k = 0; k < fftSize / 2; k++) {

            double imagSum = 0.0;
            double realSum = 0.0;

            // DFT computation for bin k
            for (int n = 0; n < fftSize; n++) {
                double theta = -2.0 * Math.PI * k * n / fftSize;
                imagSum += samples[n] * Math.sin(theta);
                realSum += samples[n] * Math.cos(theta);
            }

            double amplitude = Math.sqrt(realSum * realSum + imagSum * imagSum);
            magnitudes[k] = amplitude;

            // Convert bin index to frequency (Hz)
            frequencies[k] = (sampleRate * k) / fftSize;
        }

        normalize();
    }

    // Scale magnitudes to 0–1
    private void normalize() {
        double max = 0.0;
        for (double v : magnitudes) {
            if (v > max) max = v;
        }
        if (max == 0) return;
        for (int i = 0; i < magnitudes.length; i++) {
            magnitudes[i] /= max;
        }
    }

    public double[] getMagnitudes() {
        return magnitudes;
    }

    public double[] getFrequencies() {
        return frequencies;
    }
}