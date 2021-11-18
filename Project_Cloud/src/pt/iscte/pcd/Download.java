package pt.iscte.pcd;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Download extends FileInfo{

    /* this.address = InetAddress.getByName(null);
        this.socket = new Socket(address, serverPort);*/

    private List<CloudByte> cloudByteList = new ArrayList<>();
    private String fileName;
    private File file;
    private ConnectingDirectory cs;


    public Download(File file, String name) throws IOException {
        super(file, name);
        this.file = file;
        this.fileName = name;
    }

    public void downloadFile() throws IOException {
        if(super.doesFileExist(file)){
            System.out.println("File: " + file + " exists.");
            System.exit(0);
        }
        FileOutputStream fos = new FileOutputStream(file);
        ObjectInputStream ois = new ObjectInputStream(cs.getSocket().getInputStream());
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
