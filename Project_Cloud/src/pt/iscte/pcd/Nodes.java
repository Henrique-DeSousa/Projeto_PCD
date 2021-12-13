package pt.iscte.pcd;

public class Nodes {

    private final String node;
    private final int hostPort;


    public Nodes(String string) {
        String[] temp = string.split("/");

        String splitOne = temp[1];
        String[] splitTwo = splitOne.split(" ");
        String hostAddress = splitTwo[0];
        hostPort = Integer.parseInt(splitTwo[1]);

        splitOne = temp[0];
        splitTwo = splitOne.split(" ");
        String name = splitTwo[1];


        this.node = "node " + name + "/" + hostAddress + " " + this.hostPort;

    }

    public int getHostPort() {
        return hostPort;
    }

    public String getNode() {
        return node;
    }

}
