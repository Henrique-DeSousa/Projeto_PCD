package pt.iscte.pcd;

import java.io.Serializable;

public class ByteBlockRequest implements Serializable {

    private final CloudByte[] list;
    private final byte[] byteArray;


    ByteBlockRequest(CloudByte[] list) {
        this.list = list;
        this.byteArray = new byte[list.length];
        readArray();
    }

    public void readArray() {
        for (int i = 0; i < list.length; i++) {
            byteArray[i] = list[i].getValue();
        }
    }

    //Return the byte[] from the array
    public byte[] getByteArray() {
        return byteArray;
    }

    public int getByteArrayLength() {
        return byteArray.length;
    }

    //For the GUI
    public void readArrayFromPos(int start, int length) {
        byte[] fromPos = new byte[length];
        for (int i = start; (i != length && i < list.length); i++) {
            fromPos[i] = list[i].getValue();
        }
    }

    //To be used in the Download
    public byte[] blocksToSend(int j) {
        int downloadBlocksSize = 100;
        byte[] blockSend = new byte[downloadBlocksSize];
        for (int i = 0; i < downloadBlocksSize; i++) {
            blockSend[i] = byteArray[i + j * 100];
        }
        return blockSend;
    }
}
