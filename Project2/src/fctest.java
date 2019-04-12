import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class fctest {

    public static void main(String[] args) {

        long start, end, total;
        int port = 2710;
        String host = "pi.cs.oswego.edu";
        FileClient fc = new FileClient(host, port);
        String filepath = "/Users/jondntryniski/445/Project2/test.png";
        File file = new File(filepath);
        String filepath1 = "/Users/jondntryniski/445/Project2/test1.png";
        File file1 = new File(filepath1);
        double totalTime = 0;


        try {
//            for(int i = 0; i < 20; i++) {
//                start = System.nanoTime();
//                fc.wrq(file.getName(), file, 0);
//                fc.sendSequential(filepath);
//                end = System.nanoTime();
//                total = end - start;
//                System.out.println("Sequential RTT: " + (double) total / 1000000000 + " File size in bytes: " + file.length());
//                TimeUnit.SECONDS.sleep(5);
//                totalTime += (double) total / 1000000000;
//            }
//            System.out.println("First sequential avg: " + totalTime/20);
//            totalTime = 0;
//            for(int i = 0; i < 20; i++) {
//                start = System.nanoTime();
//                fc.wrq(file.getName(), file, 1);
//                fc.sendDropped(filepath);
//                end = System.nanoTime();
//                total = end - start;
//                System.out.println("Dropped RTT: " + (double) total / 1000000000 + " File size in bytes: " + file.length());
//                TimeUnit.SECONDS.sleep(2);
//                totalTime += (double) total / 1000000000;
//            }
//            System.out.println("Dropped avg: " + totalTime/20);
//            totalTime = 0;
//            for(int i = 0; i < 20; i++) {
                start = System.nanoTime();
                fc.wrq(file1.getName(), file1, 2);
                fc.sendWindow(filepath1);
                end = System.nanoTime();
                total = end - start;
                System.out.println("Windowed RTT: " + (double) total / 1000000000 + " File size in bytes: " + file.length());
                TimeUnit.SECONDS.sleep(2);
                totalTime += (double) total / 1000000000;
//            }
            System.out.println("Windowed avg: " + totalTime/20);
            }catch(Exception e){
            e.printStackTrace();
        }

//        System.out.println("Testing ipv6");
//
//        try {
//            FileClient fcv6 = new FileClient(host, 3710, InetAddress.getByName(host));
//            for(int i = 0; i < 20; i++) {
//                start = System.nanoTime();
//                fcv6.wrq(file1.getName(), file, 0);
//                fcv6.sendSequential(filepath1);
//                end = System.nanoTime();
//                total = end - start;
//                totalTime += (double) total / 1000000000;
//            }
//            System.out.println("IPv6 Sequential: " + totalTime/20);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    }

}
