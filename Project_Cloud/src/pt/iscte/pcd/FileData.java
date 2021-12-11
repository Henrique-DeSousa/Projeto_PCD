package pt.iscte.pcd;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pt.iscte.pcd.FileData.*;

public class FileData {

    private static File file;
    private static String fileName;
    private static List<CloudByte> cloudByteList = new ArrayList<>();
    private static CloudByte[] storedData;
    private Download download = new Download();

    public FileData(String fileName) throws IOException {
        if (fileName == null) {
            this.fileName = "data1.bin";
            this.file = new File(this.fileName);
            download.start();
        } else {
            this.file = new File(fileName);
            this.fileName = fileName;
            fillingList();
        }
    }

    public void fillingList() throws IOException {
        byte[] fileContents = Files.readAllBytes(file.toPath());
        storedData = new CloudByte[fileContents.length];
        for (int i = 0; i != fileContents.length - 1; i++) {
            storedData[i] = new CloudByte(fileContents[i]);
            cloudByteList.add(new CloudByte(fileContents[i]));
        }
    }

    public static synchronized CloudByte[] getStoredData() {
        return storedData;
    }

    public static synchronized List<CloudByte> getCloudByteList() {
        return cloudByteList;
    }

    public static synchronized void putByte(byte[] bytes) {
        storedData = new CloudByte[bytes.length];
        for (int i = 0; i != bytes.length - 1; i++) {
            storedData[i] = new CloudByte(bytes[i]);
        }
    }

    public static File getFile() {
        return file;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFile(File file) {
        FileData.file = file;
    }

    /*--------------------------Download--------------------------*/

}

class Download extends Thread {
    @Override
    public void run() {
        try {
            var nodes = ConnectingDirectory.getNodes();
            for (int i = 0; i < nodes.size() - 1; i++) {
                if (!(nodes.get(i).getHostPort() == ConnectingDirectory.getHostIP())) {
                    downloadFile();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile() throws IOException {
        Socket socket = null;

        if (getFile().exists()) {
            System.out.println("File: " + getFile() + " exists.");
            Upload.temp();
        }
        socket = StorageNode.getServerSocket().accept();

        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream((OutputStream) FileData.getCloudByteList());

        int bytes = 0;
        long size = ois.readLong();
        byte[] buffer = new byte[100 * 10000];
        while (size > 0 && (bytes = ois.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            System.out.println("Downloading: " + (1000000 - size));
            oos.write(buffer, 0, bytes);
            size -= bytes;
            putByte(buffer);
            System.out.println(getStoredData());
        }
        System.out.println("Download Completed");
        ois.close();
        oos.close();
        socket.close();
    }

}

/*--------------------------Upload--------------------------*/

class Upload {

    public static void temp() throws IOException {
        if (getFile().exists()) {
            var nodes = ConnectingDirectory.getNodes();
            for (int i = 0; i < nodes.size() - 1; i++) {
                if (!(nodes.get(i).getHostPort() == ConnectingDirectory.getHostIP())) {
                    uploadFile(nodes.get(i).getHostPort());
                }
            }
        }
        new Download().start();
    }

    public static void uploadFile(int hostPort) throws IOException {
        Download download = new Download();
        int bytes = 0;
        Socket socket = new Socket("localhost", hostPort);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(getStoredData().toString().getBytes(StandardCharsets.UTF_8));
        ObjectInputStream ois = new ObjectInputStream((inputStream));
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println(oos);

        //FileInputStream fileInputStream = new FileInputStream("data.bin");
        //DataInputStream dis = new DataInputStream(fileInputStream);
        //DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        if (!getFile().exists()) {
            System.out.println("File doesn't exist." + "\nDownloading the file!");
            download.start();
        }
        oos.writeLong(new File("data.bin").length());
        byte[] buffer = new byte[100 * 10000];
        while ((bytes = ois.read(buffer)) != -1) {
            System.out.println("Uploading: " + (1000000 - bytes));
            oos.write(buffer, 0, bytes);
            oos.flush();
        }
        oos.close();
        ois.close();
    }

}

