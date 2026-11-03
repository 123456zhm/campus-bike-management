package com.ruoyi.system.service.bike.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.bike.BikeVehicle;
import com.ruoyi.system.domain.bike.BikeVehiclePublic;
import com.ruoyi.system.domain.bike.BikeVehicleStationOption;
import com.ruoyi.system.mapper.bike.BikeVehicleMapper;
import com.ruoyi.system.service.bike.IBikeVehicleService;

@Service
public class BikeVehicleServiceImpl implements IBikeVehicleService {
    private static final Set<String> MANUAL_STATES = new HashSet<>(Arrays.asList("idle", "repair", "scrapped"));
    private final BikeVehicleMapper mapper;
    public BikeVehicleServiceImpl(BikeVehicleMapper mapper) { this.mapper = mapper; }
    @Override public List<BikeVehicle> list(BikeVehicle filter) { return mapper.selectList(filter); }
    @Override public BikeVehicle get(Long id) {
        BikeVehicle vehicle = mapper.selectById(id);
        if (vehicle == null) throw new ServiceException("车辆不存在");
        return vehicle;
    }
    @Override public List<BikeVehiclePublic> available(String vehicleNo, Long stationId) {
        return mapper.selectAvailable(vehicleNo, stationId);
    }
    @Override public List<BikeVehicleStationOption> stations() { return mapper.selectEnabledStations(); }

    private void validateManualState(BikeVehicle vehicle) {
        if (!MANUAL_STATES.contains(vehicle.getStatus())) throw new ServiceException("车辆状态无效；借出状态由租借业务处理");
        vehicle.setVehicleNo(vehicle.getVehicleNo().trim());
    }
    private void lockEnabledStation(Long stationId) {
        String status = mapper.lockStationStatus(stationId);
        if (status == null) throw new ServiceException("站点不存在");
        if (!"enabled".equals(status)) throw new ServiceException("该站点已停用，请选择启用的站点");
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public int add(BikeVehicle vehicle) {
        validateManualState(vehicle);
        lockEnabledStation(vehicle.getStationId());
        try { return mapper.insert(vehicle); }
        catch (DuplicateKeyException ex) { throw new ServiceException("车辆编号已存在"); }
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public int edit(BikeVehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId() <= 0) throw new ServiceException("车辆编号无效");
        validateManualState(vehicle);
        BikeVehicle old = mapper.lockById(vehicle.getId());
        if (old == null) throw new ServiceException("车辆不存在");
        if ("rented".equals(old.getStatus()) || mapper.countActiveOrders(vehicle.getId()) > 0)
            throw new ServiceException("车辆正在借用或有未结束订单，不能手动修改");
        if ("scrapped".equals(old.getStatus()) && !"scrapped".equals(vehicle.getStatus()))
            throw new ServiceException("报废车辆不能恢复使用");
        if ("idle".equals(vehicle.getStatus()) && mapper.countOpenRepairs(vehicle.getId()) > 0)
            throw new ServiceException("车辆存在未完成的报修工单，不能设为空闲");
        lockEnabledStation(vehicle.getStationId());
        try { return mapper.update(vehicle); }
        catch (DuplicateKeyException ex) { throw new ServiceException("车辆编号已存在"); }
    }
}
