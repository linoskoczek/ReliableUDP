package Server;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import static Utilities.MessageManager.*;

public class Server {
    static MessageProcessor messageProcessor = null;
    static DatagramSocket serverSocket;
    static int port, speed;
    static boolean receiverRunning = true;
    static Session session = null;

    public static void main(String... args) {
        readArguments(args);
        serverSocket = createSocket();
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
            try {
                DatagramPacket packet = receiveMessageInPacket(serverSocket);
                receivedAction(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Exiting...");
    }

    private static void receivedAction(DatagramPacket packet) {
        if(session == null || session.isTimedOut()) {
            openSession();
        }
        if(messageProcessor == null) {
            createMessageProcessor(packet);
            connectSocketToThisSpecificClient(packet);
        }
        session.updateTime();
        messageProcessor.processMessage(packet);
    }

    private static void openSession() {
        session = new Session(serverSocket);
        session.start();
    }

    private static void createMessageProcessor(DatagramPacket packet) {
        messageProcessor = new MessageProcessor(serverSocket, packet.getAddress(), packet.getPort());
    }

    private static void connectSocketToThisSpecificClient(DatagramPacket packet) {
        session.connectTo(new InetSocketAddress(packet.getAddress(), packet.getPort()));
    }
}
