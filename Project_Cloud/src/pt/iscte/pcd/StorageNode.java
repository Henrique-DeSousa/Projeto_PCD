package pt.iscte.pcd;

import java.io.IOException;

import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class StorageNode {

    public static void main(String[] args) throws IOException, InterruptedException {
        //localhost 8081 8080 data.bin
        if (args.length == 4) {
            new StorageNode(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]);
        } else if (args.length == 3) {
            new StorageNode(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), "");
        }
    }


    public StorageNode(String addressName, int clientPort, int serverPort, String fileName) throws IOException {
        Socket socket = new Socket(addressName, serverPort);
        var cd = new ConnectingDirectory(addressName, clientPort, serverPort, socket);
        var fd = new FileData(fileName, clientPort, cd); //FileData(String fileName, int clientIP)
        var ei = new ErrorInjection(fd);
        ei.start();
    }


    static class ErrorInjection extends Thread {

        FileData fd;

        public ErrorInjection(FileData fd) {
            this.fd = fd;
        }

        @Override
        public void run() {
            CloudByte[] b = fd.getStoredData();
            Scanner scan = new Scanner(System.in);
            while (true) {
                String error = scan.nextLine();
                if (Pattern.compile(":\s[0-9]+").matcher(error).find() || Pattern.compile(":\s+-[0-9]+").matcher(error).find()) { // Accepts both positives and negative ints
                    int index = Integer.parseInt(error.replaceAll("\\D+", "")); // replaces EVERYTHING that's not a number, -1 becomes 1.
                    System.out.println(index);
                    if (index > b.length - 1) {
                        System.err.println("Please insert a value lower or equal to: " + (b.length - 1));
                        break;
                    } else {
                        b[index].makeByteCorrupt();
                        System.out.println(b[index]);
                        System.out.println("Injecting error on byte: " + index);
                    }
                }
            }
        }
    }
}
