package com.sidpatchy.audiofile;

import com.sidpatchy.audiofile.FSK.Decode.DecoderTools;
import com.sidpatchy.audiofile.FSK.Decode.ModifiedFSKDecoder;
import com.sidpatchy.audiofile.FSK.Encode.EncoderInterface;
import com.sidpatchy.audiofile.FSK.Encode.EncoderTools;
import com.sidpatchy.audiofile.FSK.Encode.ModifiedFSK;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static FileUtils fileUtils = new FileUtils();
    //private static AudioInterface fsk = new SimpleFSK();
    private static EncoderInterface fsk = new ModifiedFSK();
    private static EncoderTools encoderTools = new EncoderTools();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Do you want to run the encoder (e) or decoder (d)? ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("e")) {
            encoderTest(scanner);
        } else if (choice.equalsIgnoreCase("d")) {
            decoderTest(scanner);
        } else {
            System.out.println("Invalid choice. Please enter 'e' for encoder or 'd' for decoder.");
        }
    }

    private static void encoderTest(Scanner scanner) {
        System.out.print("Enter the path to your input file: ");
        String inputFilePath = scanner.nextLine();

        System.out.print("Enter the desired output audio file path: ");
        String outputAudioFilePath = scanner.nextLine();

        byte[] inputBytes = fileUtils.getFileAsBytes(inputFilePath);

        List<Integer> frequencies = fsk.byteListToFrequencyList(inputBytes, 1);

        encoderTools.saveFrequenciesToAudioFile(frequencies, outputAudioFilePath, 0.5, 1);

        System.out.println("Encoding complete. Output saved to: " + outputAudioFilePath);
    }

    private static void decoderTest(Scanner scanner) {
        System.out.print("Enter the path to the encoded audio file: ");
        String encodedAudioFilePath = scanner.nextLine();

        System.out.print("Enter the desired output file path: ");
        String outputFilePath = scanner.nextLine();

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

        System.out.println("Decoding complete. Output saved to: " + outputFilePath);
    }
}
