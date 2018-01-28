package Server;

import PhilFTP2.ProtocolStarter;
import Utilities.MessageManager;
import com.sun.istack.internal.NotNull;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {
    static ServerMessageProcessor serverMessageProcessor = null;
    static DatagramSocket serverSocket;
    static int port, speed;
    static boolean receiverRunning = true;
    static Session session = null;

    public static void main(String... args) {
        readArguments(args);
        serverSocket = createSocket();

        new ProtocolStarter(serverSocket, 500, 3);

        startReceiver();
    }

    private static void readArguments(@NotNull String[] args) {
        try {
            port = Integer.parseInt(args[0]);
            speed = Integer.parseInt(args[1]);
        } catch(IndexOutOfBoundsException e) {
            System.err.println("You should provide 2 arguments!\n1) port\n2) speed (in KB/s)");
            System.exit(1);
        }
    }

    private static DatagramSocket createSocket() {
        try {
            serverSocket = new DatagramSocket(port);
            System.out.println("Server's serverSocket opened on port " + serverSocket.getLocalPort());
            return serverSocket;
        } catch (SocketException e) {
            System.err.println("Could not open a serverSocket!");
            System.exit(1);
            return null;
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private static void startReceiver() {
        while(receiverRunning) {
            DatagramPacket packet = MessageManager.receiveMessage();
            receivedAction(packet);
        }
        System.out.println("Exiting...");
    }

    private static void receivedAction(DatagramPacket packet) {
        if(session == null || session.isTimedOut()) {
            openSession();
        }
        if (serverMessageProcessor == null) {
            createMessageProcessor(packet);
            connectSocketToThisSpecificClient(packet);
        }
        session.updateTime();
        serverMessageProcessor.processMessage(packet);
    }

    private static void openSession() {
        session = new Session(serverSocket);
        session.start();
    }

    private static void createMessageProcessor(DatagramPacket packet) {
        serverMessageProcessor = new ServerMessageProcessor(serverSocket, packet.getAddress(), packet.getPort());
    }

    private static void connectSocketToThisSpecificClient(DatagramPacket packet) {
        session.connectTo(packet.getAddress(), packet.getPort());
    }
}
