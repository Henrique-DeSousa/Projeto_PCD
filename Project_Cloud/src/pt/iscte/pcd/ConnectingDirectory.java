package pt.iscte.pcd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConnectingDirectory{

    private String hostName;
    private int hostIP;
    private int directoryIP;
    private InetAddress address;
    private InputStream in;
    private OutputStream out;
    private List<Nodes> nodes = new ArrayList<>();
    private Socket socket;
    private String sign = "INSC ";


    public ConnectingDirectory(String hostName, int hostIP, int directoryIP) throws IOException {
        this.hostName = hostName;
        this.hostIP = hostIP;
        this.directoryIP = directoryIP;
        this.address = InetAddress.getByName(hostName);
        this.socket = new Socket(address, directoryIP);
    }

    public void signUp() throws IOException {

        System.out.println("You are connecting to the following address: " + this.address + "\n");

        System.out.println("The port you are connected to: " + this.socket.getPort() + "\n");
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        out.write(generateSignUp(this.address, this.hostIP).getBytes());
        out.flush();
    }

    public String generateSignUp(InetAddress address, int hostIP){
        String signUpString = sign + address + " " + hostIP + "\n";
        return signUpString;
    }

    public void askConnectedNodes() throws IOException {
        String directoryNodesAvailable;
        String a = "nodes\n";
        out.write(a.getBytes());
        out.flush();
        Scanner scan = new Scanner(in);

        while (true) {
            directoryNodesAvailable = scan.nextLine();
            addExistingNodes(directoryNodesAvailable);
            //System.out.println("Eco: " + directoryNodesAvailable);
            if (directoryNodesAvailable.equals("end")) {
                out.flush();
                getNode();
                break;
            }
        }
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
        System.out.println("Checking for available nodes: \n");
        nodes.forEach((z) -> System.out.println(z.getNode()));
    }

    public Socket getSocket() {
        return this.socket;
    }

}
