package pt.iscte.pcd;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConnectingDirectory {

    private final String hostName;
    private static int hostIP;
    private final InetAddress address;
    private InputStream in;
    private OutputStream out;
    private static final List<Nodes> nodes = new ArrayList<>();
    List<String> checkNodesList = new ArrayList<>();
    private final Socket socket;


    public ConnectingDirectory(String hostName, int hostIP, int directoryIP) throws IOException {
        this.hostName = hostName;
        ConnectingDirectory.hostIP = hostIP;
        this.address = InetAddress.getByName(hostName);
        this.socket = new Socket(address, directoryIP);
        signUp();
        askConnectedNodes();
    }

    public void signUp() throws IOException {
        System.out.println("You are connecting to the following address: " + hostIP + "\n");
        System.out.println("The port you are connected to: " + socket.getPort() + "\n");
        in = socket.getInputStream();
        out = socket.getOutputStream();
        out.write(generateSignUp(address, hostIP).getBytes());
        out.flush();
    }

    public String generateSignUp(InetAddress address, int hostIP) {
        String sign = "INSC ";
        return sign + address + " " + hostIP + "\n";
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
                printNodes();
                break;
            }
        }
    }

    public void addExistingNodes(String sta) {
        if (sta.equals("end")) return;
        if (!(checkNodesList.contains(sta))) {
            checkNodesList.add(sta);
            nodes.add(new Nodes(checkNodesList.get(checkNodesList.size() - 1)));
        }
    }

    public static List<Nodes> getNodes() {
        return nodes;
    }

    public void printNodes() {
        System.out.println("Checking for available nodes: \n");
        nodes.forEach((z) -> System.out.println(z.getNode()));
    }

    public static int getHostIP() {
        return hostIP;
    }
}
