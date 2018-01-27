package PhilFTP2;

import java.io.IOException;
import java.net.DatagramPacket;
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] splitted = new String(packet.getData(), 0, packet.getLength()).split(":");

            System.out.println("IN: " + Arrays.toString(splitted));
            //todo check MD5 of message
            if (splitted.length == 5) {
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
                System.out.println("Received packet is incomplete");
            }
        }
    }
}
