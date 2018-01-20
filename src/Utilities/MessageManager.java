package Utilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MessageManager {
    public static void sendMessage(String message, DatagramSocket socket, InetAddress destinationAddress, int destinationPort) throws IOException {
        System.out.println("[SND]:" + message);
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, destinationAddress, destinationPort);
        socket.send(packet);
    }

    public static String receiveMessage(DatagramSocket clientSocket) throws SocketException,IOException {
        DatagramPacket packet = receiveMessageInPacket(clientSocket);
        return new String(packet.getData(), 0, packet.getLength());
    }

    public static DatagramPacket receiveMessageInPacket(DatagramSocket clientSocket) throws SocketException,IOException {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        clientSocket.receive(packet);
        return packet;
    }
}
