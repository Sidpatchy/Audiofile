package com.sidpatchy.audiofile.FSK.Decode;

import com.sidpatchy.audiofile.FSK.Encode.ModifiedFSK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifiedFSKDecoder implements DecoderInterface {

    private List<Integer> frequencyTable;
    private Map<Integer, Byte> frequencyToByteMap;

    public ModifiedFSKDecoder(int minFrequency, int maxFrequency, int tonesPerSegment) {
        ModifiedFSK fsk = new ModifiedFSK();
        this.frequencyTable = fsk.generateFrequencyTable(minFrequency, maxFrequency, 256 * tonesPerSegment);

        this.frequencyToByteMap = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < tonesPerSegment; j++) {
                int frequency = frequencyTable.get(i * tonesPerSegment + j);
                frequencyToByteMap.put(frequency, (byte) i);
            }
        }
    }

    @Override
    public byte[] decodeFrequencies(List<Integer> frequencies, int tonesPerSegment) {
        List<Byte> byteList = new ArrayList<>();

        for (int i = 0; i < frequencies.size(); i += tonesPerSegment) {
            List<Integer> segment = frequencies.subList(i, i + tonesPerSegment);
            Byte decodedByte = decodeFrequencySegment(segment);
            if (decodedByte != null) {
                byteList.add(decodedByte);
            }
        }

        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }

        return byteArray;
    }

    private Byte decodeFrequencySegment(List<Integer> segment) {
        for (int frequency : segment) {
            if (frequencyToByteMap.containsKey(frequency)) {
                return frequencyToByteMap.get(frequency);
            }
        }
        return null;
    }
}
