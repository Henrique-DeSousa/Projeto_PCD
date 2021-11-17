package pt.iscte.pcd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Upload extends FileInfo{

    private List<CloudByte> cloudByteList = new ArrayList<>();



    public Upload(File file, String name) {
        super(file, name);
    }



    /*
     private void sendData(File fileIn) throws FileNotFoundException {
        Scanner s = new Scanner(new BufferedReader(new FileReader(fileIn)));
        byte[] bytes = s.nextLine().getBytes();

        for (int i = 0; i < bytes.length - 1; i++) {
            cloudByteList.add(new CloudByte(bytes[i]));
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(cloudByteList);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(cloudByteList);
    }*/
}
