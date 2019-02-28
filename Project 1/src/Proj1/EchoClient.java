package Proj1;

import java.io.IOException;
import java.net.*;
import java.io.*;

public class EchoClient {

    public static void main(String[] args){
        String host = "pi.cs.oswego.edu";

        //Initializing variables
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        DataOutputStream outStream = null;
        DataInputStream inputStream = null;

        long start = 0;
        long finish = 0;
        byte[] m0 = new byte[1];
        byte[] m = new byte[64];
        byte[] m1 = new byte[1000];
        byte[] m2 = new byte[16000];
        byte[] m3 = new byte[64000];
        byte[] m4 = new byte[256000];
        byte[] m5 = new byte[1000000];
        byte[] m6 = new byte[1024];
        byte[] m7 = new byte[512];
        byte[] m8 = new byte[256];

        try{
            //Define variables
            echoSocket = new Socket(host, 2710);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            outStream = new DataOutputStream(new BufferedOutputStream(echoSocket.getOutputStream()));
            inputStream = new DataInputStream(new BufferedInputStream(echoSocket.getInputStream()));
        }
        catch(UnknownHostException e){
            //Handles host being incorrect
            System.out.println("Host " + host + " unavailable.");
            System.exit(1);
        }
        catch(IOException err){
            //Handles not being able to connect to server
            System.out.println("IO connection failed.");
            System.exit(1);
        }

        try{
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

            //First request
            start = System.nanoTime();
            outStream.write(m0);
            inputStream.read(m0, 0, 1);
            finish = System.nanoTime() - start;
            System.out.println("RTT: " + finish);

            //Second Request
            start = System.nanoTime();
            outStream.write(m0);
            inputStream.read(m, 0, 1);
            finish = System.nanoTime() - start;
            System.out.println("RTT: " + finish);

            //Third Request
            start = System.nanoTime();
            outStream.write(m0);
            inputStream.read(m6, 0, 1);
            finish = System.nanoTime() - start;
            System.out.println("RTT: " + finish);

            //First throughput measurement of 1k
            start = System.nanoTime();
            outStream.write(m1);
            outStream.flush();
            finish = System.nanoTime() - start;
            System.out.println("RTT of 1k message: " + finish);

            //Second throughput measurement of 16k
            start = System.nanoTime();
            outStream.write(m2);
            outStream.flush();
            finish = System.nanoTime() - start;
            System.out.println("RTT of 16k message: " + finish);

            //Third throughput measurement of 64k
            start = System.nanoTime();
            outStream.write(m3);
            outStream.flush();
            finish = System.nanoTime() - start;
            System.out.println("RTT of 64k message: " + finish);

            //Fourth throughput measurement of 256k
            start = System.nanoTime();
            outStream.write(m4);
            outStream.flush();
            finish = System.nanoTime() - start;
            System.out.println("RTT of 256k message: " + finish);

            //Fifth throughput measurement of 1M
            start = System.nanoTime();
            outStream.write(m5);
            outStream.flush();
            finish = System.nanoTime() - start;
            System.out.println("RTT of 1M message: " + finish);

            //1024, 1024byte messages
            start = System.nanoTime();
            for (int i = 0; i < 1024; i++){
                outStream.write(m6);
            }
            outStream.write(m0);
            inputStream.read(m0);
            finish = System.nanoTime() - start;
            System.out.println("RTT of 1024, 1024 byte messages: " + finish);

            //2048, 512 byte messages
            start = System.nanoTime();
            for (int i = 0; i < 2048; i++){
                outStream.write(m7);
            }
            outStream.write(m0);
            inputStream.read(m0);
            finish = System.nanoTime() - start;
            System.out.println("RTT of 2048, 512 byte messages: " + finish);

            //4096, 256 byte messages
            start = System.nanoTime();
            for (int i = 0; i < 4096; i++){
                outStream.write(m8);
            }
            outStream.write(m0);
            inputStream.read(m0);
            finish = System.nanoTime() - start;
            System.out.println("RTT of 4096, 256 byte messages: " + finish);


            //Clean up, closes the port, printwriter, and bufferedreader
            out.close();
            in.close();
            stdin.close();
            echoSocket.close();

        }
        catch(IOException error) {
            System.out.println("IO failure");
        }
    }
}
