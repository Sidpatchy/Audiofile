package com.sidpatchy.audiofile.FSK.Encode;

import java.util.List;

public interface EncoderInterface {
    public List<Integer> generateFrequencyTable(int minFrequency, int maxFrequency);
    public List<Integer> byteListToFrequencyList(byte[] data);

    public List<Integer> generateFrequencyTable(int minFrequency, int maxFrequency, int numberOfFrequencies);
    public List<Integer> byteListToFrequencyList(byte[] data, int numberOfFrequencies);
}
