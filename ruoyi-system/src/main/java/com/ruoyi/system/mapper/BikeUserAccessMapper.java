package com.ruoyi.system.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/** Account restrictions shared by login, existing sessions and campus operations. */
public interface BikeUserAccessMapper
{
    @Select("SELECT COUNT(*) FROM sys_user u LEFT JOIN bike_user_profile p ON p.user_id = u.user_id "
            + "WHERE u.user_id = #{userId} AND u.status = '0' AND u.del_flag = '0' "
            + "AND COALESCE(p.blacklisted, 0) = 0")
    int countAllowed(Long userId);

    @Select("SELECT COALESCE(p.blacklisted, 0) AS blacklisted, p.blacklist_reason AS reason "
            + "FROM sys_user u LEFT JOIN bike_user_profile p ON p.user_id = u.user_id "
            + "WHERE u.user_id = #{userId} AND u.del_flag = '0'")
    Map<String, Object> selectBlacklist(Long userId);

    @Insert("INSERT INTO bike_user_profile (user_id, blacklisted, blacklist_reason) "
            + "VALUES (#{userId}, #{blacklisted}, #{reason}) "
            + "ON DUPLICATE KEY UPDATE blacklisted = VALUES(blacklisted), blacklist_reason = VALUES(blacklist_reason)")
    int saveBlacklist(@Param("userId") Long userId, @Param("blacklisted") int blacklisted,
                      @Param("reason") String reason);
}
