package Client;

import PhilFTP2.FileManager;
import Utilities.FileUtility;

import java.io.File;
import java.io.FileNotFoundException;

class FileSender implements FileManager, Runnable {
    private File file = null;
    private String name, md5;
    private long size;
    private volatile int speed = -1; //in KB/s
    private byte[] fileArray;
    private volatile int currentPart = 0;

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
        return (long) Math.ceil(size / 1024) + 1;
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
    private void waitUntilAnswerWithSpeedIsReceived() {
        while (speed == -1) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void sendPartOfFile(int partNumber) {
        String cmd = "CNT";

        Client.connection.sendMessage(cmd, String.valueOf(partNumber) + ";" + getPartOfFileAsString(partNumber));
    }

    private String getPartOfFileAsString(int partNumber) {
        int maxContentSize = partNumber * 1024 + 1024;
        if (maxContentSize > fileArray.length) maxContentSize = fileArray.length;

        return new String(fileArray, partNumber * 1024, maxContentSize - partNumber * 1024);
    }

    private synchronized int getAndIncrementCurrentPart() {
        return currentPart++;
    }

    private void sendFinishMessage() {
        String cmd = "FSF";

        Client.connection.sendMessage(cmd, "1");
        Client.connection.connectionOpened = false;
    }

    @Override
    public void run() {
        waitUntilAnswerWithSpeedIsReceived();
        System.out.println("Starting to send file. Gonna send file with size " + size + " in " + getNumberOfParts() + " parts, 1KB each.");
        fileArray = FileUtility.getByteArrayOfFile(file);
        long numberOfParts = getNumberOfParts();
        while (currentPart < numberOfParts) {
            sendPartOfFile(getAndIncrementCurrentPart());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sendFinishMessage();
    }
}
