import java.util.*;
import java.io.File;

public class Main {

    public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);
    FileClient fileClient;
    String host, filePath;
    File file;
    int port, input;

    System.out.print("Welcome to the simple File Server program. \n");
    System.out.print("In order to get started please input the server the host shall use: ");
    host = sc.next();
    System.out.print("Next please enter the port that the client and server shall connect to: ");
    while(!sc.hasNextInt()){
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
    input = sc.nextInt();
    if(input == 1){
        //Get the name of the working directory.
        System.out.print("What is the full filepath of the file/directory? ");
        filePath = sc.next();
        file = new File(filePath);
        if(file.isDirectory()){
            //logic for reading recursively through the directory
        }
        else{
            fileClient.wrq(filePath, file);
        }

    }
    else if (input == 2){

    }

    }
}
