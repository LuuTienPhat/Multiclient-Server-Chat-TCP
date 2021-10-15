package ConsoleApp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Phat
 */
public class Client2 {

    private String host;
    private int port;

    public Client2(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void excute() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Nhập vào tên của bạn");
        String name = sc.nextLine();

        Socket client = new Socket(host, port);
        ReadClient read = new ReadClient(client);
        read.start();
        WriteClient write = new WriteClient(client, name);
        write.start();
    }

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 1234;

        Client2 client = new Client2(host, port);
        client.excute();
    }
}

class ReadClient extends Thread {

    private Socket client;

    public ReadClient(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(client.getInputStream());
            while (true) {
                String message = dis.readUTF();
                System.out.println(message);
            }

        } catch (IOException e) {
            try {
                dis.close();
                client.close();
            } catch (IOException ex) {
                System.out.println("Ngắt kết nối Server");
            }
        }
    }

}

class WriteClient extends Thread {

    Socket client;
    String name;

    public WriteClient(Socket client, String name) {
        this.client = client;
        this.name = name;
    }

    public void run() {
        DataOutputStream dos = null;
        Scanner sc = null;

        try {
            dos = new DataOutputStream(client.getOutputStream());
            sc = new Scanner(System.in);

            while (true) {
                String message = sc.nextLine();
                dos.writeUTF(name + ": " + message);
            }
        } catch (IOException e) {
            try {
                dos.close();
                client.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
