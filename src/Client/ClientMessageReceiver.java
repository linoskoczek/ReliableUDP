package Client;

import Utilities.MessageManager;

import java.net.DatagramPacket;

public class ClientMessageReceiver extends Thread {
    boolean receiverRunning = true;

    @Override
    public void run() {
        while (receiverRunning) {
            DatagramPacket packet = MessageManager.receiveMessage();
            processMessage(packet);
        }
        System.out.println("Exiting...");
    }

    private void processMessage(DatagramPacket packet) {
        processMessage(new String(packet.getData(), 0, packet.getLength()));
    }

    private void processMessage(String msg) {
        String[] message = msg.split(":");
        switch (message[2]) {
            case "HAI":
                reactOnMyWelcomeMessage();
                break;
            case "FLI":
                fileInformationAnswerReceived(message[4]);
                break;
            default:
                System.out.println("Unrecognized message arrived. Ignoring... CMD: " + message[2]);
        }
    }

    private void fileInformationAnswerReceived(String message) {
        String[] content = message.split(";");
        if (content[0].equals("KK")) {
            Client.fileSender.setSpeed(Integer.parseInt(content[1]));
            System.out.println("Speed changed to " + content[1]);
        }
    }

    private void reactOnMyWelcomeMessage() {
        Client.connection.connectionOpened = true;
    }
}
