package com.ruoyi.system.service.report;

import java.time.LocalDate;
import com.ruoyi.system.domain.report.CampusReport;

public interface ICampusReportService {
    CampusReport summary(LocalDate startDate, LocalDate endDate);
}
