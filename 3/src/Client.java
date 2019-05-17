import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class Client {

    private static final int PORT = 2710;

    MulticastSocket socket;
    InetAddress group;
    HashMap<String, MessagePacket> messages = new HashMap<String, MessagePacket>();
    AES aes;


    public Client(){


        try{
            aes = new AES();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void connect(String address) throws IOException {

        group = InetAddress.getByName(address);
        socket = new MulticastSocket(PORT);
        socket.setTimeToLive(25);
        socket.joinGroup(group);

    }

    public void close() throws IOException{
        socket.leaveGroup(group);
        socket.close();
    }

    public void send(String id, String message) throws Exception {

        MessagePacket messagePacket = new MessagePacket(id, message);
        byte[] encyrptedData = aes.encyrpt(messagePacket.createBytes());
        DatagramPacket outPacket = new DatagramPacket(encyrptedData, encyrptedData.length);

        socket.send(outPacket);


    }

}
