package org.example.lebonachat.ModuleReport.Repository;

import org.example.lebonachat.ModuleReport.Metier.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}
