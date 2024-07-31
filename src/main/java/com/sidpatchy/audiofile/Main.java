package com.sidpatchy.audiofile;

import com.sidpatchy.audiofile.FSK.Decode.DecoderTools;
import com.sidpatchy.audiofile.FSK.Decode.ModifiedFSKDecoder;
import com.sidpatchy.audiofile.FSK.Encode.EncoderInterface;
import com.sidpatchy.audiofile.FSK.Encode.EncoderTools;
import com.sidpatchy.audiofile.FSK.Encode.ModifiedFSK;

import java.util.List;

public class Main {

    private static FileUtils fileUtils = new FileUtils();
    //private static AudioInterface fsk = new SimpleFSK();
    private static EncoderInterface fsk = new ModifiedFSK();
    private static EncoderTools encoderTools = new EncoderTools();

    public static void main(String[] args) {
        //encoderTest();
        decoderTest();
    }

    private static void encoderTest() {
        String inputFilePath = "/home/phoenix/Downloads/menards.ico"; // Replace with the path to your input file
        String outputAudioFilePath = "/home/phoenix/Downloads/menards.wav"; // Replace with your desired output audio file path

        byte[] inputBytes = fileUtils.getFileAsBytes(inputFilePath);

        List<Integer> frequencies = fsk.byteListToFrequencyList(inputBytes, 1);

        encoderTools.saveFrequenciesToAudioFile(frequencies, outputAudioFilePath, 0.5, 1);
    }

    private static void decoderTest() {
        String encodedAudioFilePath = "/home/phoenix/Downloads/menards.wav"; // Path to the encoded audio file
        String outputFilePath = "/home/phoenix/Downloads/menards-decodedreal.ico"; // Path to the decoded output file

        // Extract frequencies from the audio file
        DecoderTools decoder = new DecoderTools();
        List<Integer> frequencies = decoder.extractFrequenciesFromAudioFile(encodedAudioFilePath, 0.5, 1);
        for (int frequency : frequencies) {
            System.out.println("Detected Frequency: " + frequency);
        }

        // Decode the frequencies back to byte data
        ModifiedFSKDecoder fskDecoder = new ModifiedFSKDecoder(200, 18000, 1);
        byte[] decodedBytes = fskDecoder.decodeFrequencies(frequencies, 1);

        // Save the decoded byte data to a file
        fileUtils.writeBytesToFile(outputFilePath, decodedBytes);
    }
}
