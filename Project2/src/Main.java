import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.io.File;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        FileClient fileClient;
        String host, filePath;
        int port, input;
        filePath = "";
        FileOutputStream fos = null;
        ZipOutputStream zip = null;
        File toUpload = null;

        System.out.print("Welcome to the simple File Server program. \n");
        System.out.print("In order to get started please input the server the host shall use: ");
        host = sc.next();
        System.out.print("Next please enter the port that the client and server shall connect to: ");
        while (!sc.hasNextInt()) {
            System.out.print("Sorry please enter a number for the port: ");
            sc.next();
        }
        port = sc.nextInt();

        System.out.println("Host: " + host + " Port: " + port);

        fileClient = new FileClient(host, port);

        System.out.println("Available Commands: ");
        System.out.println("1. Send a file/directory");
        System.out.println("2. Quit");
        System.out.print("What would you like to do? ");
        while (sc.hasNext()) {

            input = sc.nextInt();
            if (input == 1) {
                //Get the name of the working directory.
                System.out.print("What is the full filepath of the file/directory? ");
                filePath = sc.next();
                toUpload = new File(filePath);
                    if (toUpload.isDirectory()) {
                        String split = toUpload.getName();
                        split.split(".");
                        try {
                            File zipFile = new File(split + ".zip");
                            fos = new FileOutputStream(split + ".zip");
                            zip = new ZipOutputStream(fos);
                            toUpload = zipFile;
                        }catch(FileNotFoundException fe){
                            System.out.println("Sorry no file found");
                        }
                    }
                    System.out.println("How would you like to send the file?");
                    System.out.println("Sequential: 1");
                    System.out.println("Dropping 1% of Packets: 2");
                    System.out.println("Windowed: 3");
                    System.out.println("IPv6: 4");

                    while(!sc.hasNextInt()){
                        System.out.println("Sorry please an integer");
                        sc.next();
                    }
                    input = sc.nextInt();
                    if (input == 1) {
                        fileClient.wrq(toUpload.getName(), toUpload, 0);
                        fileClient.sendSequential(toUpload.getName());
                    }
                    if (input == 2) {
                        fileClient.wrq(toUpload.getName(), toUpload, 1);
                        fileClient.sendDropped(toUpload.getName());
                    }
                    if (input == 3){
                        fileClient.wrq(toUpload.getName(), toUpload, 2);
                        fileClient.sendWindow(toUpload.getName());
                    }
                    if (input == 4){
                        fileClient.close();
                        FileClient fc = null;
                        try {
                            fc = new FileClient(host, port, InetAddress.getByName(host));
                        }catch(UnknownHostException e){
                            System.out.println("Sorry don't know about that host");
                        }
                        fc.wrq(toUpload.getName(), toUpload, 0);
                        fc.sendSequential(toUpload.getName());
                    }

            } else if (input == 2) {
                try {
                    fos.close();
                    zip.close();
                }catch(IOException e){
                    System.out.println("Error closing streams");
                }
                fileClient.close();
                System.exit(0);
            }

        }
    }
}
