import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class FileServer extends Thread {

    private String toUpload;
    private int port;
    private DatagramSocket socket = null;
    private DataInputStream in = null;

    //Constructor for the file server, takes in a port to create a socket on specified port
    public FileServer(int port){
        super("File Server");
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        }
        catch(java.io.IOException e){
            System.out.println("Couldn't create a fileserver on socket " + socket);
        }

    }

    public void run() {

        //Run until server receives a quit command
        boolean isValid = true;
        while(isValid){

        }

    }


}
