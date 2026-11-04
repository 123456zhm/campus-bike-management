package com.ruoyi.system.domain.bike;

import java.io.Serializable;
import java.util.Date;

public class BikeRepairOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id, vehicleId, reporterId;
    private String repairNo, vehicleNo, reporterName, description, status;
    private Date createdAt, updatedAt, resolvedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
    public String getRepairNo() { return repairNo; }
    public void setRepairNo(String repairNo) { this.repairNo = repairNo; }
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Date getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Date resolvedAt) { this.resolvedAt = resolvedAt; }
}
