package com.sidpatchy.audiofile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public byte[] getFileAsBytes(String path) {
        byte[] fileBytes = null;

        try {
            fileBytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileBytes;
    }

    public void writeBytesToFile(String path, byte[] bytes) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
