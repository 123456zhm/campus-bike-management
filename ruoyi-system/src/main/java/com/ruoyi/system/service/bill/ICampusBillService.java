package com.ruoyi.system.service.bill;

import java.util.List;
import com.ruoyi.system.domain.bill.CampusBill;
import com.ruoyi.system.domain.bill.CampusBillFilter;
import com.ruoyi.system.domain.bill.CampusPayment;

public interface ICampusBillService {
    List<CampusBill> myBills(Long userId, CampusBillFilter filter);
    List<CampusBill> allBills(CampusBillFilter filter);
    List<CampusPayment> myPayments(Long userId);
    CampusPayment pay(Long userId, Long billId, String requestKey);
}
