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

    private void connectToServer() throws IOException{
        //not implemented yet
    }

    private void setupStreams() throws IOException{
        //not implemented yet
    }

    private void whileChatting() throws IOException{
        //not implemented yet
    }

    private void sendClientMessage(String message){
        //not implemented yet
    }

    private void showMessage(final String text){

    }

    private void closeClientApp(){

    }
}
