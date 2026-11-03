package com.ruoyi.system.domain.bike;

/** 普通用户可见信息，不包含管理备注及内部状态。 */
public class BikeVehiclePublic {
    private Long id;
    private String vehicleNo;
    private Long stationId;
    private String stationName;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
}
