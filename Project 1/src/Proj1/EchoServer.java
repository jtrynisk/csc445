package Proj1;

import java.io.*;
import java.net.*;

public class EchoServer {

    public static void main(String[] args) {

        PrintWriter out = null;
        BufferedReader in = null;
        Socket client = null;

        try {
            ServerSocket serverSocket = new ServerSocket(2710);

            for (int i = 0; i <= 3; i++) {
                client = serverSocket.accept();

                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String command = in.readLine();

                String reply = command;

                out.println(reply);

            }
        } catch (IOException err) {
            System.out.println("IO error ");
            System.exit(1);
        }

        out.close();
        try {
            in.close();
            client.close();
        } catch (IOException error) {
            System.out.println("IO error on close");
            System.exit(1);
        }
    }
}
