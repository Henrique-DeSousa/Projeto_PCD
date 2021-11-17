package pt.iscte.pcd;
public class Nodes{

    private String name;
    private int port;
    private String node;

    public Nodes(String name, int port){
        this.name = name;
        this.port = port;
        this.node = "node " + this.name + "/" + this.port;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getNode() {
        return node;
    }
}
