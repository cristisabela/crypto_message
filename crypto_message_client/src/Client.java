import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

public class Client extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    private static RSA rsacipher;
    private static AES aescipher;
    private static Vigenere vigenerecipher;


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

        vigenerecipher = new Vigenere();
        aescipher = new AES();
        aescipher.initialize_key(128);
    }

    //connect to Server
    public void startUpClient() {
        try{
            connectToServer();
            setupStreams();
            readVigenereKey();
            readAESKey();
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

    private void readAESKey()
    {
        try{
            String gottenkey = input.readObject().toString();
            System.out.println(gottenkey);
            byte[] keys = Base64.getDecoder().decode(gottenkey);
            System.out.println(keys);
            aescipher.setAESKey(new SecretKeySpec(keys, "AES"));
            System.out.println(aescipher.getAESKey().getEncoded());
        }catch(Exception e) {
            showMessage("Oops, couldn't read AES key.");
            e.printStackTrace();
        }
    }

    private void readVigenereKey()
    {
        try{
            vigenerecipher.setKey((String) input.readObject());
        }catch (ClassNotFoundException cnfe){
            showMessage("Oops, couldn't, read the vigenere key.");
            cnfe.printStackTrace();
        }catch (IOException io){
            showMessage("Oops, couldn't, read the vigenere key.");
            io.printStackTrace();
        }
    }

    //while chatting with server
    private void whileChatting() throws IOException{
        ableToType(true);
        if(message != "")
        {
            sendClientMessage(message);
        }
        do try {
            //message type is the actual message that comes in
            //?????Decryption occurs here??????

            // Read the message
            message = (String) input.readObject();
            if (!message.contains("You are connected!\nYou can start chatting"))
            {
                showMessage("\nSERVER - " + message);

                String vd_msg;
                String ad_msg;
                //String rd_msg;

                if(message.contains("VIGENERE - "))
                {
                    vd_msg= vigenerecipher.Decrypt(vigenerecipher.getKey(), message.substring(11));
                    showMessage("\nSERVER - VIGENERE DECRYPTED: " + vd_msg);
                }
                else if(message.contains("AES - "))
                {
                    byte[] decoded = Base64.getDecoder().decode(message.substring(6));

                    ad_msg = aescipher.Decrypt(decoded, aescipher.getAESKey());
                    showMessage("\nSERVER - AES DECRYPTED: " + ad_msg);
                }
            }
            else
            {
                showMessage("\n" + message);
            }

        } catch (ClassNotFoundException classNotFound) {
            showMessage("\nCould not understand message!");
        }
        while(!message.equals("STOP"));
    }

    //send message to the server
    private void sendClientMessage(String message){
        try{
            //VIGENERE ENCRYPTION
            String ve_msg = vigenerecipher.Encrypt(vigenerecipher.getKey(), message);
            output.writeObject("VIGENERE - " + ve_msg);
            output.flush();

            //AES ENCRYPTION
            byte[] ae_msg = aescipher.Encrypt(message, aescipher.getAESKey());
            output.writeObject("AES - " + Base64.getEncoder().encodeToString(ae_msg));
            output.flush();
            //this shows the message on the GUI
            //???show plaintext and ciphertext here????
            //showMessage("\nCLIENT - Vigenere: " + ve_msg);
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
