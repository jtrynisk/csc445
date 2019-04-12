import java.util.concurrent.TimeUnit;

public class fstest {

    public static void main(String[] args){
        try{
        FileServer fs = new FileServer(2710);
        fs.run();
        }catch(Exception e){}

    }

}
