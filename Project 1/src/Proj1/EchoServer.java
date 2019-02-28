package Proj1;

import java.io.*;
import java.net.*;
import java.nio.Buffer;

public class EchoServer {

    public static void main(String[] args) {

        //Initializing variables
        PrintWriter out = null;
        BufferedReader in = null;
        Socket client = null;
        OutputStream outStream;
        DataInputStream inputStream;

        try {
            //Setup of the variables
            ServerSocket serverSocket = new ServerSocket(2710);
            client = serverSocket.accept();

            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            inputStream = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            outStream = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            byte[] m0 = new byte[1];
            byte[] m1 = new byte[1000];
            byte[] m2 = new byte[16000];
            byte[] m3 = new byte[64000];
            byte[] m4 = new byte[256000];
            byte[] m5 = new byte[1000000];
            byte[] data = new byte[1024];
            byte[] data1 = new byte[512];
            byte[] data2 = new byte[256];

            //Read in the first three commands, as they are strings.
            inputStream.read(m0, 0, 1);
            outStream.write(m0);

            //read in the 5 different bytes for throughput measurements.
            inputStream.read(m1);
            inputStream.read(m2);
            inputStream.read(m3);
            inputStream.read(m4);
            inputStream.read(m5);

            //1024 1024
            int read = 0;
            while((read = inputStream.read(data, 0,1024)) == 1024){
                client.setSoTimeout(200);
            }
            outStream.write(m0);

            while((read = inputStream.read(data1, 0, 512)) == 512){
                client.setSoTimeout(200);
            }
            outStream.write(m0);

            while((read = inputStream.read(data2, 0, 256)) == 256){
                client.setSoTimeout(200);
            }
            outStream.write(m0);

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
