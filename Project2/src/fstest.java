import java.util.concurrent.TimeUnit;

public class fstest {

    public static void main(String[] args){
        try{
        FileServer fs = new FileServer(2710);
//        fs.run();
//        fs.run();

//            FileServer fs1 = new FileServer(3710);
            for (int i = 0; i < 40; i++) {
                fs.run();
            }
        }catch(Exception e){}

    }

}
