import java.math.BigInteger;
import java.net.DatagramPacket;
import java.io.*;
import java.net.InetAddress;

public class WRQPacket extends Packet {


    public WRQPacket(byte[] buffer){

        super(buffer);
        this.opCode = OP_WRQ;

    }

    public DatagramPacket createPacket(String toUpload, File file, InetAddress address, int port){

        DatagramPacket outPacket = null;
        packetLength = toUpload.length() + 5;
        position = 0;
        totalPackets = file.length()/PACKET_SIZE;
        totalPackets = (int)totalPackets;
        buffer[position] = (byte)totalPackets;
        position++;
        buffer[position] = OP_WRQ;
        position++;
        for (int i = 0; i < toUpload.length(); i++){
            buffer[position] = (byte) toUpload.charAt(i);
            position++;
        }

        outPacket = new DatagramPacket(buffer, 0, packetLength, address, port);
        return outPacket;

    }

}
