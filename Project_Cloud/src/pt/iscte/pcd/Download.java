package pt.iscte.pcd;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Download extends FileInfo{

    /* this.address = InetAddress.getByName(null);
        this.socket = new Socket(address, serverPort);*/


   // private String fileName;
   // private File file;


    public Download(String file, String name, String hostName, int host, int directory) throws IOException {
        super(file, name, hostName, host , directory);
    }

    public void downloadFile() throws IOException {
        if(super.doesFileExist(getFile())){
            System.out.println("File: " + getFile() + " exists.");
            System.exit(0);
        }
        FileOutputStream fos = new FileOutputStream(getFile());
        ObjectInputStream ois = new ObjectInputStream(getSocket().getInputStream());
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
