import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public class MessagePacket {

    private static final int PACKET_SIZE = 128;

    private String id;
    private String message;

    public MessagePacket(String id, String message){

        this.id = id;
        this.message = message;

    }

    public byte[] createBytes(){

        //Create a buffer
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);

        //Fill the id
        byte[]idBytes = new byte[12];
        System.arraycopy(id.getBytes(), 0, idBytes, 0, id.length());

        //Fill the message
        buffer.put(idBytes);
        buffer.put(message.getBytes());

        return buffer.array();

    }


}
