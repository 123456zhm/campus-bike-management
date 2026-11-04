package com.ruoyi.system.service.bill.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.bill.CampusBill;
import com.ruoyi.system.domain.bill.CampusBillFilter;
import com.ruoyi.system.domain.bill.CampusPayment;
import com.ruoyi.system.mapper.bill.CampusBillMapper;
import com.ruoyi.system.service.bill.ICampusBillService;

@Service
public class CampusBillServiceImpl implements ICampusBillService {
    private final CampusBillMapper mapper;
    public CampusBillServiceImpl(CampusBillMapper mapper) { this.mapper = mapper; }
    private void requireUser(Long userId) {
        if (userId == null || userId <= 0) throw new ServiceException("用户身份无效");
    }
    @Override public List<CampusBill> myBills(Long userId, CampusBillFilter filter) {
        requireUser(userId); return mapper.selectBills(filter, userId);
    }
    @Override public List<CampusBill> allBills(CampusBillFilter filter) { return mapper.selectBills(filter, null); }
    @Override public List<CampusPayment> myPayments(Long userId) {
        requireUser(userId); return mapper.selectPayments(userId);
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public CampusPayment pay(Long userId, Long billId, String requestKey) {
        requireUser(userId);
        if (billId == null || billId <= 0) throw new ServiceException("账单编号无效");
        if (requestKey == null || !requestKey.matches("^[A-Za-z0-9_-]{16,64}$")) throw new ServiceException("支付请求标识无效");
        // 必须先锁本人账单及关联订单，避免并发写入和越权。
        CampusBill bill = mapper.lockMyBill(billId, userId);
        if (bill == null) throw new ServiceException("账单不存在或无权访问");
        BigDecimal payable = bill.getPayableAmount();
        BigDecimal paid = bill.getPaidAmount();
        if (!"completed".equals(bill.getOrderStatus()) || bill.getOrderAmount() == null || payable == null || paid == null
                || payable.compareTo(bill.getOrderAmount()) != 0 || payable.signum() < 0) {
            throw new ServiceException("账单与订单数据不一致，请联系管理员");
        }
        CampusPayment prior = mapper.selectPaymentByRequestKey(requestKey);
        if (prior != null) {
            if (!billId.equals(prior.getBillId())) throw new ServiceException("支付请求已被使用，请重新发起");
            if (!"paid".equals(bill.getStatus()) || paid.compareTo(payable) != 0
                    || prior.getAmount() == null || prior.getAmount().compareTo(payable) != 0)
                throw new ServiceException("账单和支付记录不一致，请联系管理员");
            return prior;
        }
        if ("paid".equals(bill.getStatus())) throw new ServiceException("该账单已结清");
        if (!"unpaid".equals(bill.getStatus()) || paid.signum() != 0 || payable.signum() <= 0)
            throw new ServiceException("账单状态或金额异常，不能支付");
        CampusPayment payment = new CampusPayment();
        payment.setPaymentNo("P" + UUID.randomUUID().toString().replace("-", ""));
        payment.setRequestKey(requestKey);
        payment.setBillId(billId);
        payment.setAmount(payable);
        try {
            if (mapper.insertPayment(payment) != 1 || mapper.markPaid(billId) != 1)
                throw new ServiceException("支付未完成，请刷新后重试");
        } catch (DuplicateKeyException ex) {
            throw new ServiceException("支付请求已使用或账单已结清，请刷新后重试");
        }
        return mapper.selectPaymentByRequestKey(requestKey);
    }
}
