package pt.iscte.pcd;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import static pt.iscte.pcd.FileData.*;
import static pt.iscte.pcd.FileData.getStoredData;

public class FileData {

    private static File file;
    private static String fileName;
    private static CloudByte[] storedData;
    private static ByteBlockRequest BBR;

    public FileData(String fileName) throws IOException {
        if (fileName == null) {
            FileData.fileName = "data1.bin";
            file = new File(this.fileName);
        } else {
            file = new File(fileName);
            FileData.fileName = fileName;
            fillingBBR();
        }
    }

    public void fillingBBR() throws IOException {
        byte[] fileContents = Files.readAllBytes(file.toPath());
        storedData = new CloudByte[fileContents.length];
        for (int i = 0; i != fileContents.length; i++) {
            storedData[i] = new CloudByte(fileContents[i]);
        }
        BBR = new ByteBlockRequest(storedData);
    }

    public static synchronized CloudByte[] getStoredData() {
        return storedData;
    }

    public static synchronized void putByte(byte[] bytes) {
        CloudByte[] storedData2 = new CloudByte[bytes.length];
        for (int i = 0; i != bytes.length; i++) {
            storedData2[i] = new CloudByte(bytes[i]);
            System.out.println(storedData2[i]);
        }

    }

    public static File getFile() {
        return file;
    }

    /*--------------------------Download--------------------------*/

}

class Download extends Thread {
    CloudByte[] list = new CloudByte[1000000];

    @Override
    public void run() {
        try {
            var nodes = ConnectingDirectory.getNodes();
            for (Nodes node : nodes) {
                if (!(node.getHostPort() == ConnectingDirectory.getHostIP())) {
                    downloadFile();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public synchronized void downloadFile() throws IOException, ClassNotFoundException {

        Socket socket = ConnectingDirectory.getServerSocket().accept();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        for (int j = 0; j <= 9999; j += 1) {
            byte[] bit = (byte[]) ois.readObject();
            for (int i = 0; i < bit.length; i++) {
                list[i + j * 100] = new CloudByte(bit[i]);
            }
        }
        System.out.println("Download Completed");
        fileWriting(getFile().getName(), list);
    }

    public void fileWriting(String filename, CloudByte[]x) throws IOException{
        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter(filename));
        for (int i = 0; i < x.length; i++) {
            outputWriter.write(x[i]+"");
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }
}

/*--------------------------Upload--------------------------*/

class Upload extends Thread {
    @Override
    public void run() {
        if (getFile().exists()) {
            var nodes = ConnectingDirectory.getNodes();
            for (Nodes node : nodes) {
                try {
                    uploadFile(node.getHostPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Download().start();
        }
    }

    public static void uploadFile(int hostPort) throws IOException {

        Socket socket = new Socket("localhost", hostPort);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ByteBlockRequest bbr = new ByteBlockRequest(getStoredData());
        for (int j = 0; j <= 9999; j += 1) {
            objectOutputStream.writeObject(bbr.blocksToSend(j));
        }
        objectOutputStream.flush();
        System.out.println("Uploaded all the Data!");

    }

}


