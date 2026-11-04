package com.ruoyi.system.service.bike.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.bike.BikeRepairOrder;
import com.ruoyi.system.domain.bike.BikeRepairRecord;
import com.ruoyi.system.domain.bike.BikeVehicle;
import com.ruoyi.system.mapper.bike.BikeRepairMapper;
import com.ruoyi.system.service.bike.IBikeRepairService;

@Service
public class BikeRepairServiceImpl implements IBikeRepairService {
    private final BikeRepairMapper mapper;
    public BikeRepairServiceImpl(BikeRepairMapper mapper) { this.mapper = mapper; }
    @Override public List<BikeRepairOrder> list(BikeRepairOrder filter, Long ownerId) { return mapper.selectList(filter, ownerId); }
    @Override public Map<String,Object> detail(Long id, Long ownerId) {
        BikeRepairOrder order = mapper.selectById(id, ownerId);
        if (order == null) throw new ServiceException("工单不存在或无权查看");
        Map<String,Object> result = new HashMap<>();
        result.put("order", order);
        List<BikeRepairRecord> records = mapper.selectRecords(id);
        if (ownerId != null) {
            // 普通用户只需看到进度及说明，不暴露处理人的内部账号信息。
            for (BikeRepairRecord record : records) { record.setOperatorId(null); record.setOperatorName(null); }
        }
        result.put("records", records);
        return result;
    }
    private void record(Long repairId, Long operatorId, String from, String to, String note) {
        BikeRepairRecord record = new BikeRepairRecord();
        record.setRepairId(repairId); record.setOperatorId(operatorId);
        record.setFromStatus(from); record.setToStatus(to); record.setNote(note);
        mapper.insertRecord(record);
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public BikeRepairOrder submit(String vehicleNo, String description, Long reporterId) {
        BikeVehicle vehicle = mapper.lockVehicleByNo(vehicleNo.trim());
        if (vehicle == null) throw new ServiceException("车辆编号不存在");
        if ("scrapped".equals(vehicle.getStatus())) throw new ServiceException("报废车辆不能提交维修工单");
        Long activeRental = mapper.selectActiveRentalForUpdate(vehicle.getId());
        if (activeRental != null && !"rented".equals(vehicle.getStatus()))
            throw new ServiceException("车辆状态与租借订单不一致，请联系管理员处理");
        BikeRepairOrder order = new BikeRepairOrder();
        order.setRepairNo("RP" + UUID.randomUUID().toString().replace("-", ""));
        order.setVehicleId(vehicle.getId()); order.setReporterId(reporterId);
        order.setDescription(description.trim()); order.setStatus("pending");
        mapper.insertOrder(order);
        record(order.getId(), reporterId, "new", "pending", "提交报修");
        if ("idle".equals(vehicle.getStatus())) mapper.updateVehicleStatus(vehicle.getId(), "repair");
        return order;
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public int handle(Long id, String targetStatus, String note, Long operatorId) {
        // 初次查询只读取不可变的 vehicle_id；状态和权限校验均以锁定后的记录为准。
        BikeRepairOrder initial = mapper.selectById(id, null);
        if (initial == null) throw new ServiceException("工单不存在");
        BikeVehicle vehicle = mapper.lockVehicleById(initial.getVehicleId());
        if (vehicle == null) throw new ServiceException("关联车辆不存在");
        BikeRepairOrder current = mapper.lockOrder(id);
        if (current == null || !initial.getVehicleId().equals(current.getVehicleId())) throw new ServiceException("工单已变更，请刷新后重试");
        String before = current.getStatus();
        boolean valid = ("pending".equals(before) && ("processing".equals(targetStatus) || "completed".equals(targetStatus)))
            || ("processing".equals(before) && "completed".equals(targetStatus));
        if (!valid) throw new ServiceException("工单状态已变化或处理步骤无效，请刷新后重试");
        int changed = mapper.updateOrder(id, targetStatus);
        record(id, operatorId, before, targetStatus, note.trim());
        if ("completed".equals(targetStatus)) {
            Long anotherRepair = mapper.selectOpenRepairForUpdate(vehicle.getId());
            Long activeRental = mapper.selectActiveRentalForUpdate(vehicle.getId());
            if (anotherRepair == null && activeRental == null && "repair".equals(vehicle.getStatus()))
                mapper.updateVehicleStatus(vehicle.getId(), "idle");
        }
        return changed;
    }
}
