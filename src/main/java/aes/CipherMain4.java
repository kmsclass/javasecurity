package aes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * usersecurity 테이블을 읽어서 화면에 사용자id와 복호화해서 이메일을 출력하기.
 *   1. key는 암호화시에 사용되었던 키로 한다.
 */
public class CipherMain4 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/classdb","scott","1234");
		PreparedStatement pstmt = conn.prepareStatement("select userid,email from usersecurity");
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			String userid = rs.getString("userid");
			String key = CipherUtil.makehash(userid).substring(0,16);
			String email = rs.getString("email"); 
			String plainEmail = CipherUtil.decrypt(email,key);
			System.out.println(userid + ": 이메일 = " + plainEmail);
		}
	}
}
