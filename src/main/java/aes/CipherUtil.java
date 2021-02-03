package aes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {
	private static byte[] randomKey;
	//초기화벡터. : 첫번째 블럭에 추가되는 값.
	//CBC 모드: 블럭암호화시 앞 블럭의 암호문이 뒤의 블럭의 암호화시 삽입됨.
	//패딩방법 : 마지막블럭의 자리수를 채우기 위한 방법
	private final static byte[] iv = new byte[] {
			(byte) 0x8E, 0x12, 0x39,(byte) 0x9C, 
			       0x07, 0x72, 0x6F, (byte) 0x5A,
			(byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 
			       0x07, 0x72, 0x6F, (byte) 0x5A };
	static Cipher cipher; //암호객체
	static {
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // 암호화알고리즘/블럭암호화모드/패딩방법
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static byte[] getRandomKey(String algo) throws NoSuchAlgorithmException {
		//AES용 키 생성객체 
		KeyGenerator keyGen = KeyGenerator.getInstance(algo);
		//128비트의 키를 생성.
		keyGen.init(128);  //128비트 ~ 196비트 키의크기가 가능.
		SecretKey key = keyGen.generateKey(); //랜덤키 생성
		return key.getEncoded();
	}
	//평문을 암호화하는 메서드
	public static String encrypt(String plain) {
		byte[] cipherMsg = new byte[1024];
		try {
			randomKey = getRandomKey("AES"); //암호화에 사용된 키 설정. 복호화에서 사용되야 함.
			Key key = new SecretKeySpec(randomKey, "AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv); //초기화 벡터 설정.
			// AES 알고리즘. CBC모드, PKCS5Padding, 암호화 모드, 키 설정, 초기화 벡터 
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			cipherMsg = cipher.doFinal(plain.getBytes());// 암호화실행
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg).trim(); //16진수코드값의 문자열 리턴.
	}
	private static String byteToHex(byte[] cipherMsg) {
		if (cipherMsg == null)	return null;
		String str = "";
		for (byte b : cipherMsg) {
			str += String.format("%02X", b);
		}
		return str;
	}
	//복호화 기능 메서드
	public static String decrypt(String cipherMsg) {
		byte[] plainMsg = new byte[1024];
		try {
			//randomKey : 암호화시에 사용되었던 키.
			Key key = new SecretKeySpec(randomKey, "AES"); //키 설정
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv); //초기화블럭 설정
			// AES 알고리즘. CBC모드, PKCS5Padding, 복호화 모드, 키 설정, 초기화 벡터
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			plainMsg = cipher.doFinal(hexToByte(cipherMsg.trim()));  //복호화 실행
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}
	//16진수로 나열된 문자열값을 byte[]배열로 리턴
	private static byte[] hexToByte(String str) {
		if (str == null || str.length() < 2)	return null;
		int len = str.length() / 2;
		byte[] buf = new byte[len];
		for (int i = 0; i < len; i++) {
			buf[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
		}
		return buf;
	}
	//키 설정 후 암호화 하기
	public static String encrypt(String plain, String key) {
		byte[] cipherMsg = new byte[1024];
		try {
			Key genKey = new SecretKeySpec(makeKey(key), "AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, genKey, paramSpec);
			cipherMsg = cipher.doFinal(plain.getBytes()); //암호화 실행
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg); //16진수로 표시된 문자열
	}
	// 128비트 = 16바이트
	private static byte[] makeKey(String key) {
		//key : abc1234567
		int len = key.length();
		char ch = 'A';
		for (int i = len; i < 16; i++)
			key += ch++;
		return key.substring(0, 16).getBytes();
	}
	public static String decrypt(String cipher1, String key) {
		byte[] plainMsg = new byte[1024];
		try {
			Key genKey = new SecretKeySpec(makeKey(key), "AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, genKey, paramSpec);
			plainMsg = cipher.doFinal(hexToByte(cipher1.trim())); //복호화 실행
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}
	public static String makehash(String msg) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] plain = msg.getBytes();
		byte[] hash = md.digest(plain);
		return byteToHex(hash);
	}
	
	

	public static void encryptFile
	        (String plainFile, String cipherFile, String strkey) {
		try {
			getKey(strkey); //key 파일에 등록
			ObjectInputStream ois = 
			 new ObjectInputStream(new FileInputStream("key.ser"));
			Key key = (Key) ois.readObject();
			ois.close();
			AlgorithmParameterSpec paramSpec = 
					                        new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			FileInputStream fis = new FileInputStream(plainFile);
			FileOutputStream fos = new FileOutputStream(cipherFile);
			//암호 관련 스트림
			CipherOutputStream cos = new CipherOutputStream
					                                 (fos, cipher);
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) != -1) {
				cos.write(buf, 0, len);//암호화된 내용으로 저장
			}
			fis.close(); cos.flush();	fos.flush();
			cos.close(); fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//key.ser 파일에 키를 등록.
	private static void getKey(String key) throws Exception {
		Key genkey = new SecretKeySpec(makeKey(key), "AES");
		//키를 파일에 저장.
		ObjectOutputStream out = new ObjectOutputStream
				                 (new FileOutputStream("key.ser"));
		out.writeObject(genkey);
		out.flush();	out.close();
	}
	public static void decryptFile
	                    (String cipherFile, String plainFile) {
		try {
			ObjectInputStream ois = new ObjectInputStream
					               (new FileInputStream("key.ser"));
			Key key = (Key) ois.readObject();
			ois.close();
			AlgorithmParameterSpec paramSpec = 
					                        new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			FileInputStream fis = new FileInputStream(cipherFile);
			FileOutputStream fos = new FileOutputStream(plainFile);
			CipherOutputStream cos = new CipherOutputStream
					                                (fos, cipher);
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) != -1) {
				cos.write(buf, 0, len); //복호화된 내용을 출력
			}
			fis.close();
			cos.flush();
			fos.flush();
			cos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}