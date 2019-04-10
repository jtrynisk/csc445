public class fstest {

    public static void main(String[] args){
        FileServer fs = new FileServer(2710);
        fs.run();
        fs.run();
        fs.run();
/*        FileServer fs1 = new FileServer(3710);
        fs1.run();
        fs1.run();*/
    }

}
