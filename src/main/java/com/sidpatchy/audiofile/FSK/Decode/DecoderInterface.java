package com.sidpatchy.audiofile.FSK.Decode;

import java.util.List;

public interface DecoderInterface {
    byte[] decodeFrequencies(List<Integer> frequencies, int tonesPerSegment);
}
