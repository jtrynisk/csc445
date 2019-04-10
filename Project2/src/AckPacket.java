import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class AckPacket extends Packet {

    public AckPacket(){

    }

    public DatagramPacket createAck(InetAddress address, int port){

        byte[]ack = new byte[4];
        ack[2] = OP_ACK;
        DatagramPacket packet = new DatagramPacket(ack, ack.length, address, port);
        return packet;

    }

    public DatagramPacket createAck(int blockNum, InetAddress address, int port) {

        //Initialize the ack packet.
        byte[] ack = new byte[4];
        ack[0] = (byte) blockNum;
        ack[2] = OP_ACK;

        DatagramPacket packet = new DatagramPacket(ack, ack.length, address, port);
        return packet;

    }

}
