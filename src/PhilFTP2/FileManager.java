package PhilFTP2;

import java.io.File;
import java.io.IOException;

public interface FileManager {

    default String createFile(String name) {
        StringBuilder newName = new StringBuilder(name);
        File f = new File(newName.toString());
        while (f.exists()) {
            String extension = getFileExtension(name);
            newName.replace(newName.length() - extension.length() - 1, newName.length(), " (copy)." + extension);
            f = new File(newName.toString());
        }
        try {
            if (!f.createNewFile()) throw new IOException();
        } catch (IOException e) {
            System.err.println("File could not be created! Check if you have write permissions.");
        }
        return newName.toString();
    }

    default String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }


    String getName();

    long getSize();
}
