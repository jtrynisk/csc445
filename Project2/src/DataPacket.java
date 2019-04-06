import java.net.DatagramPacket;
import java.io.*;
import java.net.InetAddress;
import java.util.Arrays;

public class DataPacket extends Packet {

    public DataPacket(){

        super();
        this.opCode = OP_DATA;

    }

    public DatagramPacket createPacket(byte[] fileContent, InetAddress address, int port, int blockNum){

        DatagramPacket returnPacket = null;
        int size = 0;
        for(int i = 0; i < fileContent.length; i++){
            size++;
        }
        byte[] retArr = new byte[size + 2];
        retArr[0] = OP_DATA;
        retArr[1] = (byte)blockNum;
        System.arraycopy(fileContent, 0, retArr, 2, size);
        returnPacket = new DatagramPacket(retArr, 0, retArr.length, address, port);
        return returnPacket;

    }

}
