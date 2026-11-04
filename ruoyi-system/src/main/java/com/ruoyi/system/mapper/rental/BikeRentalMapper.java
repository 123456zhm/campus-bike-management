package com.ruoyi.system.mapper.rental;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.rental.BikeBillingRule;
import com.ruoyi.system.domain.rental.BikeRentalOrder;

public interface BikeRentalMapper
{
    String COLUMNS = "o.id,o.order_no AS orderNo,o.user_id AS userId,o.vehicle_id AS vehicleId,"
            + "v.vehicle_no AS vehicleNo,o.start_station_id AS startStationId,s.name AS startStationName,"
            + "o.end_station_id AS endStationId,e.name AS endStationName,o.billing_rule_id AS billingRuleId,"
            + "o.unit_minutes AS unitMinutes,o.unit_price AS unitPrice,o.started_at AS startedAt,"
            + "o.ended_at AS endedAt,o.payable_amount AS payableAmount,o.status";
    String JOINED = " FROM bike_rental_order o JOIN bike_vehicle v ON v.id=o.vehicle_id "
            + "JOIN bike_station s ON s.id=o.start_station_id LEFT JOIN bike_station e ON e.id=o.end_station_id ";

    @Select("SELECT user_id AS userId,status,del_flag AS delFlag FROM sys_user WHERE user_id=#{userId} FOR UPDATE")
    SysUser lockAccount(Long userId);
    @Select("SELECT blacklisted FROM bike_user_profile WHERE user_id=#{userId} FOR UPDATE")
    Integer lockBlacklist(Long userId);
    @Select("SELECT COUNT(*) FROM bike_bill b JOIN bike_rental_order o ON o.id=b.order_id WHERE o.user_id=#{userId} AND b.status='unpaid'")
    int countUnpaid(Long userId);
    @Select("SELECT COUNT(*) FROM bike_rental_order WHERE user_id=#{userId} AND status IN ('ongoing','exception')")
    int countActiveOrders(Long userId);
    @Select("SELECT COUNT(*) FROM bike_repair_order WHERE vehicle_id=#{vehicleId} AND status IN ('pending','processing') FOR UPDATE")
    int countOpenRepairsForUpdate(Long vehicleId);
    @Select("SELECT id,unit_minutes AS unitMinutes,unit_price AS unitPrice FROM bike_billing_rule WHERE is_active=1")
    BikeBillingRule activeRule();
    @Insert("INSERT INTO bike_rental_order(order_no,user_id,vehicle_id,start_station_id,billing_rule_id,unit_minutes,unit_price,started_at,status) "
            + "VALUES(#{orderNo},#{userId},#{vehicleId},#{startStationId},#{billingRuleId},#{unitMinutes},#{unitPrice},#{startedAt},'ongoing')")
    @Options(useGeneratedKeys=true,keyProperty="id")
    int insertOrder(BikeRentalOrder order);
    @Select("SELECT " + COLUMNS + JOINED + "WHERE o.id=#{id}")
    BikeRentalOrder find(Long id);
    @Select("SELECT id,order_no AS orderNo,user_id AS userId,vehicle_id AS vehicleId,unit_minutes AS unitMinutes,"
            + "unit_price AS unitPrice,started_at AS startedAt,status FROM bike_rental_order WHERE id=#{id} FOR UPDATE")
    BikeRentalOrder lockOrder(Long id);
    @Select("SELECT " + COLUMNS + JOINED + "WHERE o.user_id=#{userId} ORDER BY o.id DESC")
    List<BikeRentalOrder> listMine(Long userId);
    @Select("SELECT " + COLUMNS + JOINED + "ORDER BY o.id DESC")
    List<BikeRentalOrder> listAll();
    @Update("UPDATE bike_vehicle SET status=#{status},station_id=#{stationId} WHERE id=#{id}")
    int updateVehicle(@Param("id") Long id,@Param("stationId") Long stationId,@Param("status") String status);
    @Update("UPDATE bike_rental_order SET status='completed',ended_at=#{endTime},end_station_id=#{stationId},payable_amount=#{amount} WHERE id=#{id} AND status IN ('ongoing','exception')")
    int finishOrder(@Param("id") Long id,@Param("endTime") LocalDateTime endTime,@Param("stationId") Long stationId,@Param("amount") BigDecimal amount);
    @Update("UPDATE bike_rental_order SET status='exception' WHERE id=#{id} AND status='ongoing'")
    int markException(Long id);
    @Insert("INSERT INTO bike_order_operation_log(order_id,operator_id,operation,reason) VALUES(#{orderId},#{operatorId},#{operation},#{reason})")
    int logOperation(@Param("orderId") Long orderId,@Param("operatorId") Long operatorId,
                     @Param("operation") String operation,@Param("reason") String reason);
    @Insert("INSERT INTO bike_bill(bill_no,order_id,payable_amount,paid_amount,status) VALUES(#{billNo},#{orderId},#{amount},0,#{status})")
    int insertBill(@Param("billNo") String billNo,@Param("orderId") Long orderId,@Param("amount") BigDecimal amount,@Param("status") String status);
}
