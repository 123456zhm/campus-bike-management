package com.ruoyi.system.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.BikeStation;
import com.ruoyi.system.mapper.BikeStationMapper;
import com.ruoyi.system.service.IBikeStationService;

@Service
public class BikeStationServiceImpl implements IBikeStationService
{
    private final BikeStationMapper mapper;
    public BikeStationServiceImpl(BikeStationMapper mapper) { this.mapper = mapper; }

    @Override
    public List<BikeStation> selectList(BikeStation filter) { return mapper.selectList(filter); }
    @Override
    public BikeStation selectById(Long id) { return mapper.selectById(id); }

    private void normalize(BikeStation station)
    {
        station.setStationNo(station.getStationNo().trim());
        station.setName(station.getName().trim());
        station.setLocationText(station.getLocationText().trim());
    }

    @Override
    public int insert(BikeStation station)
    {
        normalize(station);
        if (mapper.countByStationNo(station.getStationNo(), null) > 0) throw new ServiceException("站点编号已存在");
        try { return mapper.insert(station); }
        catch (DuplicateKeyException ex) { throw new ServiceException("站点编号已存在"); }
    }

    @Override
    public int update(BikeStation station)
    {
        if (station.getId() == null) throw new ServiceException("站点ID不能为空");
        normalize(station);
        if (mapper.countByStationNo(station.getStationNo(), station.getId()) > 0) throw new ServiceException("站点编号已存在");
        try { return mapper.update(station); }
        catch (DuplicateKeyException ex) { throw new ServiceException("站点编号已存在"); }
    }

    /** 锁定父站点行；外键约束为并发新增车辆或订单提供最终保护。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long[] ids)
    {
        if (ids == null || ids.length == 0) throw new ServiceException("请选择站点");
        Long[] sorted = Arrays.stream(ids).distinct().sorted().toArray(Long[]::new);
        int count = 0;
        for (Long id : sorted)
        {
            BikeStation station = mapper.selectForUpdate(id);
            if (station == null) throw new ServiceException("站点不存在或已删除");
            if (mapper.countVehicles(id) > 0 || mapper.countOrders(id) > 0)
                throw new ServiceException("站点“" + station.getName() + "”存在关联车辆或历史订单，请停用而不要删除");
            count += mapper.delete(id);
        }
        return count;
    }
}
