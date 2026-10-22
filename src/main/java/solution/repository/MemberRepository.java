package solution.repository;

import solution.model.Member;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class MemberRepository {

    private static final String QUERY_FILE =
            "src/main/java/solution/mapper/member-query.xml";

    private final Properties prop = new Properties();

    public MemberRepository() {
        try (FileInputStream inputStream = new FileInputStream(QUERY_FILE)) {
            prop.loadFromXML(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("회원 SQL 파일을 읽지 못했습니다.", e);
        }
    }

    public boolean save(Member member) {
        String query = prop.getProperty("insertMember");

        try (Connection connection = JdbcConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, member.getName());
            preparedStatement.setInt(2, member.getAge());
            preparedStatement.setString(3, member.getPhone());

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("회원 저장 중 오류가 발생했습니다.", e);
        }
    }

    public Member findByPhone(String phone) {
        String query = prop.getProperty("selectMemberByPhone");

        try (Connection connection = JdbcConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, phone);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Member(
                            resultSet.getInt("member_id"),
                            resultSet.getString("name"),
                            resultSet.getInt("age"),
                            resultSet.getString("phone")
                    );
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("회원 조회 중 오류가 발생했습니다.", e);
        }

        return null;
    }

    public boolean deleteById(int memberId) {
        String query = prop.getProperty("deleteMemberById");

        try (Connection connection = JdbcConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, memberId);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("회원 탈퇴 중 오류가 발생했습니다.", e);
        }
    }
}
