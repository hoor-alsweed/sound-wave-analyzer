package soundproject.amplitude;

import soundproject.AudioData;

public class AmplitudeAnalyzer {

    private AudioData audio;

    public AmplitudeAnalyzer(AudioData audio) {
        this.audio = audio;
    }

    public double[] getAmplitudeData() {
        return audio.samples;
    }

    public double getTimeAtIndex(int index) {
        return index / (double) audio.sampleRate;
    }

    public int getSampleRate() {
        return audio.sampleRate;
    }
}
