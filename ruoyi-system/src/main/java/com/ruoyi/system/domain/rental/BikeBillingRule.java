package com.ruoyi.system.domain.rental;
import java.math.BigDecimal;
public class BikeBillingRule {
    private Long id; private Integer unitMinutes; private BigDecimal unitPrice;
    public Long getId(){ return id; } public void setId(Long v){ id=v; }
    public Integer getUnitMinutes(){ return unitMinutes; } public void setUnitMinutes(Integer v){ unitMinutes=v; }
    public BigDecimal getUnitPrice(){ return unitPrice; } public void setUnitPrice(BigDecimal v){ unitPrice=v; }
}
