package PhilFTP2;

import java.io.File;
import java.io.IOException;

public interface FileManager {

    default void createFile(String name) {
        StringBuilder newName = new StringBuilder(name);
        File f = new File(newName.toString());
        while (f.exists()) {
            newName.append(" (copy)");
            f = new File(newName.toString());
        }
        try {
            if (!f.createNewFile()) throw new IOException();
        } catch (IOException e) {
            System.err.println("File could not be created! Check if you have write permissions.");
        }
    }


    String getName();

    long getSize();
}
