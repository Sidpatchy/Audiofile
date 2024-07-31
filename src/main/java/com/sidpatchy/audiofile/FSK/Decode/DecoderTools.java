package com.sidpatchy.audiofile.FSK.Decode;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

import org.jtransforms.fft.DoubleFFT_1D;

public class DecoderTools {

    /**
     * Reads the audio file and extracts frequencies played at each segment.
     *
     * @param filePath the path to the audio file
     * @param duration the duration of each tone in seconds
     * @param tonesPerSegment the number of tones played simultaneously
     * @return a list of detected frequencies
     */
    public List<Integer> extractFrequenciesFromAudioFile(String filePath, double duration, int tonesPerSegment) {
        final int SAMPLE_RATE = 44100;
        final int NUM_CHANNELS = 1;
        final int SAMPLE_SIZE_IN_BITS = 16;

        List<Integer> frequencies = new ArrayList<>();

        try {
            File file = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            double frameRate = format.getFrameRate();
            int frameSize = format.getFrameSize();

            byte[] audioBytes = new byte[(int) (frames * frameSize)];
            audioInputStream.read(audioBytes);

            int numSamplesPerSegment = (int) (SAMPLE_RATE * duration);
            int numSegments = audioBytes.length / (numSamplesPerSegment * frameSize);

            DoubleFFT_1D fft = new DoubleFFT_1D(numSamplesPerSegment);

            for (int i = 0; i < numSegments; i++) {
                byte[] segment = Arrays.copyOfRange(audioBytes, i * numSamplesPerSegment * frameSize,
                        (i + 1) * numSamplesPerSegment * frameSize);

                double[] samples = new double[numSamplesPerSegment];
                for (int j = 0; j < numSamplesPerSegment; j++) {
                    int sample = ((segment[2 * j + 1] << 8) | (segment[2 * j] & 0xFF));
                    samples[j] = sample / 32768.0; // Normalize
                }

                applyWindowFunction(samples);

                double[] fftData = new double[numSamplesPerSegment * 2];
                System.arraycopy(samples, 0, fftData, 0, numSamplesPerSegment);
                fft.realForwardFull(fftData);

                List<Integer> detectedFrequencies = detectFrequencies(fftData, numSamplesPerSegment, SAMPLE_RATE, tonesPerSegment);
                frequencies.addAll(detectedFrequencies);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return frequencies;
    }

    /**
     * Applies a Hamming window function to the samples.
     *
     * @param samples the array of samples to apply the window function to
     */
    private void applyWindowFunction(double[] samples) {
        for (int i = 0; i < samples.length; i++) {
            samples[i] *= 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (samples.length - 1));
        }
    }

    /**
     * Detects frequencies from FFT data.
     *
     * @param fftData the FFT data
     * @param numSamples the number of samples
     * @param sampleRate the sample rate
     * @param tonesPerSegment the number of tones played simultaneously
     * @return a list of detected frequencies
     */
    private List<Integer> detectFrequencies(double[] fftData, int numSamples, int sampleRate, int tonesPerSegment) {
        List<Integer> frequencies = new ArrayList<>();

        double binWidth = (double) sampleRate / numSamples;
        int[] peakIndices = findPeaks(fftData, numSamples, tonesPerSegment);

        for (int i : peakIndices) {
            double frequency = i * binWidth;
            frequencies.add((int) frequency);
        }

        return frequencies;
    }

    /**
     * Finds the peak indices in the FFT data.
     *
     * @param fftData the FFT data
     * @param numSamples the number of samples
     * @param tonesPerSegment the number of tones played simultaneously
     * @return an array of peak indices
     */
    private int[] findPeaks(double[] fftData, int numSamples, int tonesPerSegment) {
        double[] magnitudes = new double[numSamples / 2];
        for (int i = 0; i < numSamples / 2; i++) {
            double real = fftData[2 * i];
            double imag = fftData[2 * i + 1];
            magnitudes[i] = Math.sqrt(real * real + imag * imag);
        }

        int[] peakIndices = new int[tonesPerSegment];
        for (int i = 0; i < tonesPerSegment; i++) {
            int peakIndex = 0;
            for (int j = 1; j < magnitudes.length; j++) {
                if (magnitudes[j] > magnitudes[peakIndex]) {
                    peakIndex = j;
                }
            }
            peakIndices[i] = peakIndex;
            magnitudes[peakIndex] = 0; // Exclude this peak from future searches
        }

        return peakIndices;
    }
}
