package PhilFTP2;

import Utilities.FileUtility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.PortUnreachableException;
import java.util.Arrays;
import java.util.LinkedList;

public class Receiver extends Thread {
    public static volatile LinkedList<DatagramPacket> received = new LinkedList<>();

    Receiver() {
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            byte[] buffer = new byte[1280];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                ProtocolStarter.socket.receive(packet);
            } catch (PortUnreachableException e) {
                System.err.println("Server port is unreachable. Exiting...");
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] splitted = new String(packet.getData(), 0, packet.getLength()).split(":");

            System.out.println("IN: " + Arrays.toString(splitted));
            if (splitted.length == 5) {
                if (FileUtility.getChecksum(splitted[0] + splitted[2] + splitted[4]).equals(splitted[1])) {
                    try {
                        switch (splitted[2]) {
                            case "ACK":
                                AcknowledgeMaster.getInstance().acknowledge(Short.parseShort(splitted[4]));
                                break;
                            default:
                                Sender.sendAck(Short.parseShort(splitted[0]), packet.getSocketAddress());
                                if (splitted[2].length() == 3 && !received.contains(packet)) received.push(packet);
                        }

                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        System.out.println("Some corrupted data received...");
                    }
                } else {
                    System.out.println("MD5 hash of received message does not match!");
                }
            } else {
                System.out.println("Received packet is incorrect!");
            }
        }
    }
}
