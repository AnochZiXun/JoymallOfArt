package tw.com.joymall;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
public class Blowfish {

	/**
	 * @param message 加密前的字串
	 * @return 加密後的字串
	 */
	public static String encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		return encrypt(message, false);
	}

	/**
	 * @param message 加密前的字串
	 * @param random 是否隨機
	 * @return 加密後的字串
	 */
	public static String encrypt(String message, boolean random) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		@SuppressWarnings("MismatchedReadAndWriteOfArray")
		final byte[] KEY_MATERIAL = "TJP3omnrODMQJTBI".getBytes();
		KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
		keyGenerator.init(128);
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.ENCRYPT_MODE, random ? keyGenerator.generateKey() : new SecretKeySpec(KEY_MATERIAL, "Blowfish"));
		StringBuilder stringBuilder = new StringBuilder();
		for (byte b : cipher.doFinal(message.getBytes())) {
			stringBuilder.append(String.format("%02x", b));
		}
		return stringBuilder.reverse().toString();
	}
}
