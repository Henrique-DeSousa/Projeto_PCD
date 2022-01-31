package pt.iscte.pcd;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;
import static java.lang.Thread.sleep;

public class StorageNode {

    public static void main(String[] args) throws IOException, InterruptedException {
        //localhost 8081 8080 data.bin
        if (args.length == 4) {
            new StorageNode(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]);
        } else if (args.length == 3) {
            new StorageNode(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), "");
        }
    }


    public StorageNode(String addressName, int clientPort, int serverPort, String fileName) throws IOException, InterruptedException {
        Socket socket = new Socket(addressName, serverPort);
        var cd = new ConnectingDirectory(addressName, clientPort, serverPort, socket);
        var fd = new FileData(fileName, clientPort, cd);
        var ei = new ErrorInjection(fd);
        ei.start();
        var ed1 = new ErrorDetection(fd);
        var ed2 = new ErrorDetection2(fd);
        sleep(10000);
        ed1.start();
        sleep(5000);
        ed2.start();
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
                    if (index > b.length - 1) {
                        System.err.println("Please insert a value lower or equal to: " + (b.length - 1));
                        break;
                    } else {
                        b[index].makeByteCorrupt();
                        System.out.println("Injecting error on byte: " + index);
                        System.out.println("Error Injection was successful! " + "\n" + b[index] + "\n");

                    }
                }
            }
        }
    }

    public class ErrorDetection extends Thread {

        FileData fd;
        CloudByte[] cB;

        public ErrorDetection(FileData fd) {
            this.fd = fd;
            this.cB = fd.getStoredData();
        }

        @Override
        public void run() {
            int index = 0;
            System.out.printf("Beginning Error detection from: " + index + "\n");
            for (index = 0; index < 500000; index++) {
                if (!cB[index].isParityOk()) {
                    System.out.println("ERROR DETECTED - INDEX:" + index);
                    fd.replaceErrorByte(index, fd.getClientIP());
                }
            }
            if (index == 499999) {
                index = -1;
            }
        }
    }

    public class ErrorDetection2 extends Thread {

        FileData fd;
        CloudByte[] cB;

        public ErrorDetection2(FileData fd) {
            this.fd = fd;
            this.cB = fd.getStoredData();
        }

        @Override
        public void run() {
            int index = 500000;
            System.out.printf("Beginning Error detection from: " + index + "\n");

            for (index = 500000; index < 1000000; index++) {
                if (cB[index].isParityOk() == false) {
                    System.out.println("ERROR DETECTED - INDEX:" + index);
                    fd.replaceErrorByte(index, fd.getClientIP());
                }
            }
            if (index == 999999) {
                index = 499999;
            }
        }
    }
}
