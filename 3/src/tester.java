import java.io.IOException;

public class tester {

    public static void main(String args[]) {

        Client c = new Client();

        try {
            c.connect("239.0.0.193");
            c.send("Jondn", "This is a test");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

