package aes;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * useraccount 테이블의  email 값을 읽어서  usersecurity 테이블의 email을 암호화 하기.
 *   1. email 컬럼의 크기 300으로 변경하기
 *   2. key 는 userid의 (SHA-256)해쉬값의 문자열 앞 16자리로 정한다
 */
public class CipherMain3 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection
	 ("jdbc:mariadb://localhost:3307/classdb","scott","1234");
		PreparedStatement pstmt = conn.prepareStatement("select * from useraccount");
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			String userid = rs.getString("userid");
			String key = CipherUtil.makehash(userid).substring(0,16);
			String email = rs.getString("email");
			String newEmail = CipherUtil.encrypt(email,key);
			pstmt = conn.prepareStatement("update usersecurity set email=? where userid=?");
			pstmt.setString(1, newEmail);
			pstmt.setString(2, userid);
			pstmt.executeUpdate();			
		}
	}
}
