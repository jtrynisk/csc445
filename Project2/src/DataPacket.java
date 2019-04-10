import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetAddress;

public class DataPacket extends Packet {

    public DataPacket(){

    }

    public DatagramPacket createPacket(byte[] fileContent, InetAddress address, int port, int blockNum){

        DatagramPacket returnPacket;
        byte[] retArr = new byte[PACKET_SIZE];
        retArr[0] = OP_DATA;
        retArr[1] = (byte)blockNum;
        System.arraycopy(fileContent, 0, retArr, 2, 510);
        returnPacket = new DatagramPacket(retArr, 0, retArr.length, address, port);
        return returnPacket;

    }

}
