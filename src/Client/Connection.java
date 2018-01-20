package Client;

import Utilities.MessageManager;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Connection extends MessageManager {
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort;

    Connection(DatagramSocket clientSocket, String serverAddress, int serverPort) throws IOException {
        this.clientSocket = clientSocket;
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;

        welcome();
    }

    private void welcome() throws IOException {
        boolean success = false;
        for(int i = 0; i < 3; i++) {
            String message = "HAI!ME CLIENT";
            sendMessage(message);
            String answer = receiveMessage(clientSocket);

            if (answer.equals("HAI!ME SERVER")) {
                success = true;
                break;
            }
        }
        if(!success) System.err.println("Could not send WELCOME message to the server.");
    }

    void sendMessage(String message) throws IOException {
        sendMessage(message, clientSocket, serverAddress, serverPort);
    }

    public void disconnect() {
        try {
            sendMessage("DISCONNECT");
        } catch (IOException e) {
            System.err.println("Could not send DISCONNECT message.");
        }
    }
}
