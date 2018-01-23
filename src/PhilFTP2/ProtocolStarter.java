package PhilFTP2;

import java.net.DatagramSocket;

public class ProtocolStarter {
    static DatagramSocket socket;
    static Receiver receiver;
    static int timeout; //in miliseconds
    static int numOfTries;

    public ProtocolStarter(DatagramSocket socket, int timeout, int numOfTries) {
        ProtocolStarter.socket = socket;
        ProtocolStarter.timeout = timeout;
        ProtocolStarter.numOfTries = numOfTries;
        ProtocolStarter.receiver = new Receiver();

        receiver.start();
        System.out.println("Receiver started!");
    }
}
