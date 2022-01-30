package pt.iscte.pcd;


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectingDirectory {

    private int directoryIP;
    private int hostIP;
    private InetAddress address;
    private BufferedReader in;
    private PrintWriter out;
    private final List<Nodes> nodes = new ArrayList<>();

    public ConnectingDirectory(String hostName, int hostIP, int directoryIP, Socket socket) throws IOException {
        this.directoryIP = directoryIP;
        this.hostIP = hostIP;
        this.address = InetAddress.getByName(hostName);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        signUp();
    }


    public void signUp() throws IOException {
        System.out.println("You are connecting to the following address: " + directoryIP + " Using: " + hostIP + "\n");
        out.println("INSC " + address + " " + hostIP);
    }

    public PrintWriter getOut() {
        return this.out;
    }

    public BufferedReader getIn() {
        return this.in;
    }

    public int getDirectoryIP() {
        return directoryIP;
    }

    public int getHostIP() {
        return hostIP;
    }

    public InetAddress getAddress() {
        return address;
    }


}
