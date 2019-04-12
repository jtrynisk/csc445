import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FileServer extends Thread {

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

       // while(true) {
            try {
                //Run until server receives a quit command
                byte[] req = new byte[50];
                inPacket = new DatagramPacket(req, req.length);
                try {
                    socket.receive(inPacket);
                }catch(SocketTimeoutException to){
                    socket.receive(inPacket);
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
      //  }
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
                System.out.println(k);
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
            System.out.println(newPath.getAbsolutePath());
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
            randomNumbers.add(rand.nextInt(totalPackets ));
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
            System.out.println(newPath.getAbsolutePath());
            if (!newPath.exists())
                newPath.createNewFile();
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

        int blockNum;
        byte[] data = new byte[totalPackets * 512];

        window.setWindowSize(2);

        int loopCount = (totalPackets/window.getWindowSize());

        int[] receivedPackets = new int[totalPackets - 1];
        Arrays.fill(receivedPackets, 0);

        try {
            //This will keep track of what has been received by blockNum and request
            //What is missed once the window is "done"
            for (int k = 0; k < loopCount; k++){
                //Start the first sliding window receive
                byte[] buff = new byte[512];
                inPacket = new DatagramPacket(buff, buff.length);
                try {
                    socket.receive(inPacket);
                }catch(SocketTimeoutException to){
                    System.out.println("Timeout on receive");
                }
                byte[] buff2 = new byte[512];
                DatagramPacket inPacket2 = new DatagramPacket(buff2, buff2.length);
                try {
                    socket.receive(inPacket2);
                }catch(SocketTimeoutException to){
                    System.out.println("Timeout on receive 2");
                }

                blockNum = buff[1];
                int blockNum2 = buff2[1];

                byte[] ack = new byte[4];
                ack[0] = (byte)blockNum;
                ack[1] = (byte) window.getWindowSize();
                ack[2] = 4;
                DatagramPacket ACK = new DatagramPacket(ack, ack.length, inPacket.getAddress(), inPacket.getPort());
                byte[] ack2 = new byte[4];
                ack2[0] = (byte)blockNum2;
                ack2[1] = (byte) window.getWindowSize();
                ack2[2] = 4;
                DatagramPacket ACK2 = new DatagramPacket(ack2, ack2.length, inPacket.getAddress(), inPacket.getPort());
                try {
                    socket.send(ACK);
                    socket.setSoTimeout(5000);
                    socket.send(ACK2);
                    socket.setSoTimeout(5000);
                }catch(SocketTimeoutException to){
                    System.out.println("Dropped ack");
                    socket.send(ACK);
                    socket.send(ACK2);
                }

                //This stores the packet received into the array list
                //At the corresponding blockNum.
                receivedPackets[blockNum] = 1;
                receivedPackets[blockNum2] = 1;
                System.out.println(blockNum + " " + blockNum2);
                for (int j = 0; j < 510; j++){
                    data[(blockNum * 510) + j] = buff[j + 2];
                    data[(blockNum2 * 510) + j] = buff2[j + 2];
                }


            }

            for (int l = 0; l < receivedPackets.length; l++){
                if(receivedPackets[l] != 1){
                    System.out.println(l);
                    byte[] buff = new byte[512];
                    inPacket = new DatagramPacket(buff, buff.length);
                    try {
                        socket.receive(inPacket);
                    }catch(SocketTimeoutException to){
                        socket.receive(inPacket);
                    }
                    blockNum = buff[1];
                    //This stores the packet received into the array list
                    //At the corresponding blockNum.
                    receivedPackets[blockNum] = 1;
                    for (int j = 0; j < 510; j++){
                        data[(blockNum * 510) + j] = buff[j + 2];
                    }
                    byte[] ack = new byte[4];
                    ack[0] = (byte)blockNum;
                    ack[1] = (byte) window.getWindowSize();
                    ack[2] = 4;
                    DatagramPacket ACK = new DatagramPacket(ack, ack.length, inPacket.getAddress(), inPacket.getPort());
                    try {
                        socket.send(ACK);
                        socket.setSoTimeout(5000);
                    }catch(SocketTimeoutException to){
                        socket.send(ACK);
                    }
                    receivedPackets[ack[0]] = 1;
                }
            }

            String newFilePath = ("output/" + fileName);
            File newPath = new File(newFilePath);
            System.out.println(newPath.getAbsolutePath());
            if (!newPath.exists())
                newPath.createNewFile();
            else{
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
