package pt.iscte.pcd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ErrorInjection extends FileInfo{

    private String error;
    private List<CloudByte> cloudy = new ArrayList<>();

    public ErrorInjection(String file, String name, String hostName, int host, int directory) throws IOException {
        super(file,name, hostName, host , directory);
        this.cloudy = getCloudByteList();
        // is there one? we don't know

    }

    public void injection(){
        System.out.println(cloudy.size());
        Scanner scan = new Scanner(System.in);
        while(true){
            error = scan.nextLine();
            if(error.contains("Error")){
                String[] split = error.split(" ");
                Integer index = Integer.parseInt(split[1]);
                    if(index < 0){
                        System.err.println("Please insert a value higher or equal to 0");
                        break;
                    }else if( index > cloudy.size()-1){
                        System.err.println("Please inster a value lower or equal to: " + (cloudy.size()));
                        break;
                    }else{
                        System.out.println("Injecting error on byte: " + index);
                        cloudy.get(index).makeByteCorrupt();
                        System.out.println(cloudy);
                        break;
                    }
            }
        }
    }
}