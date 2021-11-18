package pt.iscte.pcd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ErrorInjection extends FileInfo{

    private String error;

    public ErrorInjection(String file, String name, String hostName, int host, int directory) throws IOException {
        super(file,name, hostName, host , directory);

        // is there one? we don't know

    }

    public void injection() {
        System.out.println(getCloudByteList().size());
        Scanner scan = new Scanner(System.in);
        while (true) {
            error = scan.nextLine();
            if ((error.contains("Error") || error.contains("error") || error.contains("erro") || error.contains("Erro"))) {
                String clean = error.replaceAll("\\D+", "");
                    int index = Integer.parseInt(clean);
                    //Integer index = Integer.parseInt(split[1]);
                    if (index < 0) {
                        System.err.println("Please insert a value higher or equal to 0");
                        break;
                    } else if (index > getCloudByteList().size() - 1) {
                        System.err.println("Please insert a value lower or equal to: " + (getCloudByteList().size()));
                        break;
                    } else {
                        System.out.println("Injecting error on byte: " + index);
                        getCloudByteList().get(index).makeByteCorrupt();
                        System.out.println(getCloudByteList());
                        break;
                    }
                }
            }
        }
}
