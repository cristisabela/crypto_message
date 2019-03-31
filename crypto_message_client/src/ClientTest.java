import javax.swing.JFrame;

public class ClientTest {
    public static void main(String [] args){
        //using local host for now
        //server is on the same computer as the client
        //can put another ip if the server is somewhere else
        Client client = new Client("127.0.0.1");
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.startUpClient();

    }
}
