package com.ruoyi.web.controller.bike;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ruoyi.system.domain.BikeStation;
import com.ruoyi.system.service.IBikeStationService;

@RestController
@RequestMapping("/bike/station")
public class BikeStationController extends BaseController
{
    private final IBikeStationService service;
    public BikeStationController(IBikeStationService service) { this.service = service; }

    @PreAuthorize("@ss.hasPermi('bike:station:list')")
    @GetMapping("/list")
    public TableDataInfo list(BikeStation filter)
    {
        startPage();
        return getDataTable(service.selectList(filter));
    }

    /** 登录用户仅能浏览启用站点。 */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/browse")
    public TableDataInfo browse(BikeStation filter)
    {
        filter.setStatus("enabled");
        startPage();
        return getDataTable(service.selectList(filter));
    }

    @PreAuthorize("@ss.hasAnyPermi('bike:station:query,bike:station:edit')")
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id)
    {
        BikeStation station = service.selectById(id);
        return station == null ? error("站点不存在") : success(station);
    }

    @PreAuthorize("@ss.hasPermi('bike:station:add')")
    @Log(title="站点管理", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BikeStation station) { return toAjax(service.insert(station)); }

    @PreAuthorize("@ss.hasPermi('bike:station:edit')")
    @Log(title="站点管理", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BikeStation station) { return toAjax(service.update(station)); }

    @PreAuthorize("@ss.hasPermi('bike:station:remove')")
    @Log(title="站点管理", businessType=BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) { return toAjax(service.delete(ids)); }
}
