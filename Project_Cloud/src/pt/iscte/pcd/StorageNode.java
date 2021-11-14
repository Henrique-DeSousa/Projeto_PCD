package pt.iscte.pcd;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


public class StorageNode implements Serializable {

    private static int serverPort;
    private final int clientPort;
    private ServerSocket serverSockets;
    private Socket socket;
    private PrintWriter directoryIn;
    private BufferedReader clientIn;
    private static String adrname; // = "localhost";
    private static String fileName; // = "data.bin";
    private List<CloudByte> cloudByteList = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        new StorageNode("localhost", "data.bin", 8080, 8081).runClient();
    }


    public void runClient() {
        try {
            connectToDirectory();
            checkAvailableConnections();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("closing");
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public StorageNode(String nameAddress, String filename, int serverPort, int clientPort) throws IOException {
        this.adrname = nameAddress;
        this.fileName = filename;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        serverSockets = new ServerSocket(clientPort);
    }

    private void connectToDirectory() throws IOException {

        InetAddress address = InetAddress.getByName(null);
        System.out.println("Address you are connecting to: " + address);
        this.socket = new Socket(address, serverPort);
        System.out.println("Socket connected is: " + this.socket);

        clientIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        directoryIn = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream())), true);

        directoryIn.println("INSC " + address + " " + clientPort); //inscricao no directory
    }

    private void checkAvailableConnections() throws IOException {
        System.out.println("Checking for available nodes:");
        this.directoryIn.println("nodes");
        String directoryNodesAvailable;
        while (true) {
            directoryNodesAvailable = this.clientIn.readLine();
            System.out.println("Eco: " + directoryNodesAvailable);
            if (directoryNodesAvailable.equals("end")) {
                sendData(directoryIn);
                break;
            }
        }
        this.directoryIn.flush();
        this.directoryIn.flush();
        System.out.println(directoryNodesAvailable.hashCode());
        System.out.println(directoryIn.toString().hashCode());
    }


    private void sendData(PrintWriter directoryIn) throws IOException {
        Scanner s = new Scanner(new BufferedReader(new FileReader(fileName)));
        byte[] bytes = s.nextLine().getBytes();

        for (int i = 0; i < bytes.length - 1; i++) {
            cloudByteList.add(new CloudByte(bytes[i]));
        }


       /* try {
            FileInputStream fis = new FileInputStream("data.bin");
                ObjectInputStream in = new ObjectInputStream(fis);
            CloudByte cb = (CloudByte) in.readObject();
            in.close();
        } catch(IOException e){ e.printStackTrace();
        }catch(ClassNotFoundException e){
        e.printStackTrace();}
        System.out.println(cloudByteList);
    }*/

        ObjectOutputStream out = new ObjectOutputStream("data.bin");
        Object cb = new Object(new CloudByte(cloudByteList.get(0)));
        cb.setState(100);

        System.out.println(cloudByteList);
    }
}