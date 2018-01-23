package PhilFTP2;


import java.util.TreeSet;

class AcknowledgeMaster extends TreeSet<Short> {
    private static AcknowledgeMaster instance = null;
    private volatile short currentId = 0;

    private AcknowledgeMaster() {
    }

    static AcknowledgeMaster getInstance() {
        return instance == null ? instance = new AcknowledgeMaster() : instance;
    }

    synchronized short getACKid() {
        if (isAcknowledged(currentId)) this.remove(currentId);
        return currentId++;
    }

    boolean isAcknowledged(short id) {
        return this.contains(id);
    }

    public void acknowledge(short ackId) {
        this.add(ackId);
    }
}
