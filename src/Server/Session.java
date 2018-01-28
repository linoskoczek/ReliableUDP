package Server;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Session extends Thread {
    private DatagramSocket serverSocket;
    private volatile long lastTimeSeen; //in miliseconds
    private volatile long timeout; //in miliseconds
    volatile boolean shouldExit = false;

    public Session(DatagramSocket serverSocket) {
        setTimeOut(5000); //5 seconds
        updateTime();
        this.serverSocket = serverSocket;
    }

    public Session(DatagramSocket serverSocket, long timeout) {
        if(timeout <= 0) {
            System.err.println("Timeout cannot be less or equal zero! Timeout set to 5000.");
            setTimeOut(5000);
        }
        else setTimeOut(timeout);
        updateTime();
        this.serverSocket = serverSocket;
    }

    void setTimeOut(long timeout) {
        this.timeout = timeout;
    }

    void updateTime() {
        lastTimeSeen = System.currentTimeMillis();
    }

    boolean isTimedOut() {
        return lastTimeSeen + timeout < System.currentTimeMillis();
    }

    void connectTo(InetAddress address, int port) {
        serverSocket.connect(address, port);
        System.out.println("Server's socket connected to " + address.getHostName() + ":" + port);
    }

    void disconnect() {
        System.out.println("Disconnecting from a client. Exiting.");
        serverSocket.disconnect();
        Server.serverMessageProcessor = null;
        shouldExit = true;
        Server.receiverRunning = false;
        System.exit(0);
    }

    @Override
    public void run() {
        while(!isTimedOut()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
        System.out.println("Timed out.");
        Server.receiverRunning = false;
        System.exit(0);
    }
}
