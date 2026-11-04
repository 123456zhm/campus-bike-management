package com.ruoyi.web.controller.campus;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.bill.CampusBillFilter;
import com.ruoyi.system.domain.bill.CampusPaymentRequest;
import com.ruoyi.system.service.bill.ICampusBillService;

@RestController
@RequestMapping("/campus/bill")
public class CampusBillController extends BaseController {
    private final ICampusBillService bills;
    public CampusBillController(ICampusBillService bills) { this.bills = bills; }
    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:bill:list')")
    @GetMapping("/list")
    public TableDataInfo list(CampusBillFilter filter) {
        startPage(); return getDataTable(bills.allBills(filter));
    }
    @PreAuthorize("@ss.hasPermi('campus:bill:mine')")
    @GetMapping("/mine")
    public TableDataInfo mine(CampusBillFilter filter) {
        startPage(); return getDataTable(bills.myBills(getUserId(), filter));
    }
    @PreAuthorize("@ss.hasPermi('campus:bill:mine')")
    @GetMapping("/mine/records")
    public TableDataInfo records() {
        startPage(); return getDataTable(bills.myPayments(getUserId()));
    }
    @PreAuthorize("@ss.hasPermi('campus:bill:mine')")
    @Log(title="模拟支付", businessType=BusinessType.UPDATE)
    @PostMapping("/mine/{billId}/pay")
    public AjaxResult pay(@PathVariable Long billId, @Valid @RequestBody CampusPaymentRequest request) {
        return success(bills.pay(getUserId(), billId, request.getRequestKey()));
    }
}
