package com.ruoyi.system.domain.rental;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BikeRentalOrder
{
    private Long id;
    private String orderNo;
    private Long userId;
    private Long vehicleId;
    private String vehicleNo;
    private Long startStationId;
    private String startStationName;
    private Long endStationId;
    private String endStationName;
    private Long billingRuleId;
    private Integer unitMinutes;
    private BigDecimal unitPrice;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private BigDecimal payableAmount;
    private String status;

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String value) { this.orderNo = value; }
    public Long getUserId() { return userId; }
    public void setUserId(Long value) { this.userId = value; }
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long value) { this.vehicleId = value; }
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String value) { this.vehicleNo = value; }
    public Long getStartStationId() { return startStationId; }
    public void setStartStationId(Long value) { this.startStationId = value; }
    public String getStartStationName() { return startStationName; }
    public void setStartStationName(String value) { this.startStationName = value; }
    public Long getEndStationId() { return endStationId; }
    public void setEndStationId(Long value) { this.endStationId = value; }
    public String getEndStationName() { return endStationName; }
    public void setEndStationName(String value) { this.endStationName = value; }
    public Long getBillingRuleId() { return billingRuleId; }
    public void setBillingRuleId(Long value) { this.billingRuleId = value; }
    public Integer getUnitMinutes() { return unitMinutes; }
    public void setUnitMinutes(Integer value) { this.unitMinutes = value; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal value) { this.unitPrice = value; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime value) { this.startedAt = value; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime value) { this.endedAt = value; }
    public BigDecimal getPayableAmount() { return payableAmount; }
    public void setPayableAmount(BigDecimal value) { this.payableAmount = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
}
