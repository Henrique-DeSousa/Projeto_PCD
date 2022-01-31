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
import java.util.concurrent.CountDownLatch;

public class FileData {

    private CloudByte[] storedData;
    private List<Thread> threadsList;
    private ServerSocket serverSocket;
    private Queue<ByteBlockRequest> byteBlockRequests;
    private ConnectingDirectory cd;
    private List<CloudByte> correctParity;
    private int clientIP;

    public FileData(String fileName, int clientIP, ConnectingDirectory cd) throws IOException {
        this.cd = cd;
        this.clientIP = clientIP;
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
    }

    private void server(int clientIP) {
        try {
            serverSocket = new ServerSocket(clientIP);
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();
                        clientHandler cH = new clientHandler(socket);
                        cH.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            for (Thread trd : threadsList) {
                trd.join();
            }
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFileNodesConnected(int clientIP){
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
            e.printStackTrace();
        }
    }

    public int getClientIP() {
        return clientIP;
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
                } catch (IOException | ClassNotFoundException e) {
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
                        if (!storedData[i].isParityOk()) {
                            System.out.println("Error detected ->" + storedData[i]);
                            replaceErrorByte(i, getClientIP());
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


    public void replaceErrorByte(int i, int clientIP) {
        PrintWriter out = cd.getOut();
        BufferedReader in = cd.getIn();


        ByteBlockRequest bbr = new ByteBlockRequest(i, 1);
        out.println("nodes");
        CountDownLatch count = new CountDownLatch(2);
        correctParity = new LinkedList<>();
        String end = "";
        try {
            threadsList.clear();
            while (!end.equals("end")) {
                end = in.readLine();
                String[] split = end.split(" ");
                if (split[0].equals("node") && (!split[2].contentEquals(String.valueOf(clientIP)))) {
                    System.out.println("Checking node: " + split[2]);
                    Replace r = new Replace("localhost", Integer.parseInt(split[2]), bbr, count);
                    threadsList.add(r);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Thread t : threadsList) {
            t.start();
        }
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int byteCorrect = 0;
        while (byteCorrect != 1) {
            for (int k = 0; k < correctParity.size(); k++) {
                for (int j = 0; j < correctParity.size(); j++) {
                    if (correctParity.get(k) == correctParity.get(j)) {
                        byteCorrect = 1;
                        storedData[i] = correctParity.get(k);
                        System.out.println("Content found! New value: " + correctParity.get(k));
                        return;
                    }
                    j++;
                }
                k++;
            }
        }
    }


    public synchronized void addByte(CloudByte c) {
        correctParity.add(c);
    }


    public class Replace extends Thread {

        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private ByteBlockRequest bbr;
        private CountDownLatch count;

        public Replace(String Address, int clientIP, ByteBlockRequest bbr, CountDownLatch count) throws IOException {
            this.bbr = bbr;
            this.count = count;
            socket = new Socket(InetAddress.getByName(Address), clientIP);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            try {
                out.writeObject(bbr);
                CloudByte result = ((CloudByte[]) in.readObject())[0];
                addByte(result);
                count.countDown();
                System.out.println("Node info ->" + socket.getInetAddress().toString() + " " + socket.getPort() + ": " + result.toString());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}


