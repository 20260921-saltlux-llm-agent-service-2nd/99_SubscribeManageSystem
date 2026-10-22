package solution.repository;

import solution.model.Membership;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MembershipRepository {

    private static final String QUERY_FILE =
            "src/main/java/solution/mapper/membership-query.xml";

    private final Properties prop = new Properties();

    public MembershipRepository() {
        try (FileInputStream inputStream = new FileInputStream(QUERY_FILE)) {
            prop.loadFromXML(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("구독 SQL 파일을 읽지 못했습니다.", e);
        }
    }

    public boolean addMembership(int memberId,
                                 String ottType,
                                 String planName,
                                 int subscriptionDays) {
        String query = prop.getProperty("insertMembership");

        try (Connection connection = JdbcConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, memberId);
            preparedStatement.setString(2, ottType);
            preparedStatement.setString(3, planName);
            preparedStatement.setInt(4, subscriptionDays);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("구독 추가 중 오류가 발생했습니다.", e);
        }
    }

    public List<Membership> findActiveMembershipsByMemberId(int memberId) {
        List<Membership> memberships = new ArrayList<>();
        String query = prop.getProperty("selectActiveMemberships");

        try (Connection connection = JdbcConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, memberId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    memberships.add(mapMembership(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("현재 구독 목록 조회 중 오류가 발생했습니다.", e);
        }

        return memberships;
    }

    public List<Membership> findAllMembershipsByMemberId(int memberId) {
        List<Membership> memberships = new ArrayList<>();
        String query = prop.getProperty("selectAllMemberships");

        try (Connection connection = JdbcConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, memberId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    memberships.add(mapMembership(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("전체 구독 기록 조회 중 오류가 발생했습니다.", e);
        }

        return memberships;
    }

    public boolean extendMembership(int membershipId, int memberId, int extensionDays) {
        String query = prop.getProperty("updateMembershipExpiry");

        try (Connection connection = JdbcConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, extensionDays);
            preparedStatement.setInt(2, membershipId);
            preparedStatement.setInt(3, memberId);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("구독 연장 중 오류가 발생했습니다.", e);
        }
    }

    public boolean deleteMembership(int membershipId, int memberId) {
        String query = prop.getProperty("deleteMembership");

        try (Connection connection = JdbcConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, membershipId);
            preparedStatement.setInt(2, memberId);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("구독 기록 삭제 중 오류가 발생했습니다.", e);
        }
    }

    public Map<String, List<String>> findSubscribedPlansByOtt(int memberId) {
        Map<String, List<String>> planHistory = new LinkedHashMap<>();
        String query = prop.getProperty("selectPlanHistory");

        try (Connection connection = JdbcConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, memberId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String ottType = resultSet.getString("ott_type");
                    String planName = resultSet.getString("plan_name");

                    planHistory
                            .computeIfAbsent(ottType, key -> new ArrayList<>())
                            .add(planName);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("OTT별 플랜 이력 조회 중 오류가 발생했습니다.", e);
        }

        return planHistory;
    }

    private Membership mapMembership(ResultSet resultSet) throws SQLException {
        return new Membership(
                resultSet.getInt("membership_id"),
                resultSet.getInt("member_id"),
                resultSet.getString("ott_type"),
                resultSet.getString("plan_name"),
                resultSet.getTimestamp("enroll_date").toLocalDateTime(),
                resultSet.getTimestamp("expiry_date").toLocalDateTime(),
                resultSet.getBoolean("active")
        );
    }
}
