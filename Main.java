package soundproject;

import soundproject.AudioData;
import soundproject.Audioloader;
import soundproject.amplitude.AmplitudeAnalyzer;
import soundproject.amplitude.AmplitudePanel;
import soundproject.fft.FFTPanel;
import soundproject.fft.FFTProcessor;
import soundproject.power.PowerSpectrum;
import soundproject.power.SpectrumPanel;
import soundproject.filter.FilterView;
import soundproject.beat.BeatPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {

    private static AudioData audioData;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        Color bgMain   = new Color(10, 15, 28);
        Color bgLayer  = new Color(18, 28, 44);
        Color bgTabs   = new Color(14, 22, 38);

        Color textMain = new Color(175, 185, 195);
        Color textSoft = new Color(145, 155, 165);
        Color accent   = new Color(70, 110, 150);

        JFrame frame = new JFrame("Sound Wave Analyzer");
        frame.setSize(1100, 650);
        frame.setMinimumSize(new Dimension(1000, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(bgMain);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(bgTabs);
        tabs.setForeground(textMain);

        tabs.add("Amplitude", placeholder(bgLayer, textMain));
        tabs.add("FFT",       placeholder(bgLayer, textMain));
        tabs.add("Power",     placeholder(bgLayer, textMain));
        tabs.add("Filter",    placeholder(bgLayer, textMain));
        tabs.add("Beats",     placeholder(bgLayer, textMain));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        infoArea.setBackground(bgLayer);
        infoArea.setForeground(textMain);
        infoArea.setCaretColor(textMain);

        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.getViewport().setBackground(bgLayer);
        infoScroll.setPreferredSize(new Dimension(300, 0));
        infoScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(accent),
                "Audio Info",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                textMain
        ));

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(bgTabs);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(textMain);
        fileMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JMenuItem loadItem = new JMenuItem("Load Audio");
        JMenuItem exitItem = new JMenuItem("Exit");

        loadItem.setForeground(textMain);
        exitItem.setForeground(textMain);
        loadItem.setBackground(bgLayer);
        exitItem.setBackground(bgLayer);

        fileMenu.add(loadItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);

        loadItem.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(
                    new javax.swing.filechooser.FileNameExtensionFilter("WAV Files", "wav")
            );

            int result = chooser.showOpenDialog(frame);
            if (result != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();
            audioData = Audioloader.load(file);

            if (audioData == null) {
                JOptionPane.showMessageDialog(frame, "Error loading audio.");
                return;
            }

            FFTProcessor fft = new FFTProcessor(audioData);

            tabs.setComponentAt(0, new AmplitudePanel(new AmplitudeAnalyzer(audioData)));
            tabs.setComponentAt(1, new FFTPanel(audioData));

            PowerSpectrum spectrum = new PowerSpectrum(fft);
            tabs.setComponentAt(2, new SpectrumPanel(spectrum));

            tabs.setComponentAt(3, new FilterView(audioData));
            tabs.setComponentAt(4, new BeatPanel(audioData));

            infoArea.setText("");
            infoArea.append("File: " + file.getName() + "\n");
            infoArea.append("Sample Rate: " + audioData.sampleRate + " Hz\n");
            infoArea.append("Channels: " + audioData.numChannels + "\n");
            infoArea.append("Duration: " +
                    String.format("%.2f", audioData.getDurationSeconds()) + " sec\n");

            double peak = spectrum.getMaxFrequency();
            infoArea.append("Peak Frequency: " +
                    String.format("%.2f", peak) + " Hz\n");

            if (peak < 300)
                infoArea.append("Type: Bass\n");
            else if (peak < 2000)
                infoArea.append("Type: Speech / Music\n");
            else
                infoArea.append("Type: Sharp\n");
        });

        exitItem.addActionListener(e -> System.exit(0));

        frame.add(tabs, BorderLayout.CENTER);
        frame.add(infoScroll, BorderLayout.EAST);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel placeholder(Color bg, Color text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);

        JLabel lbl = new JLabel("Load audio first", SwingConstants.CENTER);
        lbl.setForeground(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        p.add(lbl, BorderLayout.CENTER);
        return p;
    }
}