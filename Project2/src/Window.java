import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;

public class Window {

    private long start, rtt, totalTime, end;
    private int windowSize, loopCount, packetSize, totalPackets, port;
    private InetAddress address;
    private DatagramSocket socket;
    private DatagramPacket wPacket, ACK;
    private byte[] window, ack;

    public Window(int packetSize, int totalPackets, InetAddress address, int port, DatagramSocket socket){
        this.packetSize = packetSize;
        this.totalPackets = totalPackets;
        this.address = address;
        this.port = port;
        this.socket = socket;
        byte[] window = new byte[packetSize];
        DatagramPacket wPacket = new DatagramPacket(window, window.length);

    }

    public Window(int packetSize, int totalPackets, DatagramSocket socket){
        this.packetSize = packetSize;
        this.totalPackets = totalPackets;
        this.address = address;
        this.port = port;
        this.socket = socket;
        byte[] window = new byte[packetSize];
        DatagramPacket wPacket = new DatagramPacket(window, window.length);

    }

    public void setUpWindowClient() {

        //Setup the window size
        byte[] s = new byte[512];
        DatagramPacket wPacket = new DatagramPacket(s, s.length, address, port);

        try {
            start = System.nanoTime();
            for (int i = 0; i < 50; i++) {
                socket.setSoTimeout(7000);
                try {
                    socket.send(wPacket);
                }catch(SocketTimeoutException to){
                    socket.send(wPacket);
                }
                DatagramPacket ACK = new AckPacket().createAck(wPacket.getAddress(), wPacket.getPort());
                socket.receive(ACK);
            }
            end = System.nanoTime();
            totalTime = end - start;
            rtt = (end - start)/50;
            setWindowSize(2);
            System.out.println("Total time: " + totalTime + " Window size: " + windowSize + " RTT: " + rtt);
            loopCount = (totalPackets/windowSize) + 1;

        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void setupWindowServer(){

        try {
            byte[] s = new byte[512];
            DatagramPacket wPacket = new DatagramPacket(s, s.length);

            for (int i = 0; i < 50; i++) {
                socket.setSoTimeout(5000);
                try {
                    socket.receive(wPacket);
                }catch(SocketTimeoutException to){
                    socket.receive(wPacket);
                }
                try {
                    DatagramPacket ACK = new AckPacket().createAck(wPacket.getAddress(), wPacket.getPort());
                    socket.send(ACK);
                }catch(SocketTimeoutException to){
                    socket.send(ACK);
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void incrementWindowSize(){
        this.windowSize++;
    }

    public void decrementWindowSize(){
        this.windowSize--;
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    public void setWindowSize(int windowSize){
        this.windowSize = windowSize;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

}
