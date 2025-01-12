package security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.SecureRandom;

public class Encryption {
    //Generate AES Key
    public static SecretKey generateKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        generator.init(256);
        return generator.generateKey();
    }
    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    public static byte[] encrypt(SecretKey key, String input, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encrypted = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encode(encrypted);
    }

    public static String decrypt(SecretKey key, String input, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(input));
        return new String(decrypted);
    }
    public static String generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; //16 bytes is common salting size
        random.nextBytes(salt);//Fills array with random bytes
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        String input = password + salt;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(digest);
    }

}
