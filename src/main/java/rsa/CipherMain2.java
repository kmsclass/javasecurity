package rsa;

import java.util.Scanner;

public class CipherMain2 {
	public static void main(String[] args) {
		CipherRSA.getKey();
		Scanner scan = new Scanner(System.in);
		String str1=null,str2=null;
		String org = null;
		String result = null;
		while(true) {
			System.out.println("문서의 종류 선택(1.기밀문서,2.본인작성표시,9.종료)");
			int menu1 = scan.nextInt();
			if(menu1==9) break;
            System.out.println("암호화(1)/복호화(2)");
			int menu2 = scan.nextInt();
			str1 = (menu1==1)?"기밀문서":"본인작성표시";
			str2 = (menu2==1)?"암호":"복호";
			System.out.println(str1 + " "+ str2 + "를 위한 내용을 입력하세요");
			org = scan.next();
			result = (menu2==1)?CipherRSA.encrypt(org,menu1):
			                   CipherRSA.decrypt(org,menu1);
			System.out.println("=========" + str2 + "=========");
			System.out.println(result);
		}
	}
}
