package com.ruoyi.system.service.bike;

import java.util.List;
import com.ruoyi.system.domain.bike.BikeVehicle;
import com.ruoyi.system.domain.bike.BikeVehiclePublic;
import com.ruoyi.system.domain.bike.BikeVehicleStationOption;

public interface IBikeVehicleService {
    List<BikeVehicle> list(BikeVehicle filter);
    BikeVehicle get(Long id);
    List<BikeVehiclePublic> available(String vehicleNo, Long stationId);
    List<BikeVehicleStationOption> stations();
    int add(BikeVehicle vehicle);
    int edit(BikeVehicle vehicle);
}
