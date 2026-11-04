package com.ruoyi.system.mapper.bike;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.bike.BikeRepairOrder;
import com.ruoyi.system.domain.bike.BikeRepairRecord;
import com.ruoyi.system.domain.bike.BikeVehicle;

public interface BikeRepairMapper {
    List<BikeRepairOrder> selectList(@Param("filter") BikeRepairOrder filter, @Param("ownerId") Long ownerId);
    BikeRepairOrder selectById(@Param("id") Long id, @Param("ownerId") Long ownerId);
    BikeRepairOrder lockOrder(Long id);
    BikeVehicle lockVehicleById(Long id);
    BikeVehicle lockVehicleByNo(String vehicleNo);
    Long selectOpenRepairForUpdate(Long vehicleId);
    Long selectActiveRentalForUpdate(Long vehicleId);
    int insertOrder(BikeRepairOrder order);
    int updateOrder(@Param("id") Long id, @Param("status") String status);
    int updateVehicleStatus(@Param("id") Long id, @Param("status") String status);
    int insertRecord(BikeRepairRecord record);
    List<BikeRepairRecord> selectRecords(Long repairId);
}
