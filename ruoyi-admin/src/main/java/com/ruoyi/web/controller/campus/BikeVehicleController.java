package com.ruoyi.web.controller.campus;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.bike.BikeVehicle;
import com.ruoyi.system.domain.bike.BikeVehiclePublic;
import com.ruoyi.system.service.bike.IBikeVehicleService;

@RestController
@RequestMapping("/campus/vehicle")
public class BikeVehicleController extends BaseController {
    private final IBikeVehicleService vehicles;
    public BikeVehicleController(IBikeVehicleService vehicles) { this.vehicles = vehicles; }
    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:vehicle:list')")
    @GetMapping("/list")
    public TableDataInfo list(BikeVehicle filter) {
        startPage();
        return getDataTable(vehicles.list(filter));
    }
    @PreAuthorize("@ss.hasPermi('campus:vehicle:browse')")
    @GetMapping("/available")
    public TableDataInfo available(@RequestParam(required=false) String vehicleNo, @RequestParam(required=false) Long stationId) {
        startPage();
        List<BikeVehiclePublic> list = vehicles.available(vehicleNo, stationId);
        return getDataTable(list);
    }
    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:vehicle:list')")
    @GetMapping("/stations")
    public AjaxResult stations() { return success(vehicles.stations()); }
    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:vehicle:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable Long id) { return success(vehicles.get(id)); }
    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:vehicle:add')")
    @Log(title="车辆管理", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody BikeVehicle vehicle) { return toAjax(vehicles.add(vehicle)); }
    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:vehicle:edit')")
    @Log(title="车辆管理", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody BikeVehicle vehicle) { return toAjax(vehicles.edit(vehicle)); }
}
