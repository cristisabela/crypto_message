/**
 * Sources: Dr. Feng Zhu and Daniel Davis
 * PCEncryptionWithAES.java
 * https://sites.google.com/a/uah.edu/pervasive-security-privacy/node-level/cryptographic-algorithms-using-java
 */
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;

public class AES {
    private static SecretKey key;
    private static Cipher cipher = null;

    /**
     * Generates an AES key for the user.
     * @param size the size of the key to be generated
     */
    public static void initialize_key(int size)
    {
        try
        {
            // Generate aes key to encrypt/decrypt messages
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(size);
            key = generator.generateKey();

            // Testing
            ////System.out.println("Symmetric Key : " + new String(key.getEncoded()) + "\n");
        }
        catch (Exception e)
        {
            System.err.println("Oops! There were some problems generating the key.\n" + e.getMessage() + "\n");
        }

        // Initialize the Cipher
        initCipher();
    }

    /**
     * Get the generated AES key
     * @return the generated key
     */
    public static Key getAESKey() {
        return key;
    }

    /**
     * Set AES key -- Used when it is generated and shared by another party
     * @param skey the key value that will be set
     */
    public static void setAESKey(SecretKey skey) {
        key = skey;
    }

    /**
     * Initialize cipher
     */
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

    /**
     * Encrypt a message using AES
     * @param message the message that needs to be encrypted
     * @param key the key used to encrypt the message
     * @return
     */
    public static byte[] Encrypt(String message, Key key)
    {
        try
        {
            byte encrypted[];
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(message.getBytes());
            return encrypted;
        }
        catch (Exception e)
        {
            System.err.println("Oops! There were some problems encrypting the message.\n" + e.getMessage() + "\n");
            return null;
        }
    }

    /**
     * Decrypt a message using AES
     * @param message the message that needs to be decrypted
     * @param key the key that will be used to decrypt the message
     * @return
     */
    public static String Decrypt(byte[] message, Key key)
    {
        try
        {
            byte[] decrypted;
            cipher.init(Cipher.DECRYPT_MODE, key);

            decrypted = cipher.doFinal(message);
            return new String(decrypted);
        }
        catch (Exception e)
        {
            System.err.println("Oops! There were some problems decrypting the message.\n" + e.getMessage() + "\n");
            return "";
        }
    }

    /**
     * This main method is here to test if the encryption and decryption functions are working correctly.
     * This was also used to test the efficiency of the cipher.
     * @param args
     */
    public static void main(String[] args)
    {
        TimingTest time = new TimingTest();
        String msg = "This is a test message that I want to encrypt using this cipher! I want to see how long" +
                " it will take to encrypt and decrypt this message so that I can see how efficient this algorithm is." +
                " This is why this test message is so long.";

        // Set start time, and initialize end time
        time.stime = System.currentTimeMillis();
        time.etime = time.stime;

        // Initialize Key
        initialize_key(128);

        // Start timer
        time.etime = System.currentTimeMillis();
        time.timing("Key Initialization: ");

        // Encrypt
        byte[] e_msg = Encrypt(msg, getAESKey());
        System.out.println("Encrypted: " + new String(e_msg));

        // Set end time after encryption and print
        time.etime = System.currentTimeMillis();
        time.timing("Encryption Time: ");

        // Decrypt the message
        String d_msg = Decrypt(e_msg, getAESKey());
        System.out.println("Decrypted: " + d_msg);

        // Set end time after decryption and print
        time.etime = System.currentTimeMillis();
        time.timing("Decryption Time: ");
    }
}
