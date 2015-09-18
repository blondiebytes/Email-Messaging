package chatclient;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame  implements ActionListener {

    // Sockets & Streams
    private static InetAddress host;
    private static final int PORT = 52946;
    private static Socket socket;
    private static BufferedReader networkInput;
    private static PrintWriter networkOutput;
    private String name;
    
    
    // UI
    private static ChatClient frame;
    private JTextArea chatArea;
    private JTextArea messageArea;
    private JButton sendButton;
    private String message = "";

    public static void main(String[] args) throws IOException{
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        
        frame = new ChatClient();
        frame.setUpStreams();
        frame.setTitle("A Simple Chat Client");
        frame.setSize(400, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        while(true) {
           frame.readMessage();
        }
        
    }



    public ChatClient() throws IOException{
        chatArea = new JTextArea(20, 50);
        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());
        JLabel prompt = new JLabel("Enter Message: ");
        messageArea = new JTextArea(5, 50);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendPanel.add(prompt, BorderLayout.WEST);
        sendPanel.add(messageArea, BorderLayout.CENTER);
        sendPanel.add(sendButton, BorderLayout.EAST);
        add(sendPanel, BorderLayout.SOUTH);

        name = JOptionPane.showInputDialog(frame,
                                                    "Enter Your Nickname: ",
                                                    "Name Entry",
                                                    JOptionPane.PLAIN_MESSAGE);
        System.out.println("User name dialog input was: " + name);
        
       // Code to connect with server and set up client
        // thread for receiving and displaying messages
        // from server.
        setUpStreams();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == sendButton) {
            System.out.println("User pressed SEND button.");
            message = messageArea.getText();
            System.out.println("User typed message: " + message);
            messageArea.setText(""); 
            if (message.equals("Bye")) {
                // Code to close connection if user typed "Bye".
                try {
                    System.out.println("Closing connection...");
                    socket.close();
                } catch (IOException ioEx) {
                    System.out.println("Unable to disconnect!");
                    System.exit(1);
                }
            } else {
                // Code to send any other message to the server.
                sendMessage();
            }
        }
    }
    
    // Code to connect with server and set up client
    // thread for receiving and displaying messages
    // from server.
    public void setUpStreams() throws IOException{
         try {
             // Making a socket
            socket = new Socket(host, PORT);
            // Setting up I/O Streams
            networkInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            networkOutput = new PrintWriter(socket.getOutputStream(), true);
            // send out the name!
            message = name;
            sendMessage();
         } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }
    
    public void sendMessage() {
        System.out.println("Sending Message...");
        networkOutput.println(message);
    }
    
    public void readMessage() throws IOException {
        // getting a response
        String response = networkInput.readLine();
        if (!response.isEmpty()) {
            // appending to the chat area
            chatArea.append(response);
            chatArea.append("\n");
        }
    }

 
}