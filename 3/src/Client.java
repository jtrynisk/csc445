import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class Client {

    private static final int PORT = 2710;
    private static final int PACKET_SIZE = 128;
    private static final int ENCRYPTED_SIZE = 144;


    MulticastSocket socket;
    InetAddress group;
    HashMap<String, MessagePacket> messages = new HashMap<String, MessagePacket>();
    AES aes;


    public Client() {


        try {
            aes = new AES();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connect(String address) throws IOException {

        group = InetAddress.getByName(address);
        socket = new MulticastSocket(PORT);
        socket.setTimeToLive(25);
        socket.joinGroup(group);

    }

    public void close() throws IOException {
        socket.leaveGroup(group);
        socket.close();
    }

    public void send(String id, String message) throws Exception {

        MessagePacket messagePacket = new MessagePacket(id, message);
        byte[] encyrptedData = aes.encrypt(messagePacket.createBytes(), "PasswordPassword".getBytes());
        DatagramPacket outPacket = new DatagramPacket(encyrptedData, encyrptedData.length, group, PORT);


        socket.send(outPacket);

    }

    public void receive()throws Exception {

        //For encryption you need to add 16 and then fill to the original packet size
        DatagramPacket inPacket = new DatagramPacket(new byte[ENCRYPTED_SIZE], ENCRYPTED_SIZE);
        socket.receive(inPacket);
        byte[] data = new byte[PACKET_SIZE];
        ByteBuffer bb = ByteBuffer.wrap(aes.decrypt(inPacket.getData(), "PasswordPassword".getBytes()));
        System.out.println(bb.array().length);
        bb.get(data);

        System.out.println(new String(data));

    }

}
