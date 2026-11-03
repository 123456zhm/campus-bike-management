package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.BikeStation;

public interface BikeStationMapper
{
    List<BikeStation> selectList(BikeStation filter);
    BikeStation selectById(Long id);
    BikeStation selectForUpdate(Long id);
    Long countByStationNo(@Param("stationNo") String stationNo, @Param("excludeId") Long excludeId);
    long countVehicles(Long id);
    long countOrders(Long id);
    int insert(BikeStation station);
    int update(BikeStation station);
    int delete(Long id);
}
