package com.ruoyi.system.mapper.bike;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.bike.BikeVehicle;
import com.ruoyi.system.domain.bike.BikeVehiclePublic;
import com.ruoyi.system.domain.bike.BikeVehicleStationOption;

public interface BikeVehicleMapper {
    List<BikeVehicle> selectList(BikeVehicle filter);
    BikeVehicle selectById(Long id);
    BikeVehicle lockById(Long id);
    List<BikeVehiclePublic> selectAvailable(@Param("vehicleNo") String vehicleNo, @Param("stationId") Long stationId);
    List<BikeVehicleStationOption> selectEnabledStations();
    String lockStationStatus(Long stationId);
    int countActiveOrders(Long vehicleId);
    int countOpenRepairs(Long vehicleId);
    int insert(BikeVehicle vehicle);
    int update(BikeVehicle vehicle);
}
