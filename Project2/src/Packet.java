import java.net.DatagramPacket;

public class Packet {

    protected final static int PACKET_SIZE = 512;
    protected final static byte OP_WRQ = 1;
    protected final static byte OP_DATA = 3;

    protected short opCode;
    protected DatagramPacket packet;
    protected byte[] buffer;
    protected int position = 0;
    protected int packetLength;
    protected long totalPackets;

    public Packet(){

    }

    public Packet(byte[] buffer){

        this.buffer = buffer;

    }

    public Packet(DatagramPacket packet, short opCode, byte[] buffer) {

        this.packet = packet;
        this.opCode = opCode;
        this.buffer = buffer;

    }

}
