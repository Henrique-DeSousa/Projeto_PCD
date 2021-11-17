package pt.iscte.pcd;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FileInfo{


    private File file;
    private String fileName;

    public FileInfo(File file, String name){
        this.file = file;
        this.fileName = name;
    }

    public boolean doesFileExist(File file){
        if(file.equals(null)){
            System.err.println("File cannot be Null");
        } else if(file.exists()) {
            return true;
        }return false;
    }

}
