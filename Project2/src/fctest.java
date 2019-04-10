import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class fctest {

    public static void main(String[] args) {

        int port = 2710;
        String host = "127.0.0.1";
        FileClient fc = new FileClient(host, port);

        String filepath = "/Users/jondntryniski/445/Project2/test.png";
        File file = new File(filepath);
        String filepath1 = "/Users/jondntryniski/445/Project2/test1.png";
        File file1 = new File(filepath1);
        String filepath2 = "/Users/jondntryniski/445/Project2/test2.png";
        File file2 = new File(filepath2);


        try {
            fc.wrq(file.getName(), file, 0);
            fc.sendSequential(filepath);
            fc.wrq(file1.getName(), file1, 1);
            fc.sendDropped(filepath1);
            fc.wrq(file2.getName(), file2, 2);
            fc.sendSequential(filepath2);
        }catch(Exception e){
            e.printStackTrace();
        }

/*        System.out.println("Testing ipv6");

        try {
            FileClient fcv6 = new FileClient(host, 3710, InetAddress.getByName(host));
            fcv6.wrq(file1.getName(), file, 0);
            fcv6.sendSequential(filepath1);
            fcv6.wrq(file1.getName(), file, 1);
            fc.sendDropped(filepath1);
        }catch(UnknownHostException e){
            e.printStackTrace();
        }*/
    }

}
