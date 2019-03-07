import java.util.*;

public class Main {

    public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);
    FileClient fileClient;
    FileServer fileServer;
    String host;
    int port;

    System.out.println("Welcome to the simple File Server program. \n");
    System.out.print("In order to get started please input the server the host shall use: ");
    host = sc.next();
    System.out.print("Next please enter the port that the client and server shall connect to: ");
    port = sc.nextInt();

    System.out.println("Host: " + host + " Port: " + port);

    fileClient = new FileClient(host, port);
    fileServer = new FileServer(port);


    }
}
