import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;

public class AES {

    private static final String ENCRYTPION = "AES";
    private static final String CIPHER = "AES/ECB/PKCS5Padding";
    private static final int PACKET_SIZE = 512;

    private static Cipher cipher;
    private SecretKey secretKey;

    /**
     * Generates a new AES encryption instance, with a secret key
     * @throws Exception for NoSuchAlgorith and PaddingException
     */
    public AES() throws Exception{

        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYTPION);
        keyGenerator.init(128);
        secretKey = keyGenerator.generateKey();

    }

    /**
     * Ecnrypts a byte under AES
     * @param toEncrypt the byte to be encrypted
     * @return encyrpted bytes
     * @throws Exception cipher init
     */
    public byte[] encyrpt(byte[] toEncrypt) throws Exception{

        cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(toEncrypt);
        return encrypted;

    }

    /**
     * Decryptes bytes under AES
     * @param encyrpted byte array of encrypted data
     * @return byte array of decrypted data
     * @throws Exception cipher
     */
    public byte[] decrypt(byte[] encyrpted) throws Exception{

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(encyrpted);
        return decrypted;

    }


}
