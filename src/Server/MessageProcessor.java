package Server;

import Utilities.MessageManager;

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

        switch(message[0]) {
            case "HAI!ME CLIENT":
                welcomeMessageAnswer();
                break;
            case "SENDING FILE":
                processSendingFile(message[1]);
                break;
            case "FILE CONTINENT":
                processFileContinent(message[1]);
                break;
            case "DISCONNECT":
                Server.session.disconnect();
                break;
            default:
                System.out.println("Unrecognized message arrived. Ignoring...");
        }
    }

    private void processSendingFile(String message) {
        String[] fileInformation = message.split(";");
        String name = fileInformation[0];
        String md5 = fileInformation[1];
        long size = Long.valueOf(fileInformation[1]);

        fileReceiver = new FileReceiver(name, size);
    }

    private void processFileContinent(String message) {
        if(fileReceiver == null) return;
    }

    void welcomeMessageAnswer() {
        String message = "HAI!ME SERVER";
        try {
            sendMessage(message, serverSocket, clientAddress, clientPort);
        } catch (IOException e) {
            System.err.println("Could not send WELCOME message to the client.");
        }
    }

    public boolean isNotOtherPerson(DatagramPacket packet) {
        return packet.getAddress() == clientAddress;
    }

    public void sendBusyMessage() {
        try {
            MessageManager.sendMessage("ME BUSY SORI", serverSocket, clientAddress, clientPort);
        } catch (IOException e) {
            System.err.println("Could not send BUSY message to the person who wanted to connect.");
        }
    }
}
