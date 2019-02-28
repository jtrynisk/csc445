package Proj1;

import java.io.IOException;
import java.net.*;
import java.io.*;

public class EchoClient {

    public static void main(String[] args){
        String host = "pi.cs.oswego.edu";

        //Initializing variables
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        DataOutputStream outStream = null;

        long start = 0;
        byte[] m1 = new byte[1000];
        byte[] m2 = new byte[16000];
        byte[] m3 = new byte[64000];
        byte[] m4 = new byte[256000];
        byte[] m5 = new byte[1000000];
        byte[] m6 = new byte[1024];
        byte[] m7 = new byte[512];
        byte[] m8 = new byte[256];

        try{
            //Define variables
            echoSocket = new Socket(host, 2710);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            outStream = new DataOutputStream(new BufferedOutputStream(echoSocket.getOutputStream()));
        }
        catch(UnknownHostException e){
            //Handles host being incorrect
            System.out.println("Host " + host + " unavailable.");
            System.exit(1);
        }
        catch(IOException err){
            //Handles not being able to connect to server
            System.out.println("IO connection failed.");
            System.exit(1);
        }

        try{
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

            //First request
            start = System.nanoTime();
            out.println("a");
            System.out.println("echo: " + in.readLine() + " RTT: " + (System.nanoTime() - start));

            //Second Request
            start = System.nanoTime();
            out.println("X00HaD9jnnfzAz4MRjHZ1gXZ518nQmOyOfh1eJBavHCeqweJW0xaR5Tf3pOuXiZY");
            System.out.println("echo: " + in.readLine() + " RTT: " + (System.nanoTime() - start));

            //Third Request
            start = System.nanoTime();
            out.println("ySzjRPNtlHH7RYNhnoMayvWzulP5VefpgeQQYanZZEeDXXdpGmFT44LRwXosEJPc2QVd8fYQPjLmJ7YQi2NIRkZtBNvnmP7i9umHoKQ3T66QKXhPo25mT2lVxTGy8iQ5iXzHd2u9CbTCbiIfyGXYI0m5a5lTtOr0JbIs2emdAVhzj4JHAEjOVLdd9uIgvO0rxuH5pin8ZogKMTfckKpfcfX0zAa6xJt10omsQS1Tm6M051FMhiAgyhg8MFyhK4vIT0xkz5FOKTUjiz2yAOgSse8q15gu9KGdCfX79Ha3eguaBe5lvSLhYV4VROd8Xbj1z2BgKJiqIMBiVjquPQ1CzUbCenIIjz2cHvRkMWvXvv6aGwFjGlRZFJZZsSzBfVaWJj9cbDz03oHFvOpWijcHMoy2UH9ABtByLDsehaTW63JLIZb7R9TerxTvdUTj79Jg8YRZOpwkwEbOMTlPkmiiqxZIvRVbdi9CQD4IkqADiL3jl1s1RqYnW4avS3FMNw0R60kQkOIN0zfeu6iD0TGJTzDKgEkAmMUcEgGsBV7yl4afuUqCnzxnH9xOiYuAk45hSJ4raU8mBOwT9nlwoOZpdO7ZP750rK6gyk4YhwByVbJGTI4CCeprIfVsnnYXYHZZ5w3255OImkwNvDz8g1Ydre2qYouwgJhkFC1oUNjN8SmYxI6PM3dGHmdEtpCs4nNoFUoJh1Cthg7TiN959zV6hTF1XozQEh3U612ZIpESBz7l5z4g7M7tEh2mh6tbRzmcP3sIsswTsAR7ili43cTs5VeK979RRjFIQRxfmN3jGBhP3jC945tr0i2GHha7nM5QfQN4bmkabN1JF99kpdeG1HpumOQNVBPrvdwc6p53Qh9IejHp4sw6SMFoONp0RJf5jbeJOeq5EpAYdatE7A8LUrJ9sCT7EV6V7ANYq7dhLhBkPaX39ulw5SgmgrG7uO6skTXUKhZDXli2TC8NuRKJQxheVToRktJIg4jHVljT4l2sR5IBxXW3ahXBUDtx9sx6");
            System.out.println("echo: " + in.readLine() + " RTT: " + (System.nanoTime() - start));

            //First throughput measurement of 1k
            start = System.nanoTime();
            outStream.write(m1);
            outStream.flush();
            System.out.println("RTT of 1k message: " + (System.nanoTime() - start));

            //Second throughput measurement of 16k
            start = System.nanoTime();
            outStream.write(m2);
            outStream.flush();
            System.out.println("RTT of 16k message: " + (System.nanoTime() - start));

            //Third throughput measurement of 64k
            start = System.nanoTime();
            outStream.write(m3);
            outStream.flush();
            System.out.println("RTT of 64k message: " + (System.nanoTime() - start));

            //Fourth throughput measurement of 256k
            start = System.nanoTime();
            outStream.write(m4);
            outStream.flush();
            System.out.println("RTT of 256k message: " + (System.nanoTime() - start));

            //Fifth throughput measurement of 1M
            start = System.nanoTime();
            outStream.write(m5);
            outStream.flush();
            System.out.println("RTT of 1M message: " + (System.nanoTime() - start));

            //1024, 1024byte messages
            start = System.nanoTime();
            for (int i = 0; i < 1024; i++){
                outStream.write(m6);
            }
            outStream.writeChars("a");
            System.out.println("RTT of 1024, 1024 byte messages: " + (System.nanoTime() - start) + " with response: " + in.readLine());

            //2048, 512 byte messages
            start = System.nanoTime();
            for (int i = 0; i < 2048; i++){
                outStream.write(m7);
            }
            outStream.writeChars("a");
            System.out.println("RTT of 2048, 512 byte messages: " + (System.nanoTime() - start) + " with response: " + in.readLine());

            //4096, 256 byte messages
            start = System.nanoTime();
            for (int i = 0; i < 4096; i++){
                outStream.write(m8);
            }
            outStream.writeChars("a");
            System.out.println("RTT of 4096, 256 byte messages: " + (System.nanoTime() - start) + " with response: " + in.readLine());


            //Clean up, closes the port, printwriter, and bufferedreader
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
