import java.security.*;
//import java.security.spec.*;
import javax.crypto.*;
public class RSA {

    private enum KeyType
    {
        PUBLIC, PRIVATE;
    }
    private static KeyPair keypair;
    private static Cipher cipher = null;

    /**
     * Generates a public and private key pair
     * @param size the size of the key
     */
    private static void initialize_key(int size)
    {
        try
        {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(size);
            keypair = generator.generateKeyPair();

            //KeyFactory factory = KeyFactory.getInstance("RSA");
            //RSAPublicKeySpec rsa_public = factory.getKeySpec(keypair.getPublic(), RSAPublicKeySpec.class);
            //RSAPrivateKeySpec rsa_private = factory.getKeySpec(keypair.getPrivate(), RSAPrivateKeySpec.class);

            //System.out.println("e = Public Key Exponent : " + rsa_public.getPublicExponent() + "\n");
            //System.out.println("d = Private Key Exponent : " + rsa_private.getPrivateExponent() + "\n");
            //System.out.println("n = Modulus : " + rsa_public.getModulus() + "\n");
        }
        catch (Exception e)
        {
            System.err.println("Oops! There were some problems generating the keypair.\n" + e.getMessage() + "\n");
        }

        initCipher();
    }

    /**
     * Gets the RSA keypair
     * @return the generated keypair
     */
    private static KeyPair getRSAKeyPair()
    {
        return keypair;
    }

    /**
     * Initializes the cipher as RSA
     */
    private static void initCipher()
    {
        if(cipher == null)
        {
            try
            {
                cipher = Cipher.getInstance("RSA");
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Encrypts some message
     * @param message the message string that needs to be encrypted
     * @param type determines which key will be used for encryption (public or private)
     * @return
     */
    public static byte[] Encrypt(String message, KeyType type)
    {
        try
        {
            byte[] encrypted = null;
            if(type == KeyType.PUBLIC)
            {
                cipher.init(Cipher.ENCRYPT_MODE, getRSAKeyPair().getPublic());
            }
            else
            {
                cipher.init(Cipher.ENCRYPT_MODE, getRSAKeyPair().getPrivate());
            }
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
     * Decrypts the bytes of some message
     * @param message the message that needs to be decrypted
     * @param type determines which key will be used for decryption (public or private)
     * @return the decrypted message string
     */
    public static String Decrypt(byte[] message, KeyType type)
    {
        try
        {
            byte decrypted[] = null;
            if(type == KeyType.PUBLIC)
            {
                cipher.init(Cipher.DECRYPT_MODE, getRSAKeyPair().getPublic());
            }
            else
            {
                cipher.init(Cipher.DECRYPT_MODE, getRSAKeyPair().getPrivate());
            }
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
        initialize_key(2048);
        byte[] e_msg_public = Encrypt(msg, KeyType.PUBLIC);
        if(e_msg_public != null)
        {
            System.out.println("The message encrypted with public key is: " + new String(e_msg_public));
            String d_msg_private = Decrypt(e_msg_public, KeyType.PRIVATE);
            System.out.println("The message decrypted with private key is: " + d_msg_private);
        }

        byte[] e_msg_private = Encrypt(msg, KeyType.PRIVATE);
        if(e_msg_private != null)
        {
            System.out.println("The message encrypted with private key is: " + new String(e_msg_private));
            String d_msg_public = Decrypt(e_msg_private, KeyType.PUBLIC);
            System.out.println("The message decrypted with public key is: " + d_msg_public);
        }
    }
}
