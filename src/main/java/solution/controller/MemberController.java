package solution.controller;

import solution.model.Member;
import solution.model.Membership;
import solution.repository.MemberRepository;
import solution.repository.MembershipRepository;
import solution.view.MemberView;

import java.util.List;
import java.util.Map;

public class MemberController {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final MemberView memberView;
    private Member loginMember;

    public MemberController() {
        memberRepository = new MemberRepository();
        membershipRepository = new MembershipRepository();
        memberView = new MemberView();
    }

    public void signUp(String name, int age, String phone) {
        if (name == null || name.isBlank()
                || phone == null || phone.isBlank()
                || age < 1) {
            memberView.displayMessage("회원 정보를 올바르게 입력하세요.");
            return;
        }

        if (memberRepository.findByPhone(phone) != null) {
            memberView.displayMessage("이미 등록된 휴대폰 번호입니다.");
            return;
        }

        Member member = new Member(0, name, age, phone);
        boolean saved = memberRepository.save(member);

        if (saved) {
            memberView.displayMessage("회원가입이 완료되었습니다.");
        } else {
            memberView.displayMessage("회원가입에 실패했습니다.");
        }
    }

    public boolean login(String phone) {
        Member member = memberRepository.findByPhone(phone);

        if (member == null) {
            memberView.displayMessage("등록되지 않은 휴대폰 번호입니다.");
            return false;
        }

        loginMember = member;
        memberView.displayMessage(loginMember.getName() + "님, 환영합니다.");
        return true;
    }

    public void addMyMembership(String ottType, String planName, int subscriptionDays) {
        if (!checkLogin()) {
            return;
        }

        if (ottType == null || ottType.isBlank()
                || planName == null || planName.isBlank()
                || subscriptionDays < 1) {
            memberView.displayMessage("구독 정보를 올바르게 입력하세요.");
            return;
        }

        boolean added = membershipRepository.addMembership(
                loginMember.getMemberId(),
                ottType,
                planName,
                subscriptionDays
        );

        if (added) {
            memberView.displayMessage("구독이 추가되었습니다.");
        } else {
            memberView.displayMessage("구독 추가에 실패했습니다.");
        }
    }

    public void displayMyActiveMemberships() {
        if (!checkLogin()) {
            return;
        }

        List<Membership> memberships =
                membershipRepository.findActiveMembershipsByMemberId(loginMember.getMemberId());

        if (memberships.isEmpty()) {
            memberView.displayMessage("현재 이용 중인 구독이 없습니다.");
            return;
        }

        memberView.displayMemberships(memberships);
    }

    public boolean displayMyMembershipHistory() {
        if (!checkLogin()) {
            return false;
        }

        List<Membership> memberships =
                membershipRepository.findAllMembershipsByMemberId(loginMember.getMemberId());

        if (memberships.isEmpty()) {
            memberView.displayMessage("선택할 수 있는 구독 기록이 없습니다.");
            return false;
        }

        memberView.displayMembershipHistory(memberships);
        return true;
    }

    public void extendMyMembership(int membershipId, int extensionDays) {
        if (!checkLogin()) {
            return;
        }

        if (membershipId < 1 || extensionDays < 1) {
            memberView.displayMessage("구독 ID와 연장 일수는 1 이상이어야 합니다.");
            return;
        }

        boolean extended = membershipRepository.extendMembership(
                membershipId,
                loginMember.getMemberId(),
                extensionDays
        );

        if (extended) {
            memberView.displayMessage("구독 기간이 연장되었습니다.");
        } else {
            memberView.displayMessage("연장할 수 있는 내 구독을 찾지 못했습니다.");
        }
    }

    public void deleteMyMembership(int membershipId) {
        if (!checkLogin()) {
            return;
        }

        if (membershipId < 1) {
            memberView.displayMessage("구독 ID는 1 이상이어야 합니다.");
            return;
        }

        boolean deleted = membershipRepository.deleteMembership(
                membershipId,
                loginMember.getMemberId()
        );

        if (deleted) {
            memberView.displayMessage("구독 기록이 삭제되었습니다.");
        } else {
            memberView.displayMessage("삭제할 수 있는 내 구독을 찾지 못했습니다.");
        }
    }

    public void displayMyPlanHistoryByOtt() {
        if (!checkLogin()) {
            return;
        }

        Map<String, List<String>> planHistory =
                membershipRepository.findSubscribedPlansByOtt(loginMember.getMemberId());

        if (planHistory.isEmpty()) {
            memberView.displayMessage("구독 이력이 없습니다.");
            return;
        }

        memberView.displayPlanHistoryByOtt(planHistory);
    }

    public boolean deleteMyAccount() {
        if (!checkLogin()) {
            return false;
        }

        boolean deleted = memberRepository.deleteById(loginMember.getMemberId());

        if (deleted) {
            loginMember = null;
            memberView.displayMessage("회원 탈퇴가 완료되었습니다.");
            return true;
        }

        memberView.displayMessage("회원 탈퇴에 실패했습니다.");
        return false;
    }

    public void logout() {
        loginMember = null;
        memberView.displayMessage("로그아웃되었습니다.");
    }

    private boolean checkLogin() {
        if (loginMember == null) {
            memberView.displayMessage("로그인이 필요합니다.");
            return false;
        }

        return true;
    }
}
