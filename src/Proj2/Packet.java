package Proj2;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;

public class Packet {

    protected static int PORT = 2710;
    protected static int PACKET_LENGTH = 512;

    protected static byte WRQ = 2;
    protected static byte DATA = 3;
    protected static byte ACK = 4;
    protected static byte ERROR = 5;

    protected byte[] message;
    protected int length;

    protected InetAddress host;
    protected int port;

    public Packet(){
        this.message = new byte[PACKET_LENGTH];
        this.length = PACKET_LENGTH;
    }

    public Packet receive(DatagramSocket socket) throws IOException {

        Packet inPack = new Packet();
        Packet retPack = new Packet();

        DatagramPacket in = new DatagramPacket(inPack.message, inPack.length);
        socket.receive(in);

        if (inPack.get(0) == WRQ)
            retPack = new WRQPacket;
        if (inPack.get(0) == DATA)
            retPack = new DataPacket;
        if (inPack.get(0) == ACK)
            retPack = new AckPacket;
        if (inPack.get(0) == ERROR)
            retPack = new ErrorPacket;

        retPack.message = inPack.message;
        retPack.length = in.getLength();
        retPack.host = in.getAddress();
        retPack.port = PORT;

        return retPack;

    }


    public int get(int at) {

        return (message[at] & 0xff);

    }

    public void send(InetAddress host, int port, DatagramSocket socket){

        socket.send(new DatagramPacket(message, length, host, port);

    }

    public InetAddress getAddress(){

        return this.host;

    }

    public int getPort() {

        return this.port;

    }

}
