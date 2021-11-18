package pt.iscte.pcd;

import com.sun.jdi.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class FileInfo extends ConnectingDirectory{

    private File file;
    private String fileName;

    private List<CloudByte> cloudByteList = new ArrayList<>();

    public FileInfo(String file, String name, String hostName, int host, int directory) throws IOException {
        super(hostName, host , directory);
        this.file = new File(file);
        this.fileName = name;
        fillingList(getFile());
    }

    public boolean doesFileExist(File file) throws IOException {
        if(file.equals(null)){
            System.err.println("File cannot be Null");
            System.exit(1);
        } else if(file.exists()){
            return true;
        }return false;
    }

    public void fillingList(File fileIn) throws IOException {
        byte[] fileContents= Files.readAllBytes(fileIn.toPath());

        for (int i = 0; i < fileContents.length - 1; i++) {
            cloudByteList.add(new CloudByte(fileContents[i]));
        }
    }

    public List<CloudByte> getCloudByteList() {
        return cloudByteList;
    }


    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

}