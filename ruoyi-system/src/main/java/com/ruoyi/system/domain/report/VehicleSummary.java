package com.ruoyi.system.domain.report;

public class VehicleSummary {
    private long total;
    private long idle;
    private long rented;
    private long repair;
    private long scrapped;
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public long getIdle() { return idle; }
    public void setIdle(long idle) { this.idle = idle; }
    public long getRented() { return rented; }
    public void setRented(long rented) { this.rented = rented; }
    public long getRepair() { return repair; }
    public void setRepair(long repair) { this.repair = repair; }
    public long getScrapped() { return scrapped; }
    public void setScrapped(long scrapped) { this.scrapped = scrapped; }
}
