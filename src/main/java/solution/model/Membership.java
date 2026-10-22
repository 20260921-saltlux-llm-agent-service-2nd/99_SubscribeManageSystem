package solution.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Membership {

    private int membershipId;
    private int memberId;
    private String ottType;
    private String planName;
    private LocalDateTime enrollDate;
    private LocalDateTime expiryDate;
    private boolean active;

    @Override
    public String toString() {
        return "Membership{" +
                "membershipId=" + membershipId +
                ", memberId=" + memberId +
                ", ottType='" + ottType + '\'' +
                ", planName='" + planName + '\'' +
                ", enrollDate=" + enrollDate +
                ", expiryDate=" + expiryDate +
                ", active=" + active +
                '}';
    }
}
