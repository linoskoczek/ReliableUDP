package PhilFTP2;

import java.net.DatagramPacket;
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

            String[] splitted = new String(packet.getData(), 0, packet.getLength()).split(":");
            if (splitted.length == 5) {
                try {
                    switch (splitted[2]) {
                        case "ACK":
                            AcknowledgeMaster.getInstance().acknowledge(Short.parseShort(splitted[0]));
                            break;
                        default:
                            Sender.sendAck(Short.parseShort(splitted[0]), packet.getSocketAddress());
                            if (splitted[2].length() == 3 && !received.contains(packet)) received.push(packet);
                    }

                } catch (ArrayIndexOutOfBoundsException ignored) {
                    System.out.println("Some corrupted data received...");
                }
            }
        }
    }
}
