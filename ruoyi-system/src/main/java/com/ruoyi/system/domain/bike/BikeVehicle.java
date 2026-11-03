package com.ruoyi.system.domain.bike;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** 车辆管理资料；状态受服务层业务规则约束。 */
public class BikeVehicle {
    private Long id;
    @NotBlank(message = "车辆编号不能为空")
    @Size(max = 32, message = "车辆编号不能超过32个字符")
    private String vehicleNo;
    @NotNull(message = "所属站点不能为空")
    @Positive(message = "站点编号无效")
    private Long stationId;
    @NotBlank(message = "车辆状态不能为空")
    private String status;
    @Size(max = 255, message = "备注不能超过255个字符")
    private String description;
    private String stationName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
