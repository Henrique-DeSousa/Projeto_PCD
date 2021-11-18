package pt.iscte.pcd;

import java.io.File;
import java.io.IOException;

public class FileInfo extends ConnectingDirectory{


    private File file;
    private String fileName;


    public FileInfo(File file, String name) throws IOException {
        super();
        this.file = file;
        this.fileName = name;
    }

    public boolean doesFileExist(File file){
        if(file.equals(null)){
            System.err.println("File cannot be Null");
            System.exit(1);
        } else if(file.exists()) {
            return true;
        }return false;
    }

}
