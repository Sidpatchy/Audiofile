package com.sidpatchy.audiofile.FSK.Encode;

import java.io.*;
import java.util.List;

public class EncoderTools {
    /**
     * Saves the frequencies to an audio file, playing each set of frequencies for the specified duration.
     *
     * @param frequencies the list of frequencies to save
     * @param filePath the path to the audio file
     * @param duration the duration of each tone in seconds
     * @param tonesPerSegment the number of tones to play simultaneously
     */
    public void saveFrequenciesToAudioFile(List<Integer> frequencies, String filePath, double duration, int tonesPerSegment) {
        final int SAMPLE_RATE = 44100;
        final int NUM_CHANNELS = 1;
        final int SAMPLE_SIZE_IN_BITS = 16;

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            int totalAudioLen = (int) (frequencies.size() / tonesPerSegment * SAMPLE_RATE * duration * NUM_CHANNELS * SAMPLE_SIZE_IN_BITS / 8);
            int totalDataLen = totalAudioLen + 36;

            writeWavHeader(fos, totalAudioLen, totalDataLen, SAMPLE_RATE, NUM_CHANNELS, SAMPLE_SIZE_IN_BITS);

            for (int i = 0; i < frequencies.size(); i += tonesPerSegment) {
                List<Integer> frequencySet = frequencies.subList(i, i + tonesPerSegment);
                byte[] tone = generateCompositeTone(frequencySet, duration, SAMPLE_RATE);
                fos.write(tone);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a composite tone of given frequencies and duration.
     *
     * @param frequencies the list of frequencies to combine
     * @param duration the duration of the tone in seconds
     * @param sampleRate the sample rate
     * @return a byte array containing the composite tone
     */
    private byte[] generateCompositeTone(List<Integer> frequencies, double duration, int sampleRate) {
        int numSamples = (int) (duration * sampleRate);
        byte[] buffer = new byte[2 * numSamples];
        double[] angles = new double[frequencies.size()];

        for (int i = 0; i < frequencies.size(); i++) {
            angles[i] = 2.0 * Math.PI * frequencies.get(i) / sampleRate;
        }

        for (int i = 0; i < numSamples; i++) {
            double sampleValue = 0;
            for (double angle : angles) {
                sampleValue += Math.sin(i * angle);
            }
            // Normalize the sample value to prevent clipping
            sampleValue = sampleValue / frequencies.size();
            short sample = (short) (sampleValue * Short.MAX_VALUE);
            buffer[2 * i] = (byte) (sample & 0xFF);
            buffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        return buffer;
    }

    /**
     * Writes the WAV file header.
     *
     * @param out the output stream to write the header to
     * @param totalAudioLen the total length of the audio data
     * @param totalDataLen the total length of the data chunk
     * @param sampleRate the sample rate
     * @param channels the number of channels
     * @param bitsPerSample the number of bits per sample
     * @throws IOException if an I/O error occurs
     */
    private void writeWavHeader(OutputStream out, int totalAudioLen, int totalDataLen, int sampleRate, int channels, int bitsPerSample) throws IOException {
        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * bitsPerSample / 8);  // block align
        header[33] = 0;
        header[34] = (byte) bitsPerSample;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
}
