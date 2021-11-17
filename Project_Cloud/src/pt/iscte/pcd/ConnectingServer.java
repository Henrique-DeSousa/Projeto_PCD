package pt.iscte.pcd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLOutput;

public class ConnectingServer {

    private String hostName;
    private int hostIP;
    private int directoryIP;
    private InetAddress address;
    private Socket socket;
    private final String sign = "INSC ";


    public ConnectingServer(String hostName, int hostIP, int directoryIP) throws IOException {
        this.hostName = hostName;
        this.hostIP = hostIP;
        this.directoryIP = directoryIP;
        this.address = InetAddress.getByName(null);
        this.socket = new Socket(this.address, this.directoryIP);
    }

    public void signUp() throws IOException {

        System.out.println("You are connecting to the following address: " + this.address + "\n");

        System.out.println("The port you are connected to: " + this.socket.getPort() + "\n");
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        out.write(generateSignUp(this.address, this.hostIP).getBytes());
    }

    public String generateSignUp( InetAddress address, int hostIP){
        String signUpString = sign + address + hostIP + "\n";
        return signUpString;
    }

}
