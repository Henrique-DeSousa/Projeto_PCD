package pt.iscte.pcd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class Download extends FileInfo{



    /* this.address = InetAddress.getByName(null);
        this.socket = new Socket(address, serverPort);*/

    private List<CloudByte> cloudByteList = new ArrayList<>();
    private String fileName;
    private File file;

    public Download(File file, String name) {
        super(file, name);
        this.file = file;
        this.fileName = name;
    }

    public void downloadFile() throws FileNotFoundException {
        if(super.doesFileExist(file)){
            System.out.println("File: " + file + " exists.");
        }
        FileOutputStream fos = new FileOutputStream(file);
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
    }

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
