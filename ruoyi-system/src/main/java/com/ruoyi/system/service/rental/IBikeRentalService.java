package com.ruoyi.system.service.rental;
import java.util.List;
import com.ruoyi.system.domain.rental.BikeRentalOrder;
public interface IBikeRentalService {
    BikeRentalOrder borrow(Long userId,Long vehicleId);
    BikeRentalOrder returnVehicle(Long userId,Long orderId,Long stationId);
    void markException(Long operatorId,Long orderId,String reason);
    BikeRentalOrder resolveException(Long operatorId,Long orderId,Long stationId,String reason);
    List<BikeRentalOrder> listMine(Long userId);
    List<BikeRentalOrder> listAll();
}
