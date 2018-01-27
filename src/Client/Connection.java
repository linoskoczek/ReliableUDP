package Client;

import Utilities.MessageManager;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Connection extends MessageManager {
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort;
    volatile boolean connectionConfirmed = false;

    Connection(DatagramSocket clientSocket, String serverAddress, int serverPort) throws IOException {
        this.clientSocket = clientSocket;
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    void welcome() throws IOException {
        System.out.print("Sending WELCOME message... ");
        String cmd = "HAI";
        String message = "ME CLIENT";

        sendMessage(cmd, message, serverAddress, serverPort);
        System.out.print("SENT! Waiting for answer... ");
        while (!connectionConfirmed) {
            //waiting for answer
        }
        System.out.println("=== WELCOME ===!");
    }

    public void confirmConnection() {
        connectionConfirmed = true;
    }

    public void disconnect() {
        sendMessage("DSC", "DISCONNECT");
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
