import java.net.DatagramPacket;
import java.io.*;
import java.net.InetAddress;

public class WRQPacket extends Packet {


    public WRQPacket(){

        this.opCode = OP_WRQ;

    }

    public DatagramPacket createPacket(String toUpload, File file, InetAddress address, int port, int sendAs){

        DatagramPacket outPacket;
        buffer = new byte[4 + (int)file.length()];
        position = 0;
        totalPackets = (file.length()/PACKET_SIZE);
        buffer[position] = (byte)totalPackets;
        position++;
        buffer[position] = OP_WRQ;
        position++;
        buffer[position] = (byte)sendAs;
        position++;
        for (int i = 0; i < toUpload.length(); i++){
            buffer[position] = (byte) toUpload.charAt(i);
            position++;
        }

        outPacket = new DatagramPacket(buffer, 0, buffer.length, address, port);
        return outPacket;

    }

}