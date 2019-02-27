package Proj1;

import java.io.*;
import java.net.*;

public class EchoServer {

    public static void main(String[] args) {

        //Initializing variables
        PrintWriter out = null;
        BufferedReader in = null;
        Socket client = null;
        OutputStream outStream;
        InputStream inputStream;

        try {
            //Setup of the variables
            ServerSocket serverSocket = new ServerSocket(2710);
            client = serverSocket.accept();

            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            inputStream = client.getInputStream();
            byte[] m1 = new byte[1000];
            byte[] m2 = new byte[16000];
            byte[] m3 = new byte[64000];
            byte[] m4 = new byte[256000];
            byte[] m5 = new byte[1000000];
            byte[] data = new byte[1024];
            byte[] data1 = new byte[512];
            byte[] data2 = new byte[256];

            //Read in the first three commands, as they are strings.
            for (int i = 0; i < 3; i++) {
                String command = in.readLine();
                String reply = command;
                out.println(reply);
            }

            //read in the 5 different bytes for throughput measurements.
            inputStream.read(m1);
            inputStream.read(m2);
            inputStream.read(m3);
            inputStream.read(m4);
            inputStream.read(m5);

            System.out.println("reading 1024");
            for (int i = 0; i < 1024; i++) {
                inputStream.read(data);
            }
            out.println("a");

            System.out.println("reading 2048");
            for (int i = 0; i < 512; i++) {
                inputStream.read(data1);
            }
            out.println("b");

            System.out.println("reading 4096");
            for (int i = 0; i < 256; i++) {
                inputStream.read(data2);
            }
            out.println("c");

        } catch (IOException err) {
            //Handle IO Error on connecting to client
            System.out.println("IO error ");
            System.exit(1);
        }

        //Close the printwriter to the client
        out.close();
        try {
            //close the bufferedreader and socket
            in.close();
            client.close();
        } catch (IOException error) {
            //Catch and IO error on closing the bufferedreader and socket.
            System.out.println("IO error on close");
            System.exit(1);
        }
    }
}
