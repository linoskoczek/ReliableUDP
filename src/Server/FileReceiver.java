package Server;

public class FileReceiver {
    private String name;
    private long size;
    String md5;

    public FileReceiver(String name, long size, String md5) {
        this.name = name;
        this.size = size;
        this.md5 = md5;
    }
}
