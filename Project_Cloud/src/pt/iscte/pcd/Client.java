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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class Client implements ActionListener {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    JButton button;
    JTextField text;
    JTextField text2;
    JTextArea textArea;
    String S1;
    String S2;

    public Client(String addressText, int port) {
        System.err.println("Connecting to: " + addressText + ":" + port);
        try {
            InetAddress address = InetAddress.getByName(addressText);
            Socket s = new Socket(address, port);
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UI() {
        JFrame frame = new JFrame("Client");
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextArea text_x = new JTextArea("Position:");
        JTextArea text_y = new JTextArea("Length:");
        this.button = new JButton("Look up");
        this.button.addActionListener(this);
        this.text = new JTextField();
        this.text2 = new JTextField();
        this.textArea = new JTextArea();
        text_x.setEditable(false);
        text_y.setEditable(false);

        text.setPreferredSize(new Dimension(250, 40));
        text2.setPreferredSize(new Dimension(250, 40));
        textArea.setPreferredSize(new Dimension(800, 200));
        textArea.setEditable(false);

        frame.add(text_x);
        frame.add(text);
        frame.add(text_y);
        frame.add(text2);
        frame.add(button);
        frame.add(textArea);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(830, 200);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.button) {

            S1 = text.getText();
            S2 = text2.getText();
        }

        int s1 = parseInt(S1);
        int s2 = parseInt(S2);
        ByteBlockRequest bbr = new ByteBlockRequest(s1, s2);
        try {
            out.writeObject(bbr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            CloudByte[] array = (CloudByte[]) in.readObject();
            textArea.setText("");
            textArea.setText(Arrays.toString(array));

        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
    }


    public static void main(String[] args){
        Client c = new Client("127.0.0.1", 8083);
        c.UI();
    }
}
