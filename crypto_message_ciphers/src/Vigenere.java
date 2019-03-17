public class Vigenere {
    private static String punctuation = "!@#$%^&*()_-=+[]{}\\|:;\"\'<>,.?/";
    private static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    //private static long start_time = 0;
    //private static long end_time = 0;

    /**
     * Uses Vigenere's polyalphabetic cipher to encrypt text
     * @param key the encryption key
     * @param message  the message that needs to be encrypted using key
     * @return the encrypted string
     */
    private static String Encrypt(String key, String message)
    {
        //String EncryptedMessage = "";
        StringBuilder EncryptedMessage = new StringBuilder();
        for(int i =0; i < message.length(); i++)
        {
            //If the character is a punctuation mark or a space, keep it
            if((punctuation.indexOf(message.charAt(i)) != -1)|| (int)message.charAt(i) == 32)
            {
                //EncryptedMessage += message.charAt(i);
                EncryptedMessage.append(message.charAt(i));
                continue;
            }

            int number = (alphabet.indexOf(message.charAt(i)) + alphabet.indexOf(key.charAt(i % key.length()))) % 52;
            //EncryptedMessage += alphabet.charAt(number);
            EncryptedMessage.append(alphabet.charAt(number));
        }
        //return EncryptedMessage;
        return EncryptedMessage.toString();
    }

    /**
     * Uses Vigenere's polyalphabetic cipher to decrypt text
     * @param key the decryption key
     * @param message the message that needs to be decrypted
     * @return the decrypted string
     */
    private static String Decrypt(String key, String message)
    {
        //String DecryptedMessage = "";
        StringBuilder DecryptedMessage = new StringBuilder();

        for(int i =0; i < message.length(); i++)
        {
            //If the character is a punctuation mark or a space, keep it
            if((punctuation.indexOf(message.charAt(i)) != -1)|| (int)message.charAt(i) == 32)
            {
                //DecryptedMessage += message.charAt(i);
                DecryptedMessage.append(message.charAt(i));
                continue;
            }

            int number = (alphabet.indexOf(message.charAt(i)) - alphabet.indexOf(key.charAt(i % key.length()))) % 52;
            if (number < 0)
                number += 52;
            //DecryptedMessage += alphabet.charAt(number);
            DecryptedMessage.append(alphabet.charAt(number));
        }
        //return DecryptedMessage;
        return DecryptedMessage.toString();
    }

    /**
     * Uses Vigenere's polyalphabetic cipher to encrypt all ASCII text
     * @param key the encryption key
     * @param message  the message that needs to be encrypted using key
     * @return the encrypted string
     */
    private static String Encrypt2(String key, String message)
    {
        //String EncryptedMessage = "";
        StringBuilder EncryptedMessage = new StringBuilder();
        for(int i =0; i < message.length(); i++)
        {
            EncryptedMessage.append((char)(((int)message.charAt(i) + (int)key.charAt(i % key.length())) % 128));
            //EncryptedMessage += (char)(((int)message.charAt(i) + (int)key.charAt(i % key.length())) % 128);
        }

        return EncryptedMessage.toString();
        //return EncryptedMessage;
    }

    /**
     * Uses Vigenere's polyalphabetic cipher to decrypt all ASCII text
     * @param key the decryption key
     * @param message the message that needs to be decrypted
     * @return the decrypted string
     */
    private static String Decrypt2(String key, String message)
    {
        //String DecryptMessage = "";
        StringBuilder DecryptedMessage = new StringBuilder();
        for(int i =0; i < message.length(); i++)
        {
            int number = ((int)message.charAt(i) - (int)key.charAt(i % key.length())) % 128;
            if(number < 0)
                number += 128;
            DecryptedMessage.append((char)number);
            //DecryptedMessage += (char)number;
        }

        //return DecryptedMessage;
        return DecryptedMessage.toString();
    }

    /*private static void timing(String header){

        System.out.println("\nMemory available at " + header + " (Free/Total): ( " +
                Runtime.getRuntime().freeMemory() + " / " +
                Runtime.getRuntime().totalMemory() + " )");

        System.out.println("Time at " + header + ": " + (end_time - start_time) + "\n");

    }*/

    public static void main(String[] args) {
        String msg = "This is a test message that I want to encrypt using the Vigenere Cipher!! I can encrypt with vigenere in one of two ways." +
                " I could just encrypt the letter, and add some padding characters for spacing, OR I could use the ASCII Table and just encrypt absolutely" +
                " everything! Which may be better because it can encrypt spaces, punctuation, and numbers without more input from me. :)";
        String key = "ASuperSecretLongKeyToULTRASECUREMyLongMessage";
        System.out.println("Test Message: " + msg);
        System.out.println("Key: " + key);

        //start_time = System.currentTimeMillis();
        //end_time = start_time;
        String e_msg = Encrypt(key, msg);
        String e_msg2 = Encrypt2(key, msg);
        System.out.println("Regular Encrypt: " + e_msg);
        System.out.println("ASCII Encrypt: " + e_msg2);
        //end_time = System.currentTimeMillis();
        //timing("After Encryption: ");
        System.out.println("Regular Decrypt: " + Decrypt(key, e_msg));
        //end_time = System.currentTimeMillis();
        //timing("After Decryption: ");
        System.out.println("ASCII Decrypt: " + Decrypt2(key, e_msg2));
    }
}
