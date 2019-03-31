package chatapp_server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    //Constructor, creating GUI here
    public Server(){
        super("Secure Instant Messenger - Server");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendServerMessage(e.getActionCommand());
                    userText.setText("");
                }
            }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(1000,500);
        setVisible(true);
    }

    //set up and run the server
    public void startUpServer(){
        try{
            server = new ServerSocket(6789,100);
            while(true){
                //connect and maintain connection
                try{
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }catch(EOFException eofException){
                    showMessage("\n Server ended connection!");
                }finally {
                    closeApp();
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //waits for clients to connect to servers
    private void waitForConnection()throws IOException {
        showMessage("Waiting for client to connect...\n");
        connection = server.accept();
        showMessage("Now connected to "+connection.getInetAddress().getHostName());
    }

    //setup input and output streams
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Input and Output Streams are configured! \n");
    }

    //Server and Clients are connected
    //we will now be a conversation
    private void whileChatting() throws IOException{
        String message = "You are connected!\nYou can start chatting";
        sendServerMessage(message);
        //allowing users to type since they are up and running
        ableToType(true);
        //have a conversation
        //where we are getting actual message
        do try {
            //message type is the actual message that comes in
            //?????Decryption occurs here??????
            message = (String) input.readObject();
            showMessage("\n" + message);
        } catch (ClassNotFoundException classNotFound) {
            showMessage("\nCould not understand message!");
        } while(!message.equals("SERVER - STOP"));
    }

    //Clean up after messaging app has been closed
    //Close streams and sockets
    private void closeApp(){
        showMessage("\nClosing Connections...\n");
        //stops users from being able to type when app is closing
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioexception){
            ioexception.printStackTrace();
        }
    }

    //Sends message to client
    private void sendServerMessage(String message) {
        try{
            /**
             * ?????
             * perform enryption here
             * before sending message across the wire
             * ???
             */
            //this sends the message over the wire
            output.writeObject("SERVER - " + message);
            output.flush();
            //this shows the message on the GUI
            //???show plaintext and ciphertext here????
            showMessage("\nSERVER - " + message);
        }catch(IOException ioexception){
            chatWindow.append("\n\nSERVER ERROR: WAS NOT ABLE TO SEND MESSAGE TO CLIENT");
            ioexception.printStackTrace();
        }
    }

    //updates chatWindow and displays message
    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                //set aside a thread that updates the chatWindow
            new Runnable(){
                public void run(){
                    //adds messsage to the end of the docuement
                    //then updates chatWindow
                    chatWindow.append(text);
                }
            }
        );
    }

    //allow user to type into chat box if set to true
    private void ableToType(final boolean b){
        SwingUtilities.invokeLater(
            new Runnable(){
                public void run(){
                    userText.setEditable(b);
                }
            }
        );
    }
}