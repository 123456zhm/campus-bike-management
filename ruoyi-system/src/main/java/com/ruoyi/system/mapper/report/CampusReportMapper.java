package com.ruoyi.system.mapper.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.report.VehicleSummary;
import com.ruoyi.system.domain.report.StationSummary;
import com.ruoyi.system.domain.report.OrderSummary;
import com.ruoyi.system.domain.report.BillSummary;

public interface CampusReportMapper {
    VehicleSummary vehicleSummary();
    List<StationSummary> stationSummary();
    OrderSummary orderSummary(@Param("fromTime") LocalDateTime fromTime, @Param("toTime") LocalDateTime toTime);
    BillSummary billSummary(@Param("fromTime") LocalDateTime fromTime, @Param("toTime") LocalDateTime toTime);
    BigDecimal cashReceived(@Param("fromTime") LocalDateTime fromTime, @Param("toTime") LocalDateTime toTime);
}
