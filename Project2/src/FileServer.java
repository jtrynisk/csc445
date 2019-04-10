import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;

public class FileServer extends Thread {

    private int port;
    private DatagramSocket socket = null;
    private DatagramPacket inPacket = null;
    private BufferedOutputStream bos;

    //Constructor for the file server, takes in a port to create a socket on specified port
    public FileServer(int port) {
        super("File Server");
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (java.io.IOException e) {
            System.out.println("Couldn't create a File Server on socket " + socket);
            e.printStackTrace();
        }

    }

    public void run() {

        try {
            //Run until server receives a quit command
            byte[] req = new byte[50];
            inPacket = new DatagramPacket(req, req.length);
            socket.receive(inPacket);
            int totalPackets = req[0] + 1;
            String fileName = "";
            for (int z = 3; z < req.length; z++) {
                if(req[z] != 0)
                    fileName += (char) req[z];
            }

            if (req[2] == 0)
                receiveSequential(fileName, totalPackets);
            if (req[2] == 1)
                receiveDropped(fileName, totalPackets);
            if (req[2] == 2)
                receiveSliding(fileName, totalPackets);

        } catch (IOException err) {
            System.out.println("Error during IO of run method");
        }
    }


    private void receiveSequential(String fileName, int totalPackets) {

        try {
            int count = 0;
            byte[] data = new byte[totalPackets * 512];
            for (int k = 0; k < totalPackets; k++) {
                byte[] buff = new byte[512];
                inPacket = new DatagramPacket(buff, buff.length);
                socket.receive(inPacket);
                for (int i = 2; i < buff.length; i++) {
                    data[count] = buff[i];
                    count++;
                }
                AckPacket ack = new AckPacket();
                DatagramPacket ACK = ack.createAck(k, inPacket.getAddress(), inPacket.getPort());
                socket.send(ACK);
            }
            String newFilePath = ("output");
            File newPath = new File(newFilePath);
            System.out.println(newPath.toPath());
            if (!newPath.exists())
                newPath.mkdirs();
            File newFile = new File(newPath, fileName);
            System.out.println(newFile.toPath());

            if(newFile.exists()) {
                bos = new BufferedOutputStream(new FileOutputStream(newFile));
                bos.write(data);
                bos.close();
            }
            else{
                bos = new BufferedOutputStream(new FileOutputStream(newFile));
                bos.write(data);
                bos.close();
            }

        } catch (IOException error) {
            error.printStackTrace();
        }

    }

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
                    socket.setSoTimeout(5000);
                    try {
                        socket.send(ACK);
                    }catch(SocketTimeoutException to){
                        socket.send(ACK);
                    }
                }
            }
            String newFilePath = ("output");
            File newPath = new File(newFilePath);
            System.out.println(newPath.toPath());
            if (!newPath.exists())
                newPath.mkdirs();
            File newFile = new File(newPath, fileName);
            System.out.println(newFile.toPath());

            if(newFile.exists()) {
                bos = new BufferedOutputStream(new FileOutputStream(newFile));
                bos.write(data);
                bos.close();
            }
            else{
                bos = new BufferedOutputStream(new FileOutputStream(newFile));
                bos.write(data);
                bos.close();
            }

        } catch (IOException err) {
            err.printStackTrace();
        }

    }

    private void receiveSliding(String fileName, int totalPackets){

        //Create the window, with packet size, and amount of packets, and the socket.
        Window window = new Window(512, totalPackets, socket);
        window.setupWindowServer();

        //This will keep track of what has been received by blockNum and request
        //What is missed once the window is "done"
        ArrayList receivedPacket = new ArrayList(totalPackets);
        int count = 0;
        byte[] data = new byte[totalPackets * 512];

        boolean allReceive = false;
        try {
            while(!allReceive) {

                //Start the first sliding window receive
                for (int i = 0; i < window.getWindowSize(); i++) {
                    byte[] buff = new byte[512];
                    inPacket = new DatagramPacket(buff, buff.length);
                    socket.receive(inPacket);
                    //This stores the packet received into the array list
                    //At the corresponding blockNum.
                    receivedPacket.add(buff[1], buff);
                    byte[] ack = new byte[4];
                    ack[0] = buff[1];
                    ack[1] = (byte) window.getWindowSize();
                    ack[2] = 4;
                    DatagramPacket ACK = new DatagramPacket(ack, ack.length, inPacket.getAddress(), inPacket.getPort());
                    try {
                        socket.setSoTimeout(2000);
                        socket.send(ACK);
                    }catch(SocketTimeoutException to){}
                }

                if(!receivedPacket.contains(null))
                    allReceive = true;
            }

            for(int i = 0; i < receivedPacket.size(); i++){
                data[i * 512] = (byte)receivedPacket.get(i);
            }
            String newFilePath = ("output");
            File newPath = new File(newFilePath);
            System.out.println(newPath.toPath());
            if (!newPath.exists())
                newPath.mkdirs();
            File newFile = new File(newPath, fileName);
            if(newFile.exists()) {
                bos = new BufferedOutputStream(new FileOutputStream(newFile));
                bos.write(data);
                bos.close();
            }
            else {
                bos = new BufferedOutputStream(new FileOutputStream(newFile));
                bos.write(data);
                bos.close();
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
