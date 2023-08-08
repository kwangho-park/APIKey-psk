package kr.com.dreamsecurity.hotp.dao;

import com.dreamsecurity.jcaos.asn1.x509.S;
import kr.com.dreamsecurity.hotp.vo.SystemInfoDTO;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * system 에서 사용하는 RESTful API를 필터링로직에서 사용하기위한 DAO  (by JNDI lib)
 */
public class SystemDAO {

    private DataSource dataSource;

    // [참고] was의 conf/context.xml 에 jdbi 설정필요
    public SystemDAO() {
        try {

            Context context = new InitialContext();

            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/mysql");
            System.out.println("[DB] connection open");

        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    // 시스템 정보 조회 //
    public SystemInfoDTO selectSystem(String systemId) throws SQLException {

        SystemInfoDTO systemInfoDTO = new SystemInfoDTO();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = dataSource.getConnection();

            String query = "SELECT id, system_id, system_pw, secret_key, secret_iv "
                    + " FROM system_info "
                    + " WHERE system_id = ? ";

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, systemId);


            rs =pstmt.executeQuery();

            while(rs.next()) {
                systemInfoDTO.setId(rs.getInt("id"));
                systemInfoDTO.setSystemId(rs.getString("system_id"));
                systemInfoDTO.setSystemPW(rs.getString("system_pw"));
                systemInfoDTO.setSecretKey(rs.getString("secret_key"));
                systemInfoDTO.setSecretIv(rs.getString("secret_iv"));
            }


        }catch(Exception e) {
            e.printStackTrace();

        }finally {
            try {
                if(rs !=null) rs.close();
                if(pstmt != null) pstmt.close();
                if(conn != null) conn.close();
                System.out.println("[DB] connection close");

            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        return systemInfoDTO;

    }



//
//    // 게시글 저장 //
//    public void insertPost(PostDto dto) {
//
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//
//        try {
//
//            conn = dataSource.getConnection();
//
//            String query = "INSERT INTO TB_POST(PT_TITLE, PT_CONTENT, PT_CATEGORY) "
//                    + " VALUES(?,?,?)";
//
//            pstmt = conn.prepareStatement(query);			/// prepare :준비하다 + statement : 진술서 (=query 준비 )
//
//            pstmt.setString(1, dto.getPtTitle());
//            pstmt.setString(2, dto.getPtContent());
//            pstmt.setString(3, dto.getPtCategory());
//
//            pstmt.executeUpdate();
//
////			conn.commit();								// auto commit 옵션이 false 일 경우에만 수동 commit 가능 (conn.setAutoCommit(false) )
//
//        }catch(Exception e) {
//            e.printStackTrace();
//
//        }finally {
//            try {
//                if(pstmt != null) pstmt.close();
//                if(conn != null) conn.close();
//                System.out.println("[DB] connection close");
//
//            }catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
//    } // insertPost() END



}
