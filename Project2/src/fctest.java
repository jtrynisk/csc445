import java.io.*;

public class fctest {

    public static void main(String[] args) {

        int port = 2710;
        String host = "127.0.0.1";
        FileClient fc = new FileClient(host, port);

        String filepath = "/Users/jondntryniski/445/Project2/test.png";
        File file = new File(filepath);
        System.out.println(file.getName());

        fc.wrq(file.getName(), file);
        try {
            fc.sendSequential(filepath);
        }catch(IOException e){
            System.out.println("Error boiiiii");
        }
    }

}
