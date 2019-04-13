import java.io.*;
import java.net.*;
import java.util.*;

public class FileServer extends Thread {

    private static final int DATA_PACKET = 510;
    private static final int DATA_OFFSET = 2;
    private static final int OP_ACK = 4;

    private int port;
    private DatagramSocket socket = null;
    private DatagramPacket inPacket = null;
    private BufferedOutputStream bos;

    /**
     * Creates a new file server that runs on a given port
     * @param port The specified port for the server to accept files on
     */
    public FileServer(int port) {
        super("File Server");
        String filePath = "output";
        File output = new File(filePath);
        output.mkdir();
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (java.io.IOException e) {
            System.out.println("Couldn't create a File Server on socket " + socket);
            e.printStackTrace();
        }

    }

    /**
     * This runs indefinitely taking files.
     */
    public void run() {

        while(true) {
            try {
                //Run until server receives a quit command
                byte[] req = new byte[50];
                inPacket = new DatagramPacket(req, req.length);
                try {
                    socket.receive(inPacket);
                }catch(SocketTimeoutException to){
                    System.out.println("Awaiting request");
                }
                int totalPackets = req[0] + 1;
                String fileName = "";
                for (int z = 3; z < req.length; z++) {
                    if ((int) req[z] != 0)
                        fileName += (char) req[z];
                }
                System.out.println(fileName);

                if (req[2] == 0)
                    receiveSequential(fileName, totalPackets);
                if (req[2] == 1)
                    receiveDropped(fileName, totalPackets);
                if (req[2] == 2)
                    receiveSliding(fileName, totalPackets);

            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }

    /**
     * This receives packet sequentially sending an ACK after each packet
     * @param fileName the name of the file to be uploaded
     * @param totalPackets amount of packets that will be received
     */
    private void receiveSequential(String fileName, int totalPackets) {

        try {
            int count = 0;
            byte[] data = new byte[totalPackets * 512];
            for (int k = 0; k < totalPackets; k++) {
                byte[] buff = new byte[512];
                inPacket = new DatagramPacket(buff, buff.length);
                try {
                    socket.receive(inPacket);
                }catch(SocketTimeoutException to){
                    socket.receive(inPacket);
                }
                for (int i = 2; i < buff.length; i++) {
                    data[count] = buff[i];
                    count++;
                }
                AckPacket ack = new AckPacket();
                DatagramPacket ACK = ack.createAck(k, inPacket.getAddress(), inPacket.getPort());
                try {
                    socket.send(ACK);
                    socket.setSoTimeout(5000);
                }catch(SocketTimeoutException to){
                    socket.send(ACK);
                }
            }
            String newFilePath = ("output/" + fileName);
            File newPath = new File(newFilePath);
            if (!newPath.exists())
                newPath.createNewFile();
            else{
                newPath.delete();
                newPath.createNewFile();
            }
            bos = new BufferedOutputStream(new FileOutputStream(newPath));
            bos.write(data);
            bos.close();

        } catch (IOException error) {
            error.printStackTrace();
        }

    }

    /**
     * This ignores 1% of the totalPackets. So the client must retransmit.
     * @param fileName Name of the file to be written
     * @param totalPackets Amount of packets from the file.
     */
    private void receiveDropped(String fileName, int totalPackets){

        int amountToDrop = Math.round(totalPackets/100);
        ArrayList randomNumbers = new ArrayList();
        Random rand = new Random();
        for (int i = 0; i < amountToDrop; i++)
            randomNumbers.add(rand.nextInt(totalPackets));
        try {
            int count = 0;
            byte[] data = new byte[totalPackets * 512];
            for (int k = 0; k < totalPackets; k++) {
                if (!randomNumbers.contains(k)) {
                    byte[] buff = new byte[512];
                    inPacket = new DatagramPacket(buff, buff.length);
                    socket.receive(inPacket);
                    for (int i = 2; i < buff.length; i++) {
                        data[count] = buff[i];
                        count++;
                    }
                    AckPacket ack = new AckPacket();
                    DatagramPacket ACK = ack.createAck(k, inPacket.getAddress(), inPacket.getPort());
                    try {
                        socket.send(ACK);
                        socket.setSoTimeout(5000);
                    }catch(SocketTimeoutException to){
                        socket.send(ACK);
                    }
                }
            }
            String newFilePath = ("output/" + fileName);
            File newPath = new File(newFilePath);
            if (!newPath.exists())
                newPath.createNewFile();
            else{
                newPath.delete();
                newPath.createNewFile();
            }
            bos = new BufferedOutputStream(new FileOutputStream(newPath.getAbsolutePath()));
            bos.write(data);
            bos.close();
        } catch (IOException err) {
            err.printStackTrace();
        }

    }

    /**
     * Receiving of a file via sliding window, window increases if there is no drop detected
     * if a drop is detected and the window size is above two it will decrease
     * @param fileName name of the file to be uploaded
     * @param totalPackets amount of packets that will be received
     */
    private void receiveSliding(String fileName, int totalPackets){

        //Create the window, with packet size, and amount of packets, and the socket.
        Window window = new Window(512, totalPackets, socket);
        int count = 0;
        byte[] data = new byte[totalPackets * 512];
        window.setWindowSize(5);

        int[] receivedPackets = new int[totalPackets];
        Arrays.fill(receivedPackets, 0);
        boolean dropDetected = false;
        Queue<Integer> toAck = new LinkedList<>();

        System.out.println(totalPackets);
        try {

            //Count keeps track of how many total packets are received.
            while(count < totalPackets - 1) {

                byte[] receivedArray = new byte[512];
                DatagramPacket receivedPacket = new DatagramPacket(receivedArray, receivedArray.length);

                for(int i = 0;count < totalPackets && i < window.getWindowSize(); i++){
                    try{
                        if(count < totalPackets -1) {
                            socket.receive(receivedPacket);
                            socket.setSoTimeout(5000);
                            count++;
                            receivedPackets[(int)receivedArray[1]] = 1;
                            toAck.add((int) receivedArray[1]);
                            System.out.println((int)receivedArray[1]);
                            System.arraycopy(receivedArray, DATA_OFFSET, data, DATA_PACKET * (int)receivedArray[1], DATA_PACKET);
                        }
                        else
                            break;
                    }catch(SocketTimeoutException to){
                        System.out.println("Droppin packets");
                        dropDetected = true;
                    }

                }

                //Need to send the acks for the received packets
                while(!toAck.isEmpty()) {
                    byte[] ackArray = new byte[4];
                    ackArray[0] = toAck.remove().byteValue();
                    ackArray[2] = OP_ACK;
                    DatagramPacket ACK = new DatagramPacket(ackArray, ackArray.length, inPacket.getAddress(), inPacket.getPort());
                    socket.send(ACK);
                }
                if(!dropDetected)
                    window.incrementWindowSize();
                else{
                    if(window.getWindowSize() > 2)
                        window.decrementWindowSize();
                }
            }

            //This creates the file from the data array
            String newFilePath = ("output/" + fileName);
            File newPath = new File(newFilePath);
            if (!newPath.exists())
                newPath.createNewFile();
            else {
                newPath.delete();
                newPath.createNewFile();
            }
            bos = new BufferedOutputStream(new FileOutputStream(newPath.getAbsolutePath()));
            bos.write(data);
            bos.close();


        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
