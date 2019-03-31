import com.sun.deploy.panel.JSmartTextArea;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.omg.IOP.IOR;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    //constructor
    public Client(String host){
        super("Secure Instant Messenger - Client");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendClientMessage(e.getActionCommand());
                    userText.setText("");
                }
            }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow),BorderLayout.CENTER);
        setSize(1000,500);
        setVisible(true);
    }

    //connect to Server
    public void startUpClient() {
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException eofException){
            showMessage("\n Client ended connection!");
        }catch(IOException ioexception){
            ioexception.printStackTrace();
        }finally {
            closeClientApp();
        }
    }

    //makes connection to the Server
    private void connectToServer() throws IOException{
        showMessage("Attempting Connection....\n");
        //setting up the IP address and port numbers here
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }

    //set up streams and receive message
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\nYour streams are now connected\n");
    }

    //while chatting with server
    private void whileChatting() throws IOException{
        ableToType(true);
        sendClientMessage(message);
        do try {
            //message type is the actual message that comes in
            //?????Decryption occurs here??????
            message = (String) input.readObject();
            showMessage("\n" + message);
        } catch (ClassNotFoundException classNotFound) {
            showMessage("\nCould not understand message!");
        } while(!message.equals("CLIENT - STOP"));
    }

    //send message to the server
    private void sendClientMessage(String message){
        try{
            //Encryption occurs here
            output.writeObject("SERVER - " + message);
            output.flush();
            //this shows the message on the GUI
            //???show plaintext and ciphertext here????
            showMessage("\nCLIENT - " + message);
        }catch(IOException io){
            chatWindow.append("\n\nCLIENT ERROR: WAS NOT ABLE TO SEND MESSAGE TO CLIENT");
            io.printStackTrace();
        }
    }

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

    //closes steams and sockets
    private void closeClientApp(){
        showMessage("Closing Connections...\n");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
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
