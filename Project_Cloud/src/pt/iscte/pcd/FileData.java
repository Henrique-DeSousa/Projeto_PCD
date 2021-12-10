package pt.iscte.pcd;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileData {

    private static File file;
    private static String fileName;
    private static ConnectingDirectory connectingDirectory;
    private static List<CloudByte> cloudByteList = Collections.synchronizedList(new ArrayList<>());

    public FileData(String fileName) throws IOException {
        if (fileName == (null)) {
            Download.downloadFile();
        }else{
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

    /*--------------------------Download--------------------------*/


    public static class Download extends Thread {

        @Override
        public void run() {
            try {
                downloadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void downloadFile() throws IOException {
            if (file != null) {
                System.out.println("File: " + getFile() + " exists.");
                new Upload();
            }
            FileOutputStream fos = new FileOutputStream(/*needs something with nodes*/ getFile());
            ObjectInputStream ois = new ObjectInputStream(connectingDirectory.getSocket().getInputStream());
        }

    /*
    BufferedOutputStream out = new BufferedOutputStream(fos);
byte[] buffer = new byte[1024];
int count;
InputStream in = socket.getInputStream();
while((count=in.read(buffer)) >0){
    fos.write(buffer);
}
    * */

    /* private void receiveData(File fileIns) throws FileNotFoundException {
        FileOutputStream fis = new FileOutputStream(fileIns);
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                CloudByte cb = (CloudByte) in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println(cloudByteList);
    }*/

    }

    /*--------------------------Upload--------------------------*/

    public static class Upload { //extends Thread
        private ConnectingDirectory connectingDirectory;

        public void uploadFile() throws IOException {
            if (!getFile().exists()) {
                System.out.println("File doesn't exist." + "\nDownloading the file!");
                new Download();
            }
            try {
                ObjectOutputStream out = new ObjectOutputStream(connectingDirectory.getSocket().getOutputStream());
                out.writeObject(getCloudByteList());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}