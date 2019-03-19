/*The following code was borrowed and modified from Daniel Davis and Dr.Zhu's project*/
import java.security.*;
import javax.crypto.*;
public class AES {

    private static Key key;
    private static Cipher cipher = null;//Cipher.getInstance("AES");

    private static void initialize_key(int size)
    {
        try
        {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(size);
            key = generator.generateKey();

            System.out.println("Symmetric Key : " + new String(key.getEncoded()) + "\n");
        }
        catch (Exception e)
        {
            System.err.println("Oops! There were some problems generating the key.\n" + e.getMessage() + "\n");
        }

        initCipher();
    }

    private static Key getAESKey()
    {
        return key;
    }

    private static void initCipher()
    {
        if(cipher == null)
        {
            try
            {
                cipher = Cipher.getInstance("AES");
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    public static byte[] Encrypt(String message)
    {
        try
        {
            byte encrypted[] = null;
            cipher.init(Cipher.ENCRYPT_MODE, getAESKey());
            encrypted = cipher.doFinal(message.getBytes());
            return encrypted;
        }
        catch (Exception e)
        {
            System.err.println("Oops! There were some problems encrypting the message.\n" + e.getMessage() + "\n");
            return null;
        }
    }

    public static String Decrypt(byte[] message)
    {
        try
        {
            byte decrypted[] = null;
            cipher.init(Cipher.DECRYPT_MODE, getAESKey());

            decrypted = cipher.doFinal(message);//message.getBytes());
            return new String(decrypted);
        }
        catch (Exception e)
        {
            System.err.println("Oops! There were some problems decrypting the message.\n" + e.getMessage() + "\n");
            return "";
        }
    }

    public static void main(String[] args)
    {
        String msg = "This is a test message that I want to encrypt! Its super duper secret and super super long because we want to test a bunch of things" +
                "like efficiency, and to make sure the code is working.";

        System.out.println(msg);
        initialize_key(128);
        byte[] e_msg = Encrypt(msg);
        if(e_msg != null)
        {
            System.out.println("The encrypted message is: " + new String(e_msg));
            String d_msg = Decrypt(e_msg);
            System.out.println("The decrypted message is: " + d_msg);
        }
    }
}
