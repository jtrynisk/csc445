import javax.xml.crypto.Data;
import java.util.*;
import java.io.*;
import java.net.*;

public class FileClient {

    // Variables that are set and cannot be changed due to TFTP protocol.
    private static final int PACKET_SIZE = 512;
    private static final int DATAPACKET_SIZE = 510;

    //Variables used by the client to be manipulated in some way, shape, or form.
    private int port;
    private String host;
    private InetAddress address = null;
    private DatagramSocket socket = null;
    private FileInputStream fis = null;

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
     * @param host host of the server
     * @param port port from the server always 2710
     * @param address Internet address
     */
    public FileClient(String host, int port, InetAddress address){

        this.host = host;
        this.port = port;
        System.setProperty("java.net.preferIPv6Addresses", "true");
        try{
            socket = new DatagramSocket();
        }catch(SocketException e){
            e.printStackTrace();
        }

    }

    /**
     * Goes through and creates a write request. The send as
     * is for the different parameters of the assignment.
     * IPv4 vs IPv6
     * Sequential or Dropped or Sliding Window.
     * @param toUpload String of the file name
     * @param file The file object of what is going to be uploaded
     * @param sendAs This is the code it will be sent, either sequential, dropped, or window
     */
    public void wrq(String toUpload, File file, int sendAs){

        try {
            //Create variables for the wrq packet
            address = InetAddress.getByName(this.host);
            WRQPacket wrq = new WRQPacket();
            DatagramPacket outPacket = wrq.createPacket(toUpload, file, address, port, sendAs);
            try {
                socket.send(outPacket);
                socket.setSoTimeout(5000);
            }catch(SocketTimeoutException to){
                socket.send(outPacket);
            }

        }
        catch(IOException err){
            err.printStackTrace();
        }
    }

    /**
     * This sends the file with a window size of 1
     * @param fileName the filename being sent
     */
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
                temp = Arrays.copyOfRange(fileContent, (i * DATAPACKET_SIZE), (i * DATAPACKET_SIZE) + DATAPACKET_SIZE);
                DataPacket packet = new DataPacket();
                try{
                    socket.send(packet.createPacket(temp, address, port, i));
                    socket.setSoTimeout(5000);
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

    /**
     * This drops 1% of the packets, it gets the amount of packets divided it by
     * 100 and then just doesn't receive those packets to imitate dropping the
     * packet
     * @param fileName Name of the file to be sent
     */
    public void sendDropped(String fileName){

        try{
            //initialize variables for the write request
            address = InetAddress.getByName(this.host);
            File file = new File(fileName);
            double totalPackets = file.length()/PACKET_SIZE;
            totalPackets = Math.ceil(totalPackets);
            byte[] fileContent = new byte[(int)file.length()];

            //Fill fileContent array with the file.
            fis = new FileInputStream(file);
            fis.read(fileContent);

            //This will create a packet for each block.
            for(int i = 0; i <= totalPackets; i++){
                byte[] temp;
                temp = Arrays.copyOfRange(fileContent, (i * DATAPACKET_SIZE), (i * DATAPACKET_SIZE) + DATAPACKET_SIZE);
                DataPacket packet = new DataPacket();
                try{
                    socket.send(packet.createPacket(temp, address, port, i));
                    socket.setSoTimeout(5000);
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

    /**
     * This creates a window size of two and will increase if it detects no drops
     * if a drop is detected the window size will stay two as that is the floor
     * any lower and it would just be sending sequentially.
     * @param fileName Name of the file to be uploaded
     */
    public void sendWindow(String fileName){

        //Create variables for windowed send.
        File file= new File(fileName);
        int totalPackets = (int)file.length()/PACKET_SIZE;
        Window window = new Window(PACKET_SIZE, totalPackets, socket);
        byte[] fileContent = new byte[(int)file.length()];
        int[] receivedAcks = new int[totalPackets];
        int blockNum = 0;
        Arrays.fill(receivedAcks, 0);
        window.setWindowSize(2);
        boolean dropDetected = false;
        Queue<DatagramPacket>toSend = null;

        try {

            //This creates each packet and adds it to the queue.
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileContent);
            for(int i = 0; i < totalPackets; i++){
                byte[] temp;
                temp = Arrays.copyOfRange(fileContent, (i * DATAPACKET_SIZE), (i * DATAPACKET_SIZE) + DATAPACKET_SIZE);
                DatagramPacket packet = new DataPacket().createPacket(temp, address, port, i);
                toSend.add(packet);
            }

            //This will keep sending until the queue is empty
            while(!toSend.isEmpty()){

                //Send the packets for the size of the window
                for(int i = 0; i < window.getWindowSize(); i++){
                    socket.send(toSend.remove());
                }

                //Need something to check if we receive an ack for what is sent

                //Receive acks for the size of the window
                for(int i = 0; i < window.getWindowSize(); i++){
                    try{
                        byte[]ack = new byte[4];
                        DatagramPacket ACK = new DatagramPacket(ack, ack.length);
                        socket.receive(ACK);
                        socket.setSoTimeout(5000);
                        receivedAcks[ack[0]] = 1;
                    }catch(SocketTimeoutException to){
                        dropDetected = true;
                    }
                }

                if(!dropDetected)
                    window.incrementWindowSize();
                else
                    if (window.getWindowSize() > 2)
                        window.decrementWindowSize();

            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            socket.close();
            fis.close();
        }catch(IOException e){
            System.out.println("Error closing File Input Stream");
        }

    }
}
