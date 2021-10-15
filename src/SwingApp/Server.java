package SwingApp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Phat
 */
public class Server {

    private final int port;
    public static ArrayList<Socket> clientList;

    public Server(int port) {
        this.port = port;
    }

    private void excute() throws IOException {
        ServerSocket server = new ServerSocket(port);
        WriteServer write = new WriteServer();
        write.start();
        System.out.println("Server is listening");

        while (true) {
            Socket socket = server.accept();
            System.out.println("Đã kết nối với " + socket);
            Server.clientList.add(socket);
            ReadServer read = new ReadServer(socket);
            read.start();
        }

    }

    public static void main(String[] args) throws IOException {
        Server.clientList = new ArrayList<>();
        Server server = new Server(1234);
        server.excute();
    }
}

class WriteServer extends Thread {

    @Override
    public void run() {
        DataOutputStream dos = null;
        Scanner sc = new Scanner(System.in);
        while (true) {
            String message = sc.nextLine();
            try {
                for (Socket client : Server.clientList) {
                    dos = new DataOutputStream(client.getOutputStream());
                    dos.writeUTF("Server: " + message);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }
}

class ReadServer extends Thread {

    private Socket client;

    public ReadServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (DataInputStream dis = new DataInputStream(client.getInputStream())) {
            while (true) {
                String message = dis.readUTF();
                if (message.contains("exit")) {
                    Server.clientList.remove(client);
                    System.out.println("Đã ngắt kết nối với" + client);
                    dis.close();
                    client.close();
                    continue;
                }

                for (Socket c : Server.clientList) {
                    if (c.getPort() != client.getPort()) {
                        DataOutputStream dos = new DataOutputStream(c.getOutputStream());
                        dos.writeUTF(message);
                    }
                }
                System.out.println(message);
            }
        } catch (IOException e) {
            try {
                client.close();
                Server.clientList.remove(client);
                System.out.println("Đã ngắt kết nối với" + client);
                System.err.println(e.toString());
            } catch (IOException ex) {
                Logger.getLogger(ReadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

}
