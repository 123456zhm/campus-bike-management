package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 站点及按当前车辆归属计算的状态数量。 */
public class BikeStation implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Long id;
    @NotBlank(message = "站点编号不能为空")
    @Size(max = 32, message = "站点编号不能超过32个字符")
    private String stationNo;
    @NotBlank(message = "站点名称不能为空")
    @Size(max = 100, message = "站点名称不能超过100个字符")
    private String name;
    @NotBlank(message = "站点位置不能为空")
    @Size(max = 255, message = "站点位置不能超过255个字符")
    private String locationText;
    @NotBlank(message = "站点状态不能为空")
    @Pattern(regexp = "enabled|disabled", message = "站点状态不正确")
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Long totalCount;
    private Long idleCount;
    private Long availableCount;
    private Long rentedCount;
    private Long repairCount;
    private Long scrappedCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStationNo() { return stationNo; }
    public void setStationNo(String stationNo) { this.stationNo = stationNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocationText() { return locationText; }
    public void setLocationText(String locationText) { this.locationText = locationText; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Long getTotalCount() { return totalCount; }
    public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
    public Long getIdleCount() { return idleCount; }
    public void setIdleCount(Long idleCount) { this.idleCount = idleCount; }
    public Long getAvailableCount() { return availableCount; }
    public void setAvailableCount(Long availableCount) { this.availableCount = availableCount; }
    public Long getRentedCount() { return rentedCount; }
    public void setRentedCount(Long rentedCount) { this.rentedCount = rentedCount; }
    public Long getRepairCount() { return repairCount; }
    public void setRepairCount(Long repairCount) { this.repairCount = repairCount; }
    public Long getScrappedCount() { return scrappedCount; }
    public void setScrappedCount(Long scrappedCount) { this.scrappedCount = scrappedCount; }
}
