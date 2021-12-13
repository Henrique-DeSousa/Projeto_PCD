package pt.iscte.pcd;

import javax.crypto.spec.PSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Integer.parseInt;

public class Client implements ActionListener {
    private ObjectInputStream an;
    private ObjectOutputStream out;
    BufferedReader in;
    JButton button;
    JTextField text;
    JTextField text2;
    TextArea textArea;
    String S1;
    String S2;

    public Client(String addressText, int port){
        System.err.println("Connecting to: "+ addressText +":"+port);
        try {
            InetAddress address = InetAddress.getByName(addressText);
            Socket s = new Socket(address,port);
            out = new ObjectOutputStream(s.getOutputStream());
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            System.out.println(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UI(){
        JFrame frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextArea text_x = new JTextArea("Posiçao a consultar:");
        JTextArea text_y = new JTextArea("Comprimento:");
        this.button = new JButton("Consultar");
        this.button.addActionListener(this);
        this.text = new JTextField();
        this.text2 = new JTextField();
        JTextArea textArea = new JTextArea();
        text_x.setEditable(false);
        text_y.setEditable(false);

        text.setPreferredSize(new Dimension(250,40));
        text2.setPreferredSize(new Dimension(250,40));
        textArea.setPreferredSize(new Dimension(800,200));

        frame.add(text_x);
        frame.add(text);
        frame.add(text_y);
        frame.add(text2);
        frame.add(button);
        frame.add(textArea);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(830,200);
    }


    public void consulta(int x,int y) throws IOException, ClassNotFoundException {
        CloudByte[] Cl = (CloudByte[]) an.readObject();
        int index=1;
        for (CloudByte cloudByte : Cl) {
            index++;
            if(index>=x && index<x+y){
                System.out.println(cloudByte);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.button){
            S1 = text.getText();
            S2 = text2.getText();
        }
        int s1 = parseInt(S1);
        int s2 = parseInt(S2);
        try {
            consulta(s1,s2);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Client c = new Client("127.0.0.1",8082);
        c.UI();
    }
}
