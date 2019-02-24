package Proj1;

import java.io.IOException;
import java.net.*;
import java.io.*;
import java.nio.Buffer;

public class EchoClient {

    public static void main(String[] args){
        String host = "127.0.0.1";

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try{
            echoSocket = new Socket(host, 2710);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

        }
        catch(UnknownHostException e){
            System.out.println("Host " + host + " unavailable.");
            System.exit(1);
        }
        catch(IOException err){
            System.out.println("IO connection failed.");
            System.exit(1);
        }

        try{
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            String input = null;

            for (int i = 0; i < 3; i++) {
                input = stdin.readLine();
                long start = System.nanoTime();
                out.println(input);
                System.out.println("echo: " + in.readLine() + " RTT: " + (System.nanoTime() - start));
            }

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
