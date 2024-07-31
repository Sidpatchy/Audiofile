package com.sidpatchy.audiofile.FSK.Encode;

import java.util.ArrayList;
import java.util.List;

public class ModifiedFSK implements EncoderInterface {
    @Override
    public List<Integer> generateFrequencyTable(int minFrequency, int maxFrequency) {
        return generateFrequencyTable(minFrequency, maxFrequency, 256);
    }

    @Override
    public List<Integer> byteListToFrequencyList(byte[] data) {
        return byteListToFrequencyList(data, 1);
    }

    /**
     * Generates a lookup table determining the value of frequencies the program should listen for in a file.
     *
     * @param minFrequency Minimum frequency value
     * @param maxFrequency Maximum frequency value
     * @param numberOfFrequencies Number of frequencies to generate
     * @return List of generated frequencies
     */
    public List<Integer> generateFrequencyTable(int minFrequency, int maxFrequency, int numberOfFrequencies) {
        List<Integer> frequencies = new ArrayList<>();
        double step = (maxFrequency - minFrequency) / (double) (numberOfFrequencies - 1);

        for (int i = 0; i < numberOfFrequencies; i++) {
            int frequency = (int) Math.round(minFrequency + i * step);
            // Ensure the frequency is even
            if (frequency % 2 != 0) {
                frequency++;
            }
            frequencies.add(frequency);
        }

        return frequencies;
    }


    public List<Integer> byteListToFrequencyList(byte[] bytes, int tonesPerSegment) {
        List<Integer> frequencyTable = generateFrequencyTable(200, 18000, 256 * tonesPerSegment); // Adjusted for multiple tones
        List<Integer> outputFrequencies = new ArrayList<>();

        for (byte b : bytes) {
            int unsignedByte = Byte.toUnsignedInt(b);
            for (int i = 0; i < tonesPerSegment; i++) {
                int frequency = frequencyTable.get(unsignedByte * tonesPerSegment + i);
                outputFrequencies.add(frequency);
                System.out.println("Byte: " + unsignedByte + ", Frequency: " + frequency); // Debug statement
            }
        }

        return outputFrequencies;
    }
}
