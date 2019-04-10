import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.TimeoutException;

public class FileClient {

    // Variables that are set and cannot be changed due to TFTP protocol.
    private static final int PACKET_SIZE = 512;

    //Variables used by the client to be manipulated in some way, shape, or form.
    private int port;
    private String host;
    private InetAddress address = null;
    private DatagramSocket socket = null;

    /**
     * Constructor of FileClient creates a new socket IPv4
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
     * Creates a File Client with IPv6 parameters
     * @param host
     * @param port
     * @param address
     */
    public FileClient(String host, int port, InetAddress address){

        this.host = host;
        this.port = port + 1;
        System.setProperty("java.net.preferIPv6Addresses", "true");
        try{
            socket = new DatagramSocket(port + 1, address);
        }catch(SocketException e){
            e.printStackTrace();
        }

    }

    /**
     * Goes through and creates a write request. The send as
     * is for the different parameters of the assignment.
     * IPv4 vs IPv6
     * Sequential or Dropped or Sliding Window.
     * @param toUpload
     * @param file
     * @param sendAs
     */
    public void wrq(String toUpload, File file, int sendAs){

        try {
            //Create variables for the wrq packet
            address = InetAddress.getByName(this.host);
            System.out.println("made address");
            WRQPacket wrq = new WRQPacket();
            System.out.println("created wrq packet");
            DatagramPacket outPacket = wrq.createPacket(toUpload, file, address, port, sendAs);
            System.out.println("created outPacket");
            socket.send(outPacket);
            System.out.println("Sent packet");

        }
        catch(IOException err){
            err.printStackTrace();
        }
    }

    public void sendSequential(String fileName) {

        try{
            //initialize variables for the write request
            address = InetAddress.getByName(this.host);
            File file = new File(fileName);
            double totalPackets = file.length()/PACKET_SIZE;
            totalPackets = Math.ceil(totalPackets);
            byte[] fileContent = new byte[(int)file.length()];

            //Fill fileContent array with the file.
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileContent);

            //This will create a packet for each block.
            for(int i = 0; i <= totalPackets; i++){
                byte[] temp;
                temp = Arrays.copyOfRange(fileContent, (i * 510), (i * 510) + 510);
                DataPacket packet = new DataPacket();
                socket.setSoTimeout(2000);
                try{
                    socket.send(packet.createPacket(temp, address, port, i));
                }catch(SocketTimeoutException to){
                    socket.send(packet.createPacket(temp, address, port, i));
                }
                byte[] ack = new byte[4];
                DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);
                socket.receive(ackPacket);

            }


        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void sendDropped(String fileName){

        try{
            //initialize variables for the write request
            address = InetAddress.getByName(this.host);
            File file = new File(fileName);
            double totalPackets = file.length()/PACKET_SIZE;
            totalPackets = Math.ceil(totalPackets);
            byte[] fileContent = new byte[(int)file.length()];

            //Fill fileContent array with the file.
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileContent);

            //This will create a packet for each block.
            for(int i = 0; i <= totalPackets; i++){
                byte[] temp;
                temp = Arrays.copyOfRange(fileContent, (i * 510), (i * 510) + 510);
                DataPacket packet = new DataPacket();
                socket.setSoTimeout(5000);
                try{
                    socket.send(packet.createPacket(temp, address, port, i));
                }catch(SocketTimeoutException to){
                    socket.send(packet.createPacket(temp, address, port, i));
                }
                byte[] ack = new byte[4];
                DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);
                socket.receive(ackPacket);

            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    public void sendWindow(String fileName){

        //Create variables for windowed send.
        File file= new File(fileName);
        int totalPackets = (int)file.length()/PACKET_SIZE;
        Window window = new Window(PACKET_SIZE, totalPackets, socket);
        window.setUpWindowClient();
        byte[] fileContent = new byte[(int)file.length()];
        ArrayList receivedAcks = new ArrayList(totalPackets);
        int blockNum = 0;
        boolean allSent = false;

        try {

            //Fill the byte array with the file
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileContent);

            //Now we send and get some acks back
            //So the thing is we need to get acks and if no dropped pakcets
            //increase the window size.
                //This will loop until we verify receivedAcks is full.
            while(!allSent) {

                for (int i = 0; i < window.getWindowSize(); i++) {
                    byte[] temp;
                    temp = Arrays.copyOfRange(fileContent, blockNum * 510, (blockNum * 510) + 510);
                    DatagramPacket outPacket = new DataPacket().createPacket(temp, address, port, blockNum);
                    try {
                        socket.setSoTimeout(2000);
                        socket.send(outPacket);
                    }catch(SocketTimeoutException to){}
                    byte[] ack = new byte[4];
                    DatagramPacket ACK = new DatagramPacket(ack, ack.length);
                    socket.receive(ACK);
                    receivedAcks.add(ack[0], 1);
                    blockNum++;

                    //Need to implement something to increase window size.
                }
                if(!receivedAcks.contains(null))
                    allSent = true;

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
