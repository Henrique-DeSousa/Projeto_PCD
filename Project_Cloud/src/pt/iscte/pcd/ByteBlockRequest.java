package pt.iscte.pcd;

import java.io.Serializable;

public class ByteBlockRequest implements Serializable {

    private int index;
    private int size = 100; //by default it's a size of 100


    public ByteBlockRequest(int index, int size) {
        this.index = index;
        this.size = size;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int setIndex){
        this.index = setIndex;
    }

    public int getSize(){
        return size;
    }

    public void setSize(int setSize){
        this.size = setSize;
    }
}
