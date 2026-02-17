package org.example.lebonachat.ModuleReport.Repository;

import org.example.lebonachat.ModuleReport.Metier.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    @Query("""
        SELECT COUNT(u)
        FROM UserReport u
        WHERE u.reportedUser.id = :userId
    """)
    long countReportsByUser(@Param("userId") Long userId);
}
