package org.example.lebonachat.ModuleReport.Metier;

import jakarta.persistence.*;
import lombok.*;
import org.example.lebonachat.ModuleAnnonce.Metier.Announcement;
import org.example.lebonachat.ModuleUser.Metier.utilisateur;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   private Long ReportCount;
    @ManyToOne
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

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

    public long getReportCount() {
        return ReportCount;
    }

    public void setReportCount(Long reportCount) {
        ReportCount = reportCount;
    }
}