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
        System.out.println("[RCV] " + msg);
        String[] message = msg.split(":"); //todo check with ':' as a file content or it's name

        switch (message[2]) {
            case "HAI":
                reactOnMyWelcomeMessage();
                break;
            default:
                System.out.println("Unrecognized message arrived. Ignoring... CMD: " + message[2]);
        }
    }

    private void reactOnMyWelcomeMessage() {
        Client.connection.connectionConfirmed = true;
    }
}
