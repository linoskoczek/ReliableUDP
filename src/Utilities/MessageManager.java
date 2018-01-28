package Utilities;

import PhilFTP2.Receiver;
import PhilFTP2.Sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class MessageManager {
    public static void sendMessage(String cmd, String data, InetAddress destinationAddress, int destinationPort) throws IOException {
        Sender.sendSingleMessage(cmd, data, new InetSocketAddress(destinationAddress, destinationPort));
    }

    public static DatagramPacket receiveMessage() {
        while (Receiver.received.isEmpty()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //waiting to get any response
        }
        return Receiver.received.pollLast();
    }
}
