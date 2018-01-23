package PhilFTP2;

import Utilities.FileUtility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;

public class Sender {

    public static boolean sendSingleMessage(String cmd, String data, SocketAddress address) throws IOException {
        short ackId = AcknowledgeMaster.getInstance().getACKid();
        byte[] buffer = prepareMessage(ackId, cmd, data).getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address);

        for (int i = 0; i < ProtocolStarter.numOfTries; i++) {
            ProtocolStarter.socket.send(packet);
            if (waitFor(ackId)) return true;
        }
        System.err.println("Packet " + ackId + " couldn't be delivered :(");
        return false;
    }

    public static boolean waitFor(short ackId) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - ProtocolStarter.timeout <= startTime) {
            if (AcknowledgeMaster.getInstance().isAcknowledged(ackId)) {
                return true;
            }
        }
        return false;
    }

    public static void sendAck(short ackId, SocketAddress address) {
        try {
            sendSingleMessage("ACK", "", address);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not send ACK message!");
        }
    }

    public static String getChecksum(String txt) {
        return FileUtility.calculateMD5(txt.getBytes());
    }

    public static String prepareMessage(short ackId, String cmd, String data) {
        return (new StringBuilder())
                .append(ackId)
                .append(":")
                .append(getChecksum(ackId + cmd + data)).append(":")
                .append(cmd)
                .append(data.length())
                .append(":")
                .append(data)
                .toString();
    }

}
