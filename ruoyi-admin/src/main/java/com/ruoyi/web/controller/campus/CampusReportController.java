package com.ruoyi.web.controller.campus;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.report.ICampusReportService;

@RestController
@RequestMapping("/campus/report")
public class CampusReportController extends BaseController {
    private final ICampusReportService reports;
    public CampusReportController(ICampusReportService reports) { this.reports = reports; }
    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:report:view')")
    @GetMapping("/summary")
    public AjaxResult summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return success(reports.summary(startDate, endDate));
    }
}
