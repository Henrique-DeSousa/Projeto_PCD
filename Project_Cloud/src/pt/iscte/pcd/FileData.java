package pt.iscte.pcd;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FileData {

    private File file;
    private String fileName;
    private CloudByte[] storedData;
    private List<Thread> threadsList;
    private ServerSocket serverSocket;
    private Queue<ByteBlockRequest> byteBlockRequests;
    private ConnectingDirectory cd;

    public FileData(String fileName, int clientIP, ConnectingDirectory cd) throws IOException {
        this.cd = cd;
        storedData = new CloudByte[1000000];
        threadsList = new LinkedList<>();
        if (fileName.equals("data.bin")) {
            fetchFileData();
        } else {
            downloadFileNodesConnected(clientIP);
        }
        server(clientIP);
    }

    public void fetchFileData() throws IOException {
        byte[] bytes = Files.readAllBytes(new File("data.bin").toPath());
        for (int i = 0; i != bytes.length; i++) {
            storedData[i] = new CloudByte(bytes[i]);
        }
        System.out.println(storedData.length);
    }

    private void server(int clientIP) {
        try {
            serverSocket = new ServerSocket(clientIP);
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            Socket socket = serverSocket.accept();
                            System.out.println("t");
                            clientHandler cH = new clientHandler(socket);
                            cH.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("2t");
            for (Thread trd : threadsList) {
                trd.join();
            }
            //goncalo stuff
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFileNodesConnected(int clientIP) throws IOException {
        PrintWriter out = cd.getOut();
        BufferedReader in = cd.getIn();

        out.println("nodes");
        byteBlockRequests = new LinkedList<>();
        int index = 0;
        int size = 100;

        for (int j = 0; j < 10000; j++) {
            ByteBlockRequest req = new ByteBlockRequest(index, size);
            byteBlockRequests.add(req);
            index += size;
        }
        String end = "";
        System.out.println("Downloading");
        try {
            while (!end.equals("end")) {
                end = in.readLine();
                String[] split = end.split(" ");
                if (split[0].equals("node") && (!split[2].contentEquals(String.valueOf(clientIP)))) {
                    System.out.println("Checking node: " + split[2]);
                    Download dw = new Download("localhost", Integer.parseInt(split[2]));
                    threadsList.add(dw);
                }
            }
            for (Thread trd : threadsList) {
                trd.start();
            }
        } catch (Exception e) {
        }
    }

    public CloudByte[] getStoredData() {
        return storedData;
    }

    public ByteBlockRequest remove() {
        synchronized (byteBlockRequests) {
            return byteBlockRequests.remove();
        }
    }

    public boolean isEmpty() {
        synchronized (byteBlockRequests) {
            return byteBlockRequests.isEmpty();
        }
    }

    public class Download extends Thread {

        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private int downloaded;
        private Socket socket;


        public Download(String address, int serverPort) throws IOException {
            socket = new Socket(InetAddress.getByName(address), serverPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            ByteBlockRequest bbr;
            while (!isEmpty()) {
                bbr = remove();
                try {
                    oos.writeObject(bbr);
                    CloudByte[] comeBack = (CloudByte[]) ois.readObject();
                    downloaded += bbr.getSize();
                    int index = 0;
                    for (int j = bbr.getIndex(); j < bbr.getIndex() + bbr.getSize(); j++) {
                        storedData[j] = comeBack[index];
                        index++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Data downloaded: " + downloaded + " bytes from: " + socket.getPort());
        }
    }

    public class clientHandler extends Thread {
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Socket socket;

        public clientHandler(Socket socket) throws IOException {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.socket = socket;
        }

        @Override
        public void run() {
            while (!socket.isClosed()) {
                try {
                    ByteBlockRequest byteBlockRequest = (ByteBlockRequest) in.readObject();
                    for (int i = byteBlockRequest.getIndex(); i < byteBlockRequest.getIndex() + byteBlockRequest.getSize(); i++) {
                        if (storedData[i].isParityOk() == false) {
                            System.out.println("Error detected ->" + storedData[i]);
                            //correçao de bits
                        }
                        out.writeObject(Arrays.copyOfRange(storedData, byteBlockRequest.getIndex(), byteBlockRequest.getIndex() + byteBlockRequest.getSize()));
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    try {
                        socket.close();
                        System.out.println("Connection terminated.");
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                }
            }
        }
    }


}


