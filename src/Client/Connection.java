package Client;

import Utilities.MessageManager;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Connection extends MessageManager {
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort;
    volatile boolean connectionOpened = false;

    Connection(DatagramSocket clientSocket, String serverAddress, int serverPort) throws IOException {
        this.clientSocket = clientSocket;
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    void welcome() throws IOException {
        String cmd = "HAI";
        String message = "ME CLIENT";

        sendMessage(cmd, message, serverAddress, serverPort);
        while (!connectionOpened) {
            //waiting for answer
        }
    }

    public void disconnect() {
        sendMessage("DSC", "DISCONNECT");
        System.exit(0);
    }

    public void sendMessage(String cmd, String data) {
        try {
            sendMessage(cmd, data, serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not send a message:");
            System.err.println("COMMAND:" + cmd);
            System.err.println("DATA:" + data);
        }
    }
}
