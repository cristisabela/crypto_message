import java.nio.charset.Charset;
import java.util.Random;

public class Vigenere {

    private static String Key;  // The generated key

    /**
     * Generates a random 20 byte key from the ASCII character set
     * @return the newly generated key
     */
    public static void generate_key()
    {
        byte[] array = new byte[20];
        new Random().nextBytes(array);
        String key = new String(array, Charset.forName("ASCII"));
        Key = key;
        //return key;
    }

    /**
     * Returns the generated key used for encryption and decryption
     * of the vigenere cipher.
     * @return the key
     */
    public static String getKey(){
        return Key;
    }

    /**
     * Sets the key for encryption when generated and shared from another party
     * @param key
     */
    public static void setKey(String key){
        Key=key;
    }

    /**
     * Uses Vigenere's polyalphabetic cipher to encrypt all ASCII text
     * @param key the encryption key
     * @param message  the message that needs to be encrypted using key
     * @return the encrypted string
     */
    public static String Encrypt(String key, String message)
    {
        StringBuilder EncryptedMessage = new StringBuilder();
        for(int i =0; i < message.length(); i++)
        {
            // message character numerical value + key character numerical value % 128 (size of ASCII)
            EncryptedMessage.append((char)(((int)message.charAt(i) + (int)key.charAt(i % key.length())) % 128));
        }

        return EncryptedMessage.toString();
    }

    /**
     * Uses Vigenere's polyalphabetic cipher to decrypt all ASCII text
     * @param key the decryption key
     * @param message the message that needs to be decrypted
     * @return the decrypted string
     */
    public static String Decrypt(String key, String message)
    {
        StringBuilder DecryptedMessage = new StringBuilder();
        for(int i =0; i < message.length(); i++)
        {
            // message character numerical value - key character numerical value % 128 (size of ASCII)
            int number = ((int)message.charAt(i) - (int)key.charAt(i % key.length())) % 128;

            // convert any negative numbers to their positive equivalent so that they can be cast to correct char
            if(number < 0)
                number += 128;
            DecryptedMessage.append((char)number);
        }

        return DecryptedMessage.toString();
    }

    public static void main(String[] args) {
        TimingTest time = new TimingTest();

        //Long Message and key for testing
        String msg = "This is a test message that I want to encrypt using this cipher! I want to see how long" +
                " it will take to encrypt and decrypt this message so that I can see how efficient this algorithm is." +
                " This is why this test message is so long.";

        //Set start time, and initialize end time
        time.stime = System.currentTimeMillis();
        time.etime = time.stime;

        //Initialize the key
        generate_key(); // "ALongKeyToSecureTheMessage";

        time.etime = System.currentTimeMillis();
        time.timing("Key Initialization: ");

        //Encrypt the message
        String e_msg = Encrypt(Key, msg);
        System.out.println("Encrypted: " + e_msg);

        //Set the end time after encryption and print it
        time.etime = System.currentTimeMillis();
        time.timing("Encryption Time: ");

        //Decrypt message
        String d_msg = Decrypt(Key, e_msg);
        System.out.println("Decrypted: " + d_msg);

        //Set the end time after decryption
        time.etime = System.currentTimeMillis();
        time.timing("After Decryption: ");
    }
}
