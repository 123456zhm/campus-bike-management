package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.BikeStation;

public interface IBikeStationService
{
    List<BikeStation> selectList(BikeStation filter);
    BikeStation selectById(Long id);
    int insert(BikeStation station);
    int update(BikeStation station);
    int delete(Long[] ids);
}
