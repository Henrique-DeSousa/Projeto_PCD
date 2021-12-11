package pt.iscte.pcd;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pt.iscte.pcd.FileData.getCloudByteList;
import static pt.iscte.pcd.FileData.getFile;

public class FileData {

    private static File file;
    private static String fileName;
    private static List<CloudByte> cloudByteList = Collections.synchronizedList(new ArrayList<>());

    public FileData(String fileName) throws IOException {
        if (fileName == null) {
            this.fileName = "data2.bin";
            this.file = new File(this.fileName);
            Download.downloadFile();
        } else {
            this.file = new File(fileName);
            this.fileName = fileName;
            fillingList();
        }
    }

    public void fillingList() throws IOException {
        byte[] fileContents = Files.readAllBytes(file.toPath());
        for (int i = 0; i < fileContents.length - 1; i++) {
            cloudByteList.add(new CloudByte(fileContents[i]));
        }
    }

    public static List<CloudByte> getCloudByteList() {
        return cloudByteList;
    }

    public static File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public static void setFile(File file) {
        FileData.file = file;
    }

    /*--------------------------Download--------------------------*/

}

class Download extends Thread {
    static ConnectingDirectory connectingDirectory;

    @Override
    public void run() {
        try {
            downloadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile() throws IOException {

        var nodes = ConnectingDirectory.getNodes();

        Socket socket = null;

        if (getFile().exists()) {
            System.out.println("File: " + getFile() + " exists.");
            new Upload().uploadFile();
        }

        FileOutputStream fos = new FileOutputStream(FileData.getFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);


        for (int i = 0; i < nodes.size() - 1; i++) {
            if (!(nodes.get(i).getHostPort() == ConnectingDirectory.getHostIP())) {
                System.out.println("test33123");

                ServerSocket serverSocket = new ServerSocket(nodes.get(i).getHostPort());
                System.out.println(serverSocket);
                socket = serverSocket.accept();
                System.out.println("now socket");
                System.out.println(socket);
                //socket = new Socket(nodes.get(i).getName(), nodes.get(i).getHostPort());
                //System.out.println(socket);
                int bytes = 0;
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                long size = ois.readLong();
                System.out.println(size);
                byte[] buffer = new byte[100 * 10000];
                while (size > 0 && (bytes = ois.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    System.out.println("test3333");
                    oos.write(buffer, 0, bytes);
                    size -= bytes;
                }
            }
        }
    }
}

/*--------------------------Upload--------------------------*/

class Upload { //extends Thread
    // private ConnectingDirectory connectingDirectory;


    public void uploadFile() throws IOException {
        int bytes = 0;
        var nodes = ConnectingDirectory.getNodes();
        InputStream fileInputStream = new FileInputStream("data.bin");
        DataInputStream ois = new DataInputStream(fileInputStream);

        if (!getFile().exists()) {
            System.out.println("File doesn't exist." + "\nDownloading the file!");
            new Download().downloadFile();
        }
        System.out.println("hello");
        for (int i = 0; i < nodes.size() - 1; i++) {
            System.out.println(!(nodes.get(i).getHostPort() == ConnectingDirectory.getHostIP()));
            System.out.println(nodes.get(i).getHostPort());
            System.out.println(ConnectingDirectory.getHostIP());
            System.out.println("hello2");
            Socket socket = new Socket(nodes.get(i).getName(), nodes.get(i).getHostPort());
            ObjectOutputStream dos = new ObjectOutputStream(socket.getOutputStream());
            dos.writeLong(new File("data.bin").length());
            byte[] buffer = new byte[100 * 10000];
            while ((bytes = ois.read(buffer)) != -1) {
                dos.write(buffer, 0, bytes);
                dos.flush();
            }
        }
    }
}