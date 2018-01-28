package Server;

import PhilFTP2.FileManager;
import Utilities.FileUtility;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static Utilities.MessageManager.sendMessage;

public class FileReceiver implements FileManager {
    private File file = null;
    private String name, md5;
    private long size;
    private int speed; //in KB/s
    private Writer writer;
    private long currentPart = 0;

    FileReceiver(String name, long size, String md5) {
        this.size = size;
        this.md5 = md5;
        this.name = createFile(name);
        file = new File(this.name);
        openFileForWriting();
    }

    private void openFileForWriting() {
        try {
            writer = new PrintWriter(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String generateFileMD5() throws Exception {
        if (md5 == null)
            this.md5 = FileUtility.calculateFileMD5(file);
        else {
            String newMd5 = FileUtility.calculateFileMD5(file);
            if (!newMd5.equals(md5))
                throw new Exception("Previous MD5 is different than current one!");
        }
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

    public void write(String message) {
        String[] content = message.split(";");

        if (Long.parseLong(content[0]) > currentPart + 1) {
            askForPacket(currentPart);
        } else {
            try {
                for (int i = 1; i < content.length; i++) {
                    if (i != 1) writer.write(";");
                    writer.write(content[i]);
                }
            } catch (IOException e) {
                System.err.println("Something wrong while writing!");
                e.printStackTrace();
            }
            currentPart++;
        }
    }

    private void askForPacket(long currentPart) {
        String cmd = "PRP";
        String message = String.valueOf(currentPart);
        try {
            sendMessage(cmd, message, Server.serverMessageProcessor.clientAddress, Server.serverMessageProcessor.clientPort);
        } catch (IOException e) {
            System.err.println("Could not send WELCOME message to the client.");
        }
    }

    public void finish() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("File has been received. Checking MD5... ");
        try {
            generateFileMD5();
            System.out.println("SUCCESS");
        } catch (Exception e) {
            System.out.println("FAILED");
        }
    }
}
