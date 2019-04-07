import java.net.DatagramPacket;
import java.net.InetAddress;

public class DataPacket extends Packet {

    public DataPacket(){

    }

    public DatagramPacket createPacket(byte[] fileContent, InetAddress address, int port, int blockNum){

        DatagramPacket returnPacket = null;
        int size = 0;
        for(int i = 0; i < fileContent.length; i++){
            System.out.println(fileContent[i]);
            if(fileContent[i] != 0)
                size++;
        }
        System.out.println(size);
        byte[] retArr = new byte[size + 2];
        retArr[0] = OP_DATA;
        retArr[1] = (byte)blockNum;
        System.arraycopy(fileContent, 0, retArr, 2, size);
        returnPacket = new DatagramPacket(retArr, 0, retArr.length, address, port);
        return returnPacket;

    }

}
