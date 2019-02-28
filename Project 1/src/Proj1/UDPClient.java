package Proj1;

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient {

    public static void main(String[] args) {

        //Initialize variables
        int port = 2710;
        DatagramSocket socket = null;
        DatagramPacket packet;
        byte[] m1 = new byte[1];
        byte[] m2 = new byte[64];
        byte[] m3 = new byte[1024];
        byte[] m4 = new byte[512];
        byte[] m5 = new byte[256];

        try {
            //Open socket
            socket = new DatagramSocket();
        } catch (java.io.IOException e) {
            System.err.println("Could not create Datagram Socket");
        }

        try {
            //send request
            InetAddress address = InetAddress.getByName("127.0.0.1");
            packet = new DatagramPacket(m1, 1, address, port);
            socket.send(packet);
            long start = System.nanoTime();

            //get response
            packet = new DatagramPacket(m1, 1);
            socket.receive(packet);
            System.out.println((System.nanoTime() - start));

            //send second request
            packet = new DatagramPacket(m2, 64, address, port);
            socket.send(packet);
            start = System.nanoTime();

            //get second response
            packet = new DatagramPacket(m2, 64);
            socket.receive(packet);
            System.out.println((System.nanoTime() - start));

            //send third request
            packet = new DatagramPacket(m3, 1024, address, port);
            socket.send(packet);
            start = System.nanoTime();

            //get response
            packet = new DatagramPacket(m3, 1024);
            socket.receive(packet);
            System.out.println((System.nanoTime() - start));

            //Send 1024, 1024 byte messages.
            packet = new DatagramPacket(m3, 1024, address, port);
            start = System.nanoTime();
            for (int i = 0; i < 1024; i++){
                socket.send(packet);
            }
            packet = new DatagramPacket(m1, 1);
            socket.setSoTimeout(200);
            socket.receive(packet);
            System.out.println("1024, 1024 byte messages RTT: " + (start - System.nanoTime()));

            //Send 2048 512 byte messages
            packet = new DatagramPacket(m4, 512, address, port);
            start = System.nanoTime();
            for (int i = 0; i < 2048; i++){
                socket.send(packet);
                System.out.println(i);
            }
            packet = new DatagramPacket(m1, 1);
            socket.setSoTimeout(200);
            socket.receive(packet);
            System.out.println("2048, 512 byte messages RTT: " + (System.nanoTime() - start));

            //Send 4096, 256 byte messages
            packet = new DatagramPacket(m5, 256, address, port);
            start = System.nanoTime();
            for (int i = 0; i < 1024; i++){
                socket.send(packet);
                System.out.println(i);
            }
            packet = new DatagramPacket(m1, 1);
            socket.setSoTimeout(200);
            socket.receive(packet);
            System.out.println("4096, 256 byte messages RTT: " + (System.nanoTime() - start));



        } catch (IOException err) {
            System.err.println("IO Exception");
        }

    }
}