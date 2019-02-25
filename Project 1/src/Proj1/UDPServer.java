package Proj1;

import java.io.*;
import java.net.*;

public class UDPServer extends Thread{

    //initialize variables
    private DatagramSocket socket = null;
    private DataInputStream in = null;

    UDPServer() {
        //Setup a UDP Server thread
        super("QuoteServer");
        try {
            socket = new DatagramSocket(2710);
        } catch(java.io.IOException e){
            System.out.println("IO Exception");
        }
    }

    public void run() {

        //Creating loop for the amount of requests we will get
        for (int i =0; i < 3; i++) {
            try {
                //Create variables for the request and response, max size of max packet.
                byte[] buf = new byte[1024];
                DatagramPacket packet;
                InetAddress address;
                int port;

                //receiving request
                packet = new DatagramPacket(buf, 1024);
                socket.receive(packet);
                address = packet.getAddress();
                port = packet.getPort();

                //send response
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch(IOException err){
                System.err.println("IO Error");
                System.exit(1);
            }
        }

    }

    //Method to close the UDP port
    protected void finalize() {
        if (socket != null) {
            socket.close();
            socket = null;
            System.out.println("Closing datagram socket.");
        }
    }


    //Main method to run and finalize UDP server
    public static void main(String[] args) {
        UDPServer e = new UDPServer();
        e.run();
        e.finalize();
    }


}
