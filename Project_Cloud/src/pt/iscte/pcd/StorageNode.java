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






    public static void main(String[] args) throws IOException {
       /* List<Workers> w = new ArrayList<>();

        for(int i = 0; i < 3; i++){

            Workers we = new Workers(adrname,serverPort);
            w.add(we);
        }*/

        ConnectingDirectory cd = new ConnectingDirectory(adrname, clientPort, serverPort);
        ErrorInjection ei = new ErrorInjection(fileName ,adrname, adrname, clientPort, serverPort);
        cd.signUp();
        cd.askConnectedNodes();
        FileInfo fi = new FileInfo(fileName,adrname, adrname, clientPort, serverPort);
        Download down = new Download(fileName, adrname, adrname, clientPort, serverPort);
        Upload up = new Upload( fileName , adrname, adrname, clientPort, serverPort);
        ei.injection();
        up.uploadFile(new File(fileName));





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







}