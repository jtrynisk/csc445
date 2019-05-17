import java.io.IOException;

public class tester {

    public static void main(String args[]) {

        Client c = new Client();

        try {
            c.connect("224.0.0.193");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

