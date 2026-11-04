package com.ruoyi.system.service.rental.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.bike.BikeVehicle;
import com.ruoyi.system.domain.rental.BikeBillingRule;
import com.ruoyi.system.domain.rental.BikeRentalOrder;
import com.ruoyi.system.mapper.bike.BikeVehicleMapper;
import com.ruoyi.system.mapper.rental.BikeRentalMapper;
import com.ruoyi.system.service.rental.IBikeRentalService;

@Service
public class BikeRentalServiceImpl implements IBikeRentalService
{
    private final BikeRentalMapper rentals;
    private final BikeVehicleMapper vehicles;
    public BikeRentalServiceImpl(BikeRentalMapper rentals, BikeVehicleMapper vehicles) { this.rentals=rentals; this.vehicles=vehicles; }

    private void lockAvailableUser(Long userId)
    {
        if (userId == null) throw new ServiceException("请先登录");
        SysUser user=rentals.lockAccount(userId);
        if (user==null || !"0".equals(user.getStatus()) || !"0".equals(user.getDelFlag()))
            throw new ServiceException("账号不可用，请联系管理员");
        Integer blacklisted=rentals.lockBlacklist(userId);
        if (blacklisted!=null && blacklisted==1) throw new ServiceException("账号已被限制使用");
    }
    private void requireEnabledStation(Long stationId)
    {
        if (stationId==null || stationId<=0 || !"enabled".equals(vehicles.lockStationStatus(stationId)))
            throw new ServiceException("站点不存在或已停用");
    }
    private static String newNumber(String prefix) { return prefix+UUID.randomUUID().toString().replace("-","").toUpperCase(); }
    private static LocalDateTime now() { return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); }

    @Override @Transactional(rollbackFor=Exception.class)
    public BikeRentalOrder borrow(Long userId,Long vehicleId)
    {
        if (vehicleId==null || vehicleId<=0) throw new ServiceException("请选择车辆");
        lockAvailableUser(userId);
        if (rentals.countUnpaid(userId)>0) throw new ServiceException("请先结清待缴账单");
        if (rentals.countActiveOrders(userId)>0) throw new ServiceException("您已有未结束订单");
        BikeVehicle vehicle=vehicles.lockById(vehicleId);
        if (vehicle==null || !"idle".equals(vehicle.getStatus())) throw new ServiceException("车辆不可借用");
        requireEnabledStation(vehicle.getStationId());
        if (vehicles.countActiveOrders(vehicleId)>0 || rentals.countOpenRepairsForUpdate(vehicleId)>0)
            throw new ServiceException("车辆有未结束订单或报修，暂不可借");
        BikeBillingRule rule=rentals.activeRule();
        if (rule==null || rule.getUnitMinutes()==null || rule.getUnitMinutes()<=0 || rule.getUnitPrice()==null || rule.getUnitPrice().signum()<0)
            throw new ServiceException("尚未配置有效收费规则");
        BikeRentalOrder order=new BikeRentalOrder();
        order.setOrderNo(newNumber("R")); order.setUserId(userId); order.setVehicleId(vehicleId);
        order.setStartStationId(vehicle.getStationId()); order.setBillingRuleId(rule.getId());
        order.setUnitMinutes(rule.getUnitMinutes()); order.setUnitPrice(rule.getUnitPrice()); order.setStartedAt(now());
        try {
            rentals.insertOrder(order);
            if (rentals.updateVehicle(vehicleId,vehicle.getStationId(),"rented")!=1) throw new ServiceException("车辆状态更新失败");
        } catch (DuplicateKeyException ex) { throw new ServiceException("车辆或账号已存在未结束订单，请刷新后重试"); }
        return rentals.find(order.getId());
    }

    @Override @Transactional(rollbackFor=Exception.class)
    public BikeRentalOrder returnVehicle(Long userId,Long orderId,Long stationId)
    {
        if (orderId==null || orderId<=0) throw new ServiceException("请选择订单");
        lockAvailableUser(userId);
        return settle(orderId,stationId,userId,"ongoing");
    }

    @Override @Transactional(rollbackFor=Exception.class)
    public void markException(Long operatorId,Long orderId,String reason)
    {
        reason=validateReason(reason);
        BikeRentalOrder snapshot=orderId==null ? null : rentals.find(orderId);
        if (snapshot==null) throw new ServiceException("订单不存在");
        BikeVehicle vehicle=vehicles.lockById(snapshot.getVehicleId());
        BikeRentalOrder order=rentals.lockOrder(orderId);
        if (vehicle==null || order==null || !"ongoing".equals(order.getStatus()))
            throw new ServiceException("只有进行中订单可以标记异常");
        if (rentals.markException(orderId)!=1) throw new ServiceException("订单状态已变化，请刷新");
        rentals.logOperation(orderId,operatorId,"mark_exception",reason);
    }

    @Override @Transactional(rollbackFor=Exception.class)
    public BikeRentalOrder resolveException(Long operatorId,Long orderId,Long stationId,String reason)
    {
        reason=validateReason(reason);
        BikeRentalOrder result=settle(orderId,stationId,null,"exception");
        rentals.logOperation(orderId,operatorId,"resolve_exception",reason);
        return result;
    }

    private static String validateReason(String reason)
    {
        if (reason==null || reason.trim().isEmpty() || reason.trim().length()>1000)
            throw new ServiceException("请填写不超过1000字的处理原因");
        return reason.trim();
    }

    private BikeRentalOrder settle(Long orderId,Long stationId,Long ownerId,String expectedStatus)
    {
        if (orderId==null || orderId<=0) throw new ServiceException("请选择订单");
        BikeRentalOrder snapshot=rentals.find(orderId);
        if (snapshot==null || (ownerId!=null && !ownerId.equals(snapshot.getUserId())))
            throw new ServiceException("订单不存在或无权操作");
        // Lock vehicle before order, matching repair operations. Re-check after acquiring both locks.
        BikeVehicle vehicle=vehicles.lockById(snapshot.getVehicleId());
        BikeRentalOrder order=rentals.lockOrder(orderId);
        if (vehicle==null || order==null || (ownerId!=null && !ownerId.equals(order.getUserId())) || !expectedStatus.equals(order.getStatus()))
            throw new ServiceException("订单不存在、无权操作或已经结束");
        if (!"rented".equals(vehicle.getStatus())) throw new ServiceException("车辆状态异常，请联系管理员");
        requireEnabledStation(stationId);
        LocalDateTime end=now();
        long seconds=Duration.between(order.getStartedAt(),end).getSeconds();
        if (seconds<0) throw new ServiceException("服务器时间异常，请联系管理员");
        BigDecimal secondsPerUnit=BigDecimal.valueOf(order.getUnitMinutes()).multiply(BigDecimal.valueOf(60));
        BigDecimal units=BigDecimal.valueOf(seconds).divide(secondsPerUnit,0,RoundingMode.CEILING).max(BigDecimal.ONE);
        BigDecimal amount=units.multiply(order.getUnitPrice()).setScale(2,RoundingMode.UNNECESSARY);
        if (amount.compareTo(new BigDecimal("9999999999.99"))>0) throw new ServiceException("费用超出允许范围，请联系管理员");
        if (rentals.finishOrder(orderId,end,stationId,amount)!=1) throw new ServiceException("订单已经结束");
        try { rentals.insertBill(newNumber("B"),orderId,amount,amount.signum()==0?"paid":"unpaid"); }
        catch (DuplicateKeyException ex) { throw new ServiceException("账单已存在，请联系管理员核对"); }
        String next=rentals.countOpenRepairsForUpdate(order.getVehicleId())>0?"repair":"idle";
        if (rentals.updateVehicle(order.getVehicleId(),stationId,next)!=1) throw new ServiceException("车辆状态更新失败");
        return rentals.find(orderId);
    }
    @Override public List<BikeRentalOrder> listMine(Long userId) { return rentals.listMine(userId); }
    @Override public List<BikeRentalOrder> listAll() { return rentals.listAll(); }
}
