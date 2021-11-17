package pt.iscte.pcd;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Workers extends Thread{

    private static String hostName; //localhost
    int serverSocket;// = 8080;


    Socket socket;
    private OutputStream directoryIn;
    private InputStream clientIn;

   public Workers(String hostName, int serverSocket) throws IOException {
       this.hostName = hostName;
       this.serverSocket = serverSocket;
       this.socket = new Socket(hostName, serverSocket);
       clientIn = socket.getInputStream();
       directoryIn = socket.getOutputStream();
   }
    @Override
    public void run() {
    }





}
