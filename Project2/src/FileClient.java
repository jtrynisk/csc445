import com.sun.tools.internal.ws.wsdl.document.Output;

import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class FileClient {

    // Variables that are set and cannot be changed due to TFTP protocol.
    private static final byte OP_ACK = 4;
    private static final byte OP_ERR = 5;
    private static final int PACKET_SIZE = 512;

    //Variables used by the client to be manipulated in some way, shape, or form.
    private int port;
    private String host;
    private InetAddress address = null;
    private byte[] buffByteArr;
    private DatagramSocket socket = null;
    private DatagramPacket outPacket = null;
    private DatagramPacket inPacket = null;

    /**
     * Constructor of FileClient creates a new socket
     * @param host taken in as string
     * @param port taken as int for the port
     * No return just creates an open socket to the host on the specified port
     */
    public FileClient(String host, int port){
        this.host = host;
        this.port = port;
        try{
            socket = new DatagramSocket();
        }
        catch(java.io.IOException e){
            System.err.println("Sorry couldn't create a new socket.");
        }

    }

    /**
     * Lets the client know if packet coming in is the last packet or not.
     * @param packet
     * @return boolean of if the packet is the last that is being transmitted else false
     */
    public boolean isLastpacket(DatagramPacket packet){
        if (packet.getLength() < 512)
            return true;
        else
            return false;
    }


    /**
     * This method simply prints when an error occurs.
     */
    public void sendError() {
        String errorCode = new String(buffByteArr, 3, 1);
        String errorText = new String(buffByteArr, 4, inPacket.getLength() - 4);
        System.err.println("Error: " + errorCode + " " + errorText);
    }


    /**
     * Takes the string of the filename and creates a wrq for the server
     * @param toUpload
     */
    public void wrq(String toUpload, File file){

        try {
            //Create variables for the wrq packet
            address = InetAddress.getByName(this.host);
            byte[] buffer = new byte[toUpload.length() + 6];
            WRQPacket wrq = new WRQPacket(buffer);
            DatagramPacket outPacket = wrq.createPacket(toUpload, file, address, port);
            socket.send(outPacket);

        }
        catch(IOException err){
            System.err.println("Error in wrq");
        }
    }

    public void sendSequential(String fileName){

        try{
            //initialize variables for the write request
            address = InetAddress.getByName(this.host);
            File file = new File(fileName);

            double totalPackets = file.length()/PACKET_SIZE;
            totalPackets = Math.ceil(totalPackets);
            int count = 0;
            Path path = Paths.get(file.getAbsolutePath());
            byte[] fileContent = Files.readAllBytes(path);
            for(int as = 0; as < fileContent.length; as++)
                System.out.println(fileContent[as]);
            for (int i = 0; i <= totalPackets; i++){
                byte[] buff = new byte[510];
                //Now somehow send over only 512 bytes of the file
                for (int j = 0; j < 510; j++) {
                    if (fileContent.length > count) {
                        buff[j] = fileContent[count];
                        count++;
                    }
                }
                DataPacket data = new DataPacket();
                outPacket = data.createPacket(buff, address, port, i);
                socket.send(outPacket);
                byte[] ack = new byte[4];
                DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);
                socket.setSoTimeout(2000);
                try {
                    socket.receive(ackPacket);
                }catch(SocketTimeoutException to){
                    socket.send(outPacket);
                }

            }

        }
        catch(IOException e){
            System.err.println("Error in send sequential");
        }
    }


}
