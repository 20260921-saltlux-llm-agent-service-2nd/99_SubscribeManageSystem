package solution.view;

import solution.model.Membership;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class MemberView {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void displayMemberships(List<Membership> memberships) {
        System.out.println("===== 현재 이용 중인 구독 =====");

        memberships.forEach(membership -> System.out.printf(
                "구독 ID: %d | OTT: %s | 플랜: %s | 가입일: %s | 만료일: %s%n",
                membership.getMembershipId(),
                membership.getOttType(),
                membership.getPlanName(),
                membership.getEnrollDate().format(DATE_TIME_FORMATTER),
                membership.getExpiryDate().format(DATE_TIME_FORMATTER)
        ));
    }

    public void displayMembershipHistory(List<Membership> memberships) {
        System.out.println("===== 선택 가능한 내 구독 기록 =====");
        LocalDateTime now = LocalDateTime.now();

        memberships.forEach(membership -> {
            boolean currentlyActive = membership.isActive()
                    && !membership.getExpiryDate().isBefore(now);
            String status = currentlyActive ? "이용 중" : "만료/비활성";

            System.out.printf(
                    "구독 ID: %d | OTT: %s | 플랜: %s | 가입일: %s | 만료일: %s | 상태: %s%n",
                    membership.getMembershipId(),
                    membership.getOttType(),
                    membership.getPlanName(),
                    membership.getEnrollDate().format(DATE_TIME_FORMATTER),
                    membership.getExpiryDate().format(DATE_TIME_FORMATTER),
                    status
            );
        });
    }

    public void displayPlanHistoryByOtt(Map<String, List<String>> planHistory) {
        System.out.println("===== OTT별 이용했던 플랜 =====");

        planHistory.forEach((ottType, planNames) ->
                System.out.println(ottType + " : " + String.join(", ", planNames))
        );
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
