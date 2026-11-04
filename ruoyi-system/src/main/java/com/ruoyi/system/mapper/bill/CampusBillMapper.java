package com.ruoyi.system.mapper.bill;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.bill.CampusBill;
import com.ruoyi.system.domain.bill.CampusBillFilter;
import com.ruoyi.system.domain.bill.CampusPayment;

public interface CampusBillMapper {
    List<CampusBill> selectBills(@Param("filter") CampusBillFilter filter, @Param("userId") Long userId);
    List<CampusPayment> selectPayments(@Param("userId") Long userId);
    CampusBill lockMyBill(@Param("billId") Long billId, @Param("userId") Long userId);
    CampusPayment selectPaymentByRequestKey(@Param("requestKey") String requestKey);
    int insertPayment(CampusPayment payment);
    int markPaid(@Param("billId") Long billId);
}
