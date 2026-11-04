package com.ruoyi.system.domain.report;

import java.math.BigDecimal;
public class BillSummary {
    private long billCount;
    private BigDecimal receivable;
    private BigDecimal received;
    private BigDecimal outstanding;
    private BigDecimal cashReceived;
    public long getBillCount() { return billCount; }
    public void setBillCount(long billCount) { this.billCount = billCount; }
    public BigDecimal getReceivable() { return receivable; }
    public void setReceivable(BigDecimal receivable) { this.receivable = receivable; }
    public BigDecimal getReceived() { return received; }
    public void setReceived(BigDecimal received) { this.received = received; }
    public BigDecimal getOutstanding() { return outstanding; }
    public void setOutstanding(BigDecimal outstanding) { this.outstanding = outstanding; }
    public BigDecimal getCashReceived() { return cashReceived; }
    public void setCashReceived(BigDecimal cashReceived) { this.cashReceived = cashReceived; }
}
