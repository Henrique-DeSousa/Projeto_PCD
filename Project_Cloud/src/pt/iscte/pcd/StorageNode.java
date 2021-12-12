package pt.iscte.pcd;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.regex.Pattern;

import static pt.iscte.pcd.FileData.*;

public class StorageNode extends Thread {

    private static int serverPort;
    private static int clientPort;
    private static String addressName = "localhost";

    private static FileData fileData;
    static ErrorInjection errorInjection;
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException{
        //localhost 8081 8080 data.bin
        fileData = new FileData(null);
        if (args.length >= 3) {
            addressName = args[0];
            clientPort = Integer.parseInt(args[1]);
            serverPort = Integer.parseInt(args[2]);
            if(args.length == 4){
                fileData = new FileData(args[3]);
            }
        }else return;

        new StorageNode(addressName, clientPort, serverPort);
        errorInjection = new ErrorInjection();
        errorInjection.start();
        new Upload().temp();
    }



    public StorageNode(String addressName, int clientPort, int serverPort) throws IOException {
        serverSocket = new ServerSocket(clientPort);
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

    public void injection() {
        System.out.println(getBBR().getByteArray().length);
        Scanner scan = new Scanner(System.in);
        while (true) {
            String error = scan.nextLine();
            if (Pattern.compile(":\s[0-9]+").matcher(error).find() || Pattern.compile(":\s+-[0-9]+").matcher(error).find()) { // Accepts both positives and negative ints
                int index = Integer.parseInt(error.replaceAll("\\D+", "")); // replaces EVERYTHING that's not a number, -1 becomes 1.
                System.out.println(index);

                if (index > getBBR().getByteArray().length - 1) {
                    System.err.println("Please insert a value lower or equal to: " + (getBBR().getByteArray().length - 1));
                    break;
                } else {
                    CloudByte[] b = getStoredData();
                    b[index].makeByteCorrupt();
                    System.out.println(b[index]);
                    System.out.println("Injecting error on byte: " + index);
                }
            }
        }
    }
}