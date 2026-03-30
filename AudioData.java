package soundproject;

public class AudioData {

    public double[] samples;
    public int sampleRate;
    public int numChannels;

    public AudioData(double[] samples, int sampleRate, int numChannels) {
        this.samples = samples;
        this.sampleRate = sampleRate;
        this.numChannels = numChannels;
    }

    public double getDurationSeconds() {
        return samples.length / (double) sampleRate;
    }
}
