import java.util.*;
public class fsmain {

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.print("What port would you like the server to run on? ");
        int port = sc.nextInt();
        FileServer fs = new FileServer(port);
        fs.run();
    }

}
