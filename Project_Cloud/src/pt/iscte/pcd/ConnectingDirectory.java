package pt.iscte.pcd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectingDirectory{

    private String hostName;
    private int hostIP;
    private int directoryIP;
    private InetAddress address;



    private Socket socket;
    String sign = "INSC ";



    public ConnectingDirectory(String hostName, int hostIP, int directoryIP) throws IOException {
        this.hostName = hostName;
        this.hostIP = hostIP;
        this.directoryIP = directoryIP;
        this.address = InetAddress.getByName(hostName);
        this.socket = new Socket(address, directoryIP);
    }

    public ConnectingDirectory() throws IOException {
        this.hostName = "localhost";
        this.hostIP = 8081;
        this.directoryIP = 8080;
        this.address = InetAddress.getByName(hostName);
        this.socket = new Socket(address, directoryIP);

    }

    public void signUp() throws IOException {

        System.out.println("You are connecting to the following address: " + this.address + "\n");

        System.out.println("The port you are connected to: " + this.socket.getPort() + "\n");
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        //String signUpSring = "INSC " + address + " " + hostIP + "\n";
        //out.write(signUpSring.getBytes());
        out.write(generateSignUp(this.address, this.hostIP).getBytes());
        out.flush();
    }

    public String generateSignUp(InetAddress address, int hostIP){
        String signUpString = sign + address + " " + hostIP + "\n";
        return signUpString;
    }

    public void askConnectedNodes(){

    }

    public Socket getSocket() {
        return this.socket;
    }

}
