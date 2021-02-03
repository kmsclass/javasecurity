package aes;
/*
 * AES 암호화 : 대칭키 암호화 (암호,복호에 사용되는 키가 같은 경우)
 */
public class CipherMain1 {
	public static void main(String[] args) {
		String plain1 = "안녕하세요. 홍길동 입니다."; //평문
		String cipher1 = CipherUtil.encrypt(plain1); //암호화
		System.out.println("암호문:" + cipher1);
		String plain2 = CipherUtil.decrypt(cipher1); //복호화
		System.out.println("복호문:" + plain2); //평문. plain1문장과 같은 문장. 복호화. 
	}
}
