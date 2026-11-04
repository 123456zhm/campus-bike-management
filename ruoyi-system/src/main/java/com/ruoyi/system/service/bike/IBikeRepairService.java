package com.ruoyi.system.service.bike;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.bike.BikeRepairOrder;

public interface IBikeRepairService {
    List<BikeRepairOrder> list(BikeRepairOrder filter, Long ownerId);
    Map<String,Object> detail(Long id, Long ownerId);
    BikeRepairOrder submit(String vehicleNo, String description, Long reporterId);
    int handle(Long id, String targetStatus, String note, Long operatorId);
}
