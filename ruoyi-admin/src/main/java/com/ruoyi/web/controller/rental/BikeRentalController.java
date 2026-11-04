package com.ruoyi.web.controller.rental;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.rental.BikeRentalOrder;
import com.ruoyi.system.service.rental.IBikeRentalService;

@RestController
@RequestMapping("/campus/rental")
public class BikeRentalController extends BaseController
{
    private final IBikeRentalService service;
    public BikeRentalController(IBikeRentalService service) { this.service=service; }

    @PreAuthorize("@ss.hasPermi('campus:rental:mine')")
    @GetMapping("/mine")
    public TableDataInfo mine() { startPage(); return getDataTable(service.listMine(getUserId())); }

    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:rental:list')")
    @GetMapping("/list")
    public TableDataInfo list() { startPage(); return getDataTable(service.listAll()); }

    @PreAuthorize("@ss.hasPermi('campus:rental:borrow')")
    @Log(title="借用车辆",businessType=BusinessType.INSERT)
    @PostMapping("/borrow")
    public AjaxResult borrow(@RequestBody BorrowRequest request) { return success(service.borrow(getUserId(),request.getVehicleId())); }

    @PreAuthorize("@ss.hasPermi('campus:rental:return')")
    @Log(title="归还车辆",businessType=BusinessType.UPDATE)
    @PostMapping("/return")
    public AjaxResult returnVehicle(@RequestBody ReturnRequest request) { return success(service.returnVehicle(getUserId(),request.getOrderId(),request.getStationId())); }

    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:rental:manage')")
    @Log(title="标记异常订单",businessType=BusinessType.UPDATE)
    @PostMapping("/exception")
    public AjaxResult markException(@RequestBody ExceptionRequest request)
    { service.markException(getUserId(),request.getOrderId(),request.getReason()); return success(); }

    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:rental:manage')")
    @Log(title="结算异常订单",businessType=BusinessType.UPDATE)
    @PostMapping("/exception/resolve")
    public AjaxResult resolveException(@RequestBody ExceptionRequest request)
    { return success(service.resolveException(getUserId(),request.getOrderId(),request.getStationId(),request.getReason())); }

    public static class BorrowRequest {
        private Long vehicleId;
        public Long getVehicleId(){ return vehicleId; } public void setVehicleId(Long v){ vehicleId=v; }
    }
    public static class ReturnRequest {
        private Long orderId,stationId;
        public Long getOrderId(){ return orderId; } public void setOrderId(Long v){ orderId=v; }
        public Long getStationId(){ return stationId; } public void setStationId(Long v){ stationId=v; }
    }
    public static class ExceptionRequest extends ReturnRequest {
        private String reason;
        public String getReason(){ return reason; } public void setReason(String v){ reason=v; }
    }
}
