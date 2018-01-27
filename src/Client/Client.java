package Client;

import PhilFTP2.ProtocolStarter;
import Utilities.FileUtility;
import com.sun.istack.internal.NotNull;

import java.io.*;
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

    public static void main(String... args) {
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
        //sendFileInformation();

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
            File fileToSend = new File(file);
            if(!fileToSend.exists()) throw new FileNotFoundException();
            String cmd = "FLI";
            String message = fileToSend.getName() + ";"
                    + fileToSend.length() + ";"
                    + FileUtility.calculateMD5(file);
            connection.sendMessage(cmd, message);
        } catch (FileNotFoundException e) {
            System.err.println("File given in argument cannot be found!");
            System.exit(1);
        }
    }
}
