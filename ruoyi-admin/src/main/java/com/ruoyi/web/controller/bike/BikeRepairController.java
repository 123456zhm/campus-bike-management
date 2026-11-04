package com.ruoyi.web.controller.bike;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.bike.BikeRepairOrder;
import com.ruoyi.system.service.bike.IBikeRepairService;

@RestController
@RequestMapping("/bike/repair")
public class BikeRepairController extends BaseController {
    private final IBikeRepairService service;
    public BikeRepairController(IBikeRepairService service) { this.service = service; }

    public static class SubmitRequest {
        @NotBlank(message="车辆编号不能为空") @Size(max=32, message="车辆编号不能超过32个字符")
        private String vehicleNo;
        @NotBlank(message="问题描述不能为空") @Size(max=1000, message="问题描述不能超过1000个字符")
        private String description;
        public String getVehicleNo() { return vehicleNo; }
        public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    public static class HandleRequest {
        @NotBlank(message="请选择处理状态") @Pattern(regexp="processing|completed", message="处理状态无效")
        private String status;
        @NotBlank(message="请填写处理说明") @Size(max=1000, message="处理说明不能超过1000个字符")
        private String note;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/list")
    public TableDataInfo myList(BikeRepairOrder filter) {
        startPage();
        return getDataTable(service.list(filter, getUserId()));
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/{id}")
    public AjaxResult myDetail(@PathVariable Long id) { return success(service.detail(id, getUserId())); }
    @PreAuthorize("isAuthenticated()")
    @Log(title="车辆报修",businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult submit(@Validated @RequestBody SubmitRequest request) {
        return success(service.submit(request.getVehicleNo(), request.getDescription(), getUserId()));
    }
    @PreAuthorize("@ss.hasPermi('bike:repair:list')")
    @GetMapping("/list")
    public TableDataInfo list(BikeRepairOrder filter) {
        startPage();
        return getDataTable(service.list(filter, null));
    }
    @PreAuthorize("@ss.hasAnyPermi('bike:repair:query,bike:repair:handle')")
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id) { return success(service.detail(id, null)); }
    @PreAuthorize("@ss.hasPermi('bike:repair:handle')")
    @Log(title="报修处理",businessType=BusinessType.UPDATE)
    @PutMapping("/{id}/handle")
    public AjaxResult handle(@PathVariable Long id, @Validated @RequestBody HandleRequest request) {
        return toAjax(service.handle(id, request.getStatus(), request.getNote(), getUserId()));
    }
}
