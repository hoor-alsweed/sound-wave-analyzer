package soundproject.filter;

public class FilterProcessor {

    private double alpha;

    private double computeAlpha(double cutoff, double sampleRate) {
        double RC = 1.0 / (2 * Math.PI * cutoff);
        double dt = 1.0 / sampleRate;
        return dt / (RC + dt);
    }

    // -------- Low Pass Filter --------
    public double[] applyLowPass(double[] x, double cutoff, double sampleRate) {

        alpha = computeAlpha(cutoff, sampleRate);

        double[] y = new double[x.length];
        y[0] = x[0];

        for (int n = 1; n < x.length; n++) {
            y[n] = y[n - 1] + alpha * (x[n] - y[n - 1]);
        }

        return normalize(y);
    }

    // -------- High Pass Filter --------
    public double[] applyHighPass(double[] x, double cutoff, double sampleRate) {

        alpha = computeAlpha(cutoff, sampleRate);

        double[] y = new double[x.length];
        y[0] = x[0];

        for (int n = 1; n < x.length; n++) {
            y[n] = alpha * (y[n - 1] + x[n] - x[n - 1]);
        }

        return normalize(y);
    }

    // -------- Normalization --------
    private double[] normalize(double[] data) {
        double max = 0;
        for (double v : data) {
            if (Math.abs(v) > max)
                max = Math.abs(v);
        }
        if (max == 0) return data;

        for (int i = 0; i < data.length; i++) {
            data[i] /= max;
        }
        return data;
    }
}
