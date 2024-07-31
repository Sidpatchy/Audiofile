package com.sidpatchy.audiofile.FSK.Encode;

import java.util.ArrayList;
import java.util.List;

public class SimpleFSK implements EncoderInterface {

    @Override
    public List<Integer> generateFrequencyTable(int minFrequency, int maxFrequency) {
        return generateFrequencyTable(minFrequency, maxFrequency, 0);
    }

    @Override
    public List<Integer> byteListToFrequencyList(byte[] data) {
        return byteListToFrequencyList(data, 0);
    }

    public List<Integer> generateFrequencyTable(int minFrequency, int maxFrequency, int numberOfFrequencies) {
        List<Integer> frequencies = new ArrayList<>();
        frequencies.add(minFrequency); // Frequency for binary '0'
        frequencies.add(maxFrequency); // Frequency for binary '1'
        return frequencies;
    }

    public List<Integer> byteListToFrequencyList(byte[] data, int numberOfFrequencies) {
        List<Integer> frequencies = generateFrequencyTable(200, 1800); // Example frequencies
        List<Integer> outputFrequencies = new ArrayList<>();

        for (byte b : data) {
            for (int i = 7; i >= 0; i--) { // Process each bit
                int bit = (b >> i) & 1;
                outputFrequencies.add(frequencies.get(bit));
            }
        }

        return outputFrequencies;
    }
}
