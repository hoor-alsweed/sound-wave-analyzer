package soundproject;

import javax.sound.sampled.*;
import java.io.File;

public class Audioloader {

    public static AudioData load(File file) {

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();

            int sampleRate = (int) format.getSampleRate();
            int channels = format.getChannels();

            byte[] bytes = stream.readAllBytes();
            double[] samples = new double[bytes.length / 2];

            int idx = 0;
            for (int i = 0; i < bytes.length; i += 2) {
                short val = (short) ((bytes[i + 1] << 8) | (bytes[i] & 0xff));
                samples[idx++] = val / 32768.0;
            }

            return new AudioData(samples, sampleRate, channels);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
