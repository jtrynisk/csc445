package Proj1;

import java.io.*;
import java.net.*;

public class EchoServer {

    public static void main(String[] args) {

        //Initializing variables
        PrintWriter out = null;
        BufferedReader in = null;
        Socket client = null;

        try {
            //Setup of the variables
            ServerSocket serverSocket = new ServerSocket(2710);
            client = serverSocket.accept();

            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            for (int i = 0; i < 3; i++) {

                //Loop for the amount of times it will get requests.
                //Send the request back
                String command = in.readLine();
                String reply = command;
                out.println(reply);

            }
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
