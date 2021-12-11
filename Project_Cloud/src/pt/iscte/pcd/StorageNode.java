package pt.iscte.pcd;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.regex.Pattern;

import static pt.iscte.pcd.FileData.*;

public class StorageNode extends Thread {

    private static int serverPort = 8080;
    private static int clientPort = 8082;
    private static String fileName = null;
    private static String addressName = "localhost";


    private static ConnectingDirectory connectingDirectory;
    private static FileData fileData;
    static ErrorInjection errorInjection;
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException, InterruptedException {

       /* if (args.length > 3) {
            addressName = args[0];
            serverPort = Integer.parseInt(args[1]);
            clientPort = Integer.parseInt(args[2]);
            fileData = new FileData(args[3]);
        } else {
            fileName = null;
            fileData = new FileData(fileName);
        }*/
        new StorageNode("localhost", 8081, 8080);
        fileData = new FileData(fileName);
        errorInjection = new ErrorInjection();
        errorInjection.start();
        if (fileData.getFile().exists()) {
        } else {
            new Upload().temp();
        }
    }

    public StorageNode(String addressName, int clientPort, int serverPort) throws IOException {
        this.serverSocket = new ServerSocket(clientPort);
        new ConnectingDirectory(addressName, clientPort, serverPort);
    }

    public static ServerSocket getServerSocket() {
        return serverSocket;
    }

}


class ErrorInjection extends Thread {

    FileData fileData;

    @Override
    public void run() {
        injection();
    }

    private String error;

    public void injection() {
        System.out.println(getCloudByteList().size());
        Scanner scan = new Scanner(System.in);
        while (true) {
            error = scan.nextLine();
            if (Pattern.compile(":\s[0-9]+").matcher(error).find() || Pattern.compile(":\s+-[0-9]+").matcher(error).find()) { // Accepts both positives and negative ints
                int index = Integer.parseInt(error.replaceAll("\\D+", "")); // replaces EVERYTHING that's not a number, -1 becomes 1.
                System.out.println(index);

                if (index > fileData.getCloudByteList().size() - 1) {
                    System.err.println("Please insert a value lower or equal to: " + (getCloudByteList().size() - 1));
                    break;
                } else {
                    fileData.getCloudByteList().get(index).makeByteCorrupt();
                    System.out.println(getCloudByteList());
                    System.out.println("Injecting error on byte: " + index);
                }
            }
        }
    }
}