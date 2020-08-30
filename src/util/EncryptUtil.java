package util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
  
/*
 * AES 암호화 (문자열 암호화, 복호화)
 */
public class EncryptUtil {
	private static final String MODE = "AES/CBC/PKCS5Padding";
	private String ENCODE = "UTF-8";
//ddd
	private Key keySpec;
	private String iv;
	private String key = "";

	public EncryptUtil(String key) throws UnsupportedEncodingException {
		this.key = key;
		iv = this.key.substring(0, 16);
		byte[] keyBytes = new byte[16];
		byte[] b = iv.getBytes(ENCODE);
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		this.keySpec = keySpec;
	}

//	// 암호화
//	public String aesEncode(String str)
//			throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
//			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
//		Cipher c = Cipher.getInstance(MODE);
//		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(this.iv.getBytes()));
//
//		byte[] encrypted = c.doFinal(str.getBytes(ENCODE));
//		String enStr = new String(Base64.encodeBase64(encrypted));
//
//		return enStr;
//	}
//
//	// 복호화
//	public String aesDecode(String str)
//			throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
//			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
//		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(this.iv.getBytes("UTF-8")));
//
//		byte[] byteStr = Base64.decodeBase64(str.getBytes());
//
//		return new String(c.doFinal(byteStr), "UTF-8");
//	}
	
	/**
	 * AES 256 으로 암호화 한다.
	 * @param msg
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidParameterSpecException
	 * @throws UnsupportedEncodingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String encryptAES256(String msg, String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		byte[] saltBytes = bytes;
		
		// Password-Based Key Derivation function 2
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		// 70000번 해시하여 256 bit 길이의 키를 만든다.
		PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, 70000, 256);
		
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
		
		// 알고리즘/모드/패딩
		// CBC : Cipher Block Chaining Mode
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		// Initial Vector(1단계 암호화 블록용)
		byte[] ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
		
		byte[] encryptedTextBytes = cipher.doFinal(msg.getBytes("UTF-8"));
		
		byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];
		System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
        System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
	    System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);
	    
		return Base64.getEncoder().encodeToString(buffer);
	}
	
	/**
	 * 위에서 암호화된 내용을 복호화 한다.
	 * @param msg
	 * @param key
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String decryptAES256(String msg, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(msg));
		
		byte[] saltBytes = new byte[20];
		buffer.get(saltBytes, 0, saltBytes.length);
		byte[] ivBytes = new byte[cipher.getBlockSize()];
		buffer.get(ivBytes, 0, ivBytes.length);
		byte[] encryoptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes.length];
		buffer.get(encryoptedTextBytes);
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, 70000, 256);
		
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
		
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
		
		byte[] decryptedTextBytes = cipher.doFinal(encryoptedTextBytes);
		return new String(decryptedTextBytes);
	}
} // https://blog.miyam.net/74
