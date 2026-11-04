package com.ruoyi.system.domain.report;

public class StationSummary extends VehicleSummary {
    private Long stationId;
    private String stationName;
    private String stationStatus;
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getStationStatus() { return stationStatus; }
    public void setStationStatus(String stationStatus) { this.stationStatus = stationStatus; }
}
