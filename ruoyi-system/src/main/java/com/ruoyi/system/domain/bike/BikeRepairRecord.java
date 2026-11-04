package com.ruoyi.system.domain.bike;

import java.io.Serializable;
import java.util.Date;

public class BikeRepairRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id, repairId, operatorId;
    private String operatorName, fromStatus, toStatus, note;
    private Date createdAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRepairId() { return repairId; }
    public void setRepairId(Long repairId) { this.repairId = repairId; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
