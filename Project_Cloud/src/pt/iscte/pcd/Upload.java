package pt.iscte.pcd;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Upload extends FileInfo{

    private List<CloudByte> cloudByteList = new ArrayList<>();
    private String name;
    private File file;
    ConnectingDirectory cs = new ConnectingDirectory();



    public Upload(File file, String name) throws IOException {
        super(file, name);
        this.name = name;
        this.file = file;
    }

    public void uploladFile(File file) throws IOException {
        if(!super.doesFileExist(file)){
            System.out.println("File: " + file + " doesn't exist.");
            System.exit(0);
        }
        byte[] fileContents= Files.readAllBytes(new File(file.getName()).toPath());

        for (int i = 0; i < fileContents.length - 1; i++) {
            cloudByteList.add(new CloudByte(fileContents[i]));
            try {
                ObjectOutputStream out = new ObjectOutputStream(cs.getSocket().getOutputStream());
                out.writeObject(cloudByteList);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            System.out.println(cloudByteList);
           // System.out.println(cloudByteList.size());
        }


}
