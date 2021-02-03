package hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
  1. usersecurity 테이블 생성 
    db useraccount => usersecurity 테이블로 모든 내용 저장
   create table usersecurity as select * from useraccount
   SELECT * FROM usersecurity
   ALTER TABLE usersecurity MODIFY PASSWORD VARCHAR(250) NOT NULL
   DESC usersecurity  => password 컬럼이 varchar(250) 수정.
   
  2. 프로그램 작성 
   useraccount 테이블을 읽어서 usersecurity테이블의 password 컬럼을 SHA256 알고리즘을 이용하여 해쉬값으로 수정하기
 */
public class DigestMain2 {
	public static void main(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection
		("jdbc:mariadb://localhost:3307/classdb","scott","1234"); 
		PreparedStatement pstmt = conn.prepareStatement	("select userid, password from useraccount");
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			String id = rs.getString(1);
			String pass = rs.getString(2);
			MessageDigest md=MessageDigest.getInstance("SHA-256");
			String hashpass = "";
			byte[] plain = pass.getBytes();
			byte[] hash = md.digest(plain); //해쉬실행
			for(byte b : hash) {
				hashpass += String.format("%02X", b);
			}
			pstmt.close();
			pstmt = conn.prepareStatement("update usersecurity set password=? where userid=?");
			pstmt.setString(1, hashpass);
			pstmt.setString(2, id);
			pstmt.executeUpdate();
	    }
	}
}
