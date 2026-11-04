package com.ruoyi.system.domain.settings;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class CampusBillingRule {
 private Long id,createdBy; private String ruleName; private Integer unitMinutes,isActive; private BigDecimal unitPrice; private LocalDateTime createdAt;
 public Long getId(){return id;} public void setId(Long v){id=v;} public Long getCreatedBy(){return createdBy;} public void setCreatedBy(Long v){createdBy=v;}
 public String getRuleName(){return ruleName;} public void setRuleName(String v){ruleName=v;} public Integer getUnitMinutes(){return unitMinutes;} public void setUnitMinutes(Integer v){unitMinutes=v;}
 public Integer getIsActive(){return isActive;} public void setIsActive(Integer v){isActive=v;} public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
