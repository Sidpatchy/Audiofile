package com.sidpatchy.audiofile.FSK.Decode;

import com.sidpatchy.audiofile.FSK.Encode.SimpleFSK;

import java.util.ArrayList;
import java.util.List;

public class SimpleFSKDecoder implements DecoderInterface {

    private final List<Integer> frequencyTable;

    public SimpleFSKDecoder(int minFrequency, int maxFrequency) {
        SimpleFSK fsk = new SimpleFSK();
        this.frequencyTable = fsk.generateFrequencyTable(minFrequency, maxFrequency, 2);
    }

    @Override
    public byte[] decodeFrequencies(List<Integer> frequencies, int tonesPerSegment) {
        List<Byte> byteList = new ArrayList<>();
        int bitsPerByte = 8;

        for (int i = 0; i < frequencies.size(); i += bitsPerByte) {
            byte decodedByte = 0;
            for (int j = 0; j < bitsPerByte; j++) {
                int frequency = frequencies.get(i + j);
                int bit = frequency == frequencyTable.get(1) ? 1 : 0;
                decodedByte = (byte) ((decodedByte << 1) | bit);
            }
            byteList.add(decodedByte);
        }

        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }

        return byteArray;
    }
}
