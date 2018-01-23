package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static Utilities.MessageManager.sendMessage;

public class MessageProcessor {
    DatagramSocket serverSocket;
    InetAddress clientAddress;
    FileReceiver fileReceiver;
    int clientPort;

    public MessageProcessor(DatagramSocket serverSocket, InetAddress clientAddress, int clientPort) {
        this.serverSocket = serverSocket;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    void processMessage(DatagramPacket packet) {
        processMessage(new String(packet.getData(), 0, packet.getLength()));
    }

    void processMessage(String msg) {
        System.out.println("[RCV] " + msg);
        String[] message = msg.split(":"); //todo check with ':' as a file content or it's name

        switch (message[2]) {
            case "HAI":
                welcomeMessageAnswer();
                break;
            case "FLI":
                processFileInformation(message[4]);
                break;
            case "CNT":
                processFileContinent(message[4]);
                break;
            case "DSC":
                Server.session.disconnect();
                break;
            default:
                System.out.println("Unrecognized message arrived. Ignoring... CMD: " + message[2]);
        }
    }

    private void processFileInformation(String message) {
        String[] fileInformation = message.split(";");
        String name = fileInformation[0];
        String md5 = fileInformation[1];
        long size = Long.valueOf(fileInformation[1]);

        fileReceiver = new FileReceiver(name, size, md5);
    }

    private void processFileContinent(String message) {
        if(fileReceiver == null) return;
    }

    void welcomeMessageAnswer() {
        String cmd = "HAI";
        String message = "ME SERVER";
        try {
            sendMessage(cmd, message, clientAddress, clientPort);
        } catch (IOException e) {
            System.err.println("Could not send WELCOME message to the client.");
        }
    }
}
