package Client;

import PhilFTP2.ProtocolStarter;
import com.sun.istack.internal.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Client {
    static DatagramSocket clientSocket;
    static String serverAddress, file;
    static Connection connection;
    static int serverPort;
    static InputStream in = null;
    static OutputStream out = null;
    static ClientMessageReceiver clientMessageReceiver;
    static FileSender fileSender;

    public static void main(String... args) throws InterruptedException {
        readArguments(args);
        clientSocket = createSocket();

        clientMessageReceiver = new ClientMessageReceiver();
        clientMessageReceiver.start();
        new ProtocolStarter(clientSocket, 500, 3);

        connection = connectToServer();
        try {
            connection.welcome();
        } catch (IOException e) {
            System.out.println("WELCOME failed");
        }
        sendFileInformation();
        Thread sender = new Thread(fileSender);
        sender.start();

        while (connection.connectionOpened) {
            Thread.sleep(50);
        }

        connection.disconnect();
    }

    private static void readArguments(@NotNull String[] args) {
        try {
            serverAddress = args[0];
            serverPort = Integer.parseInt(args[1]);
            file = args[2];
        } catch(IndexOutOfBoundsException e) {
            System.err.println("You should provide 3 arguments!\n1) server serverAddress\n2) serverPort\n3) file name");
            System.exit(1);
        }
    }

    private static DatagramSocket createSocket() {
        try {
            clientSocket = new DatagramSocket();
            System.out.println("Client's socket opened on serverPort " + clientSocket.getLocalPort());
            clientSocket.connect(new InetSocketAddress(serverAddress, serverPort));
            return clientSocket;
        } catch (SocketException e) {
            System.err.println("Could not open a clientSocket!");
            System.exit(1);
            return null;
        }
    }

    private static Connection connectToServer() {
        try {
            return new Connection(clientSocket, serverAddress, serverPort);
        } catch (IOException e) {
            System.err.println("Could not connect to server!");
            System.exit(1);
            return null;
        }
    }

    private static void sendFileInformation() {
        try {
            fileSender = new FileSender(file);

            String cmd = "FLI";
            String message = fileSender.getName() + ";"
                    + fileSender.getSize() + ";"
                    + fileSender.getNumberOfParts() + ";"
                    + fileSender.generateFileMD5();
            connection.sendMessage(cmd, message);
        } catch (FileNotFoundException e) {
            System.err.println("File given in argument cannot be found!");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
