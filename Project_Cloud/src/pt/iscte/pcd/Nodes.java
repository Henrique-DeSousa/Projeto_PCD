package pt.iscte.pcd;

public class Nodes {

    private final String node;
    private final String name;
    private final int hostPort;
    private final String hostAddress;

    public Nodes(String string) {
        String str = string;
        String[] temp = str.split("/");

        String splitOne = temp[1];
        String[] splitTwo = splitOne.split(" ");
        hostAddress = splitTwo[0];
        hostPort = Integer.parseInt(splitTwo[1]);

        splitOne = temp[0];
        splitTwo = splitOne.split(" ");
        this.name = splitTwo[1];

        this.node = "node " + this.name + "/" + this.hostAddress + " " + this.hostPort;
    }

    public String getName() {
        return name;
    }

    public int getHostPort() {
        return hostPort;
    }

    public String getNode() {
        return node;
    }
}
