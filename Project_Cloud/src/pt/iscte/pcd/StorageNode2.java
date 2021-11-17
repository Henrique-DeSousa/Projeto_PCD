package pt.iscte.pcd;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class StorageNode2 extends Thread {

    private static int serverPort = 8080;
    private static int clientPort = 8081;
    private InetAddress address;
    private static String fileName = "data.bin";
    private Socket socket;
    private static File file; //data.bin

    static private OutputStream directoryIn;
    static private  InputStream clientIn;
    private static String adrname = "localhost";
    private List<CloudByte> cloudByteList = new ArrayList<>();
    private List<Nodes> nodes = new ArrayList<Nodes>();


    public static void main(String[] args) throws IOException {
      /*  List<Workers> w = new ArrayList<>();

        for(int i = 0; i < 3; i++){

            Workers we = new Workers(adrname,serverPort);
            w.add(we);
        }*/
        new StorageNode2(adrname, 8080, clientPort+1, fileName).run();
    }

    @Override
    public void run() {
        try {
            startingUp();
            checkAvailableConnections();
            checkFileAvailability(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("finally");
        }
    }


    public StorageNode2(String nameAddress, int serverPort, int clientPort, String fileName) throws IOException {
        this.adrname = nameAddress;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.file = new File(fileName);
        this.address = InetAddress.getByName(null);
        this.socket = new Socket(address, serverPort);
    }


    private void checkAvailableConnections() throws IOException {

        Scanner scan = new Scanner(clientIn);
        System.out.println("Checking for available nodes:");
        String a = "nodes\n";
        this.directoryIn.write(a.getBytes());
        directoryIn.flush();
        String directoryNodesAvailable;

        while (true) {
            directoryNodesAvailable = scan.nextLine();
            addExistingNodes(directoryNodesAvailable);
            //System.out.println("Eco: " + directoryNodesAvailable);
            if (directoryNodesAvailable.equals("end")) {
                directoryIn.flush();
                System.out.println("test");
                getNode();
                break;
            }
        }
    }

    private void startingUp() throws IOException {


        System.out.println("Address you are connecting to: " + address);
        System.out.println("Socket connected is: " + this.socket);

        clientIn = socket.getInputStream();
        directoryIn = socket.getOutputStream();
        String s = "INSC " + address + " " + clientPort + "\n";
        directoryIn.write(s.getBytes()); //inscricao no directory
        directoryIn.flush();

    }

    private void checkFileAvailability(File file) throws FileNotFoundException {
        if (file.exists()){
            System.out.println("The file: " + file + " already exists!");
            sendData(file);
        }else{
            System.out.println("Downloading: " + fileName + " file!");
            receiveData(new File(fileName));
        }
    }

    private void sendData(File fileIn) throws FileNotFoundException {
        Scanner s = new Scanner(new BufferedReader(new FileReader(fileIn)));
        byte[] bytes = s.nextLine().getBytes();

        for (int i = 0; i < bytes.length - 1; i++) {
            cloudByteList.add(new CloudByte(bytes[i]));
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            BufferedInputStream buff = new BufferedInputStream((new FileInputStream(fileIn)));
            out.writeObject(cloudByteList);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(cloudByteList);
    }

    private void receiveData(File fileIns) throws FileNotFoundException {
        FileOutputStream fis = new FileOutputStream(fileIns);
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                CloudByte cb = (CloudByte) in.readObject();
                cloudByteList = (List<CloudByte>) cb;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println(cloudByteList);
    }

    public void addExistingNodes(String sta) throws IOException {
        if (sta.equals("end")) return;
        String str = sta;
        String[] temp = str.split("/");

        String test = temp[1];
        String[] testy = test.split(" ");
        int clientPort = Integer.parseInt(testy[1]);

        test = temp[0];
        testy = test.split(" ");
        nodes.add(new Nodes(testy[1],clientPort));
    }

    public void getNode(){
        nodes.forEach((z) -> System.out.println(z.getNode()));
    }


}