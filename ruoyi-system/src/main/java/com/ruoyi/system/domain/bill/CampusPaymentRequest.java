package com.ruoyi.system.domain.bill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CampusPaymentRequest {
    @NotBlank(message="支付请求标识不能为空")
    @Pattern(regexp="^[A-Za-z0-9_-]{16,64}$", message="支付请求标识格式无效")
    private String requestKey;
    public String getRequestKey() { return requestKey; }
    public void setRequestKey(String requestKey) { this.requestKey = requestKey; }
}
