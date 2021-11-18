package pt.iscte.pcd;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Upload extends FileInfo{



    public Upload(String file, String name, String hostName, int host, int directory) throws IOException {
        super(file, name, hostName, host , directory);

    }

    public void uploadFile(File file) throws IOException {
        if (!super.doesFileExist(file)) {
            System.out.println("File: " + file + " doesn't exist.");
            System.exit(0);
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(getSocket().getOutputStream());
            out.writeObject(getCloudByteList());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(getCloudByteList());
    }
           // System.out.println(cloudByteList.size());



}
