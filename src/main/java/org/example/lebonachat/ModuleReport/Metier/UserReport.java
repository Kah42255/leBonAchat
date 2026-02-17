package org.example.lebonachat.ModuleReport.Metier;



import jakarta.persistence.*;
import lombok.*;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ReportCount;
    // L'utilisateur signalé
    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = false)
    private utilisateur reportedUser;

    // L'utilisateur qui fait le signalement
    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private utilisateur reporter;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @Column(length = 500)
    private String adminComment;

    @PrePersist
    protected void onCreate() {
        reportedAt = LocalDateTime.now();
        status = ReportStatus.PENDING;
    }

    public Long getReportCount() {
        return ReportCount;
    }

    public void setReportCount(Long reportCount) {
        ReportCount = reportCount;
    }
}

