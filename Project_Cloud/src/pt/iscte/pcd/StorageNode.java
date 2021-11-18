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


public class StorageNode extends Thread {

    private static int serverPort = 8080;
    private static int clientPort = 8081;
    private InetAddress address;
    private static String fileName = "data.bin";
    private Socket socket;
    private static File file; //data.bin
    private static String adrname = "localhost";


    private List<Nodes> nodes = new ArrayList<Nodes>();



    public static void main(String[] args) throws IOException {
       /* List<Workers> w = new ArrayList<>();

        for(int i = 0; i < 3; i++){

            Workers we = new Workers(adrname,serverPort);
            w.add(we);
        }*/

        ConnectingDirectory cd = new ConnectingDirectory(adrname, clientPort, serverPort);
        cd.signUp();
        FileInfo fi = new FileInfo(new File(fileName),adrname);
        Download down = new Download(new File(fileName), adrname);
        Upload up = new Upload(new File(fileName), adrname);
        up.uploladFile(new File(fileName));
        //new StorageNode(adrname, 8080, clientPort, "null").run();
    }

   /* @Override
    public void run() {
        try {
           checkAvailableConnections();
            checkFileAvailability(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("finally");
        }
    }*/


    public StorageNode(String nameAddress, int serverPort, int clientPort, String fileName) throws IOException {
        this.adrname = nameAddress;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.file = new File(fileName);
        this.address = InetAddress.getByName(null);
        this.socket = new Socket(address, serverPort);
    }


    /*private void checkAvailableConnections() throws IOException {

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
    }*/


/*
    private void checkFileAvailability(File file) throws FileNotFoundException {
        if (file.exists()){
            System.out.println("The file: " + file + " already exists!");
            sendData(file);
        }else{
            System.out.println("Downloading: " + fileName + " file!");
            receiveData(new File(fileName));
        }
    }
*/




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