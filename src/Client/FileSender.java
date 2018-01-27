package Client;

import PhilFTP2.FileManager;
import Utilities.FileUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

class FileSender implements FileManager, Runnable {
    private File file = null;
    private String name, md5;
    private long size;
    private volatile int speed = -1; //in KB/s

    FileSender(String name) throws FileNotFoundException {
        this.file = new File(name);
        this.name = name;

        if (!file.exists()) throw new FileNotFoundException();
        else {
            this.size = file.length();
            generateFileMD5();
        }
    }

    long getNumberOfParts() {
        return (long) Math.ceil(size / 1024);
    }

    String generateFileMD5() {
        md5 = FileUtility.calculateFileMD5(file);
        return md5;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void run() {
        while (speed == -1) {
        }
        byte[] array;
        try {
            array = Files.readAllBytes(file.toPath());
            System.out.println("Size expected:" + size);
            System.out.println("Size of byte array: " + array.length);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
