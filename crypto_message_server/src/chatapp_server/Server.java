package chatapp_server;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

public class Server extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    private static AES aescipher;
    private static Vigenere vigenerecipher;

    /**
     * Server Constructor
     * The GUI is created here
     */
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

        aescipher = new AES();
        aescipher.initialize_key(128);

        vigenerecipher = new Vigenere();
        vigenerecipher.generate_key();
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
                    sendServerVigenereKey();
                    sendServerAESKey();
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

    /**
     * Waits for Clients to connect to the Server
     * @throws IOException
     */
    private void waitForConnection()throws IOException {
        showMessage("Waiting for client to connect...\n");
        connection = server.accept();
        showMessage("Now connected to " + connection.getInetAddress().getHostName());
    }

    /**
     * Set up the input and output streams
     * @throws IOException
     */
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Input and Output Streams are configured! \n");
    }

    private void sendServerAESKey(){
        try{
            output.writeObject(Base64.getEncoder().encodeToString(aescipher.getAESKey().getEncoded()));
        }catch (IOException ioexception){
            chatWindow.append("\n\nSERVER ERROR: KEY SHARING FAILED");
            ioexception.printStackTrace();
        }
    }

    private void sendServerVigenereKey(){
        try{
            output.writeObject(vigenerecipher.getKey());
        }catch (IOException ioe){
            chatWindow.append("\n\nSERVER ERROR: Vigenere Key sending failed.");
        }
    }

    /**
     * Server and Clients are now Connected -- A Conversation can be started
     * @throws IOException
     */
    private void whileChatting() throws IOException{
        String message = "You are connected!\nYou can start chatting";
        sendConnectionCompleteMessage(message);
        //allowing users to type since they are up and running
        ableToType(true);
        //have a conversation
        //where we are getting actual message
        do try {
            //message type is the actual message that comes in
            // Read the message
            message = (String) input.readObject();
            showMessage("\nCLIENT - " + message);       // Show the encrypted message

            String vd_msg;      // Vigenere Decrypted Message
            String ad_msg;      // AES Decrypted Message
            String rd_msg;      // RSA Decrypted Message

            if(message.contains("VIGENERE - "))     // If the message was encrypted with the Vigenere Cipher
            {
                vd_msg = vigenerecipher.Decrypt(vigenerecipher.getKey(), message.substring(11));
                showMessage("\nCLIENT - VIGENRE DECRYPTED: " + vd_msg); // Show the decrypted message
            }
            else if (message.contains("AES - "))    // If the message was encrypted with AES
            {
                byte[] decoded = Base64.getDecoder().decode(message.substring(6));
                ad_msg = aescipher.Decrypt(decoded, aescipher.getAESKey());
                showMessage("\nCLIENT - AES DECRYPTED: " + ad_msg);
            }
            // ELSE IF RSA GOES HERE

        } catch (ClassNotFoundException classNotFound) {
            showMessage("\nCould not understand message!");
        } while(!message.equals("STOP"));
    }

    /**
     * Close streams and sockets -- Cleanup after the messaging application has been closed
     */
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

    private void sendConnectionCompleteMessage(String message) {
        try{
            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("\nSERVER - " + message);
        }catch(IOException ioexception){
            chatWindow.append("\n\nSERVER ERROR: WAS NOT ABLE TO SEND MESSAGE TO CLIENT");
            ioexception.printStackTrace();
        }
    }

    /**
     * Send a message to the Client
     * @param message the message that will be sent
     */
    private void sendServerMessage(String message) {
        try{
            // Encrypt the message before it is send across the wire here!
            //VIGENERE ENCRYPTION
            String ve_message = vigenerecipher.Encrypt(vigenerecipher.getKey(), message);
            output.writeObject("VIGENERE - " + ve_message);
            output.flush();

            //AES ENCRYPTION
            byte[] ae_msg = aescipher.Encrypt(message, aescipher.getAESKey());
            output.writeObject("AES - " + Base64.getEncoder().encodeToString(ae_msg));
            output.flush();

            // showMessage shows the message on the GUI
            // Show the unencrypted message that the Server is sending on the GUI
            showMessage("\nSERVER - " + message);
        }catch(IOException ioexception){
            chatWindow.append("\n\nSERVER ERROR: WAS NOT ABLE TO SEND MESSAGE TO CLIENT");
            ioexception.printStackTrace();
        }
    }

    /**
     * Update the chatWindow, and display the message
     * @param text
     */
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

    /**
     * Allow/Disallow the user to type into the chat box
     * @param b the boolean value to set whether or not the window is editable
     */
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
