package pt.iscte.pcd;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client{
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Client(String addressText, int port){
        System.err.println("Connecting to:"+ addressText +":"+port);
        //connect to node
        try {
            InetAddress address = InetAddress.getByName(addressText);
            Socket s = new Socket(address,port);
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());

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
        JButton button = new JButton("Consultar");
        JTextField text = new JTextField();
        JTextField text2 = new JTextField();
        JTextArea textArea = new JTextArea("Respostas aparecerao aqui...");
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
}
