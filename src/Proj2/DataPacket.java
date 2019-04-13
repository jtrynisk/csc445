package Proj2;

import java.io.FileInputStream;
import java.io.IOException;

public class DataPacket extends Packet {

    public DataPacket() {

    }

    public DataPacket(int blockNum, FileInputStream in) throws IOException {

        this.message = new byte[PACKET_LENGTH];
        message[0] = DATA;
        message[1] = (byte)blockNum;
        length

    }

}
