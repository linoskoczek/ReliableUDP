package Server;

import PhilFTP2.FileManager;
import Utilities.FileUtility;

import java.io.File;

public class FileReceiver implements FileManager, Runnable {
    private File file = null;
    private String name, md5;
    private long size;
    private int speed; //in KB/s

    FileReceiver(String name, long size, String md5) {
        this.name = name;
        this.size = size;
        this.md5 = md5;

        createFile(name);
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

    @Override
    public void run() {

    }

}
