package backend;

import java.util.Scanner;

public class tester {

    public static void main(String args[]) {

        Client c = new Client();

        while (true) {

            Thread listener = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MessagePacket mp = c.receive();
                        System.out.println("ID: " + mp.getId().trim() + ", TimeStamp: " + mp.getTimeStamp() + ", Message: " + mp.getMessage().trim());

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

            try {
                c.connect("239.0.0.193");
                Scanner sc = new Scanner(System.in);
                System.out.print("Please enter and id: ");
                String id = sc.nextLine();
                System.out.print("Please enter a message: ");
                String message = sc.nextLine();
                c.send(id, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

