import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class FileServer extends Thread {

    private int port;
    private DatagramSocket socket = null;
    private DatagramPacket inPacket = null;
    private static final byte OP_ACK = 4;
    private BufferedOutputStream bos;

    //Constructor for the file server, takes in a port to create a socket on specified port
    public FileServer(int port) {
        super("File Server");
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (java.io.IOException e) {
            System.out.println("Couldn't create a fileserver on socket " + socket);
        }

    }

    public void run() {

        try {
            //Run until server receives a quit command
            byte[] req = new byte[10];
            int count = 0;
            inPacket = new DatagramPacket(req, req.length);
            socket.receive(inPacket);
            int totalPackets = req[0] + 1;
            String fileName = "";
            for(int z = 2; z < req.length; z++){
                fileName +=  (char)req[z];
            }
            byte[] data = new byte[totalPackets * 512];
            for(int k = 0; k < totalPackets; k++) {
                byte[] buff = new byte[512];
                inPacket = new DatagramPacket(buff, buff.length);
                socket.receive(inPacket);
                for(int i = 2; i < buff.length; i++){
                    data[count] = buff[i];
                    count++;
                }
                byte[] ack = new byte[4];
                sendAcknowledgment(ack);
            }

            byte[] actualData = new byte[27001];
            System.arraycopy(data, 0, actualData, 0, 27001);
            String newFilePath = ("/Users/jondntryniski/445/" + fileName);
            bos = new BufferedOutputStream(new FileOutputStream(newFilePath));
            bos.write(actualData);
            bos.close();

        }catch(IOException err){
            System.out.println("Error during IO of run method");
        }
    }


    private void sendAcknowledgment(byte[] blockNumber) {

        byte[] ACK = { 0, OP_ACK, blockNumber[0], blockNumber[1] };

        DatagramPacket ack = new DatagramPacket(ACK, ACK.length, inPacket.getAddress(),
                inPacket.getPort());
        try {
            socket.send(ack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}