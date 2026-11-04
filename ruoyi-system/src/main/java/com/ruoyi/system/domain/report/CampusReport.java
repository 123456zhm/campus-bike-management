package com.ruoyi.system.domain.report;

import java.util.List;
public class CampusReport {
    private String startDate;
    private String endDate;
    private VehicleSummary vehicles;
    private List<StationSummary> stations;
    private OrderSummary orders;
    private BillSummary bills;
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public VehicleSummary getVehicles() { return vehicles; }
    public void setVehicles(VehicleSummary vehicles) { this.vehicles = vehicles; }
    public List<StationSummary> getStations() { return stations; }
    public void setStations(List<StationSummary> stations) { this.stations = stations; }
    public OrderSummary getOrders() { return orders; }
    public void setOrders(OrderSummary orders) { this.orders = orders; }
    public BillSummary getBills() { return bills; }
    public void setBills(BillSummary bills) { this.bills = bills; }
}
