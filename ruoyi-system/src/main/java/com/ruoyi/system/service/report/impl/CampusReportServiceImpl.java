package com.ruoyi.system.service.report.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.report.BillSummary;
import com.ruoyi.system.domain.report.CampusReport;
import com.ruoyi.system.mapper.report.CampusReportMapper;
import com.ruoyi.system.service.report.ICampusReportService;

@Service
public class CampusReportServiceImpl implements ICampusReportService {
    private static final LocalDate MIN_DATE = LocalDate.of(1000, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);
    private final CampusReportMapper mapper;
    public CampusReportServiceImpl(CampusReportMapper mapper) { this.mapper = mapper; }
    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public CampusReport summary(LocalDate startDate, LocalDate endDate) {
        if ((startDate != null && (startDate.isBefore(MIN_DATE) || startDate.isAfter(MAX_DATE)))
                || (endDate != null && (endDate.isBefore(MIN_DATE) || endDate.isAfter(MAX_DATE))))
            throw new ServiceException("日期超出支持范围");
        if (startDate != null && endDate != null && startDate.isAfter(endDate))
            throw new ServiceException("开始日期不能晚于结束日期");
        LocalDateTime from = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime to = endDate == null || endDate.equals(MAX_DATE) ? null : endDate.plusDays(1).atStartOfDay();
        CampusReport report = new CampusReport();
        report.setStartDate(startDate == null ? null : startDate.toString());
        report.setEndDate(endDate == null ? null : endDate.toString());
        report.setVehicles(mapper.vehicleSummary());
        report.setStations(mapper.stationSummary());
        report.setOrders(mapper.orderSummary(from, to));
        BillSummary bills = mapper.billSummary(from, to);
        bills.setCashReceived(mapper.cashReceived(from, to));
        report.setBills(bills);
        return report;
    }
}
