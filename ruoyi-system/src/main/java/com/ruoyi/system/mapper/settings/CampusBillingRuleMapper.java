package com.ruoyi.system.mapper.settings;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.ruoyi.system.domain.settings.CampusBillingRule;
public interface CampusBillingRuleMapper {
 String COLUMNS="id,rule_name AS ruleName,unit_minutes AS unitMinutes,unit_price AS unitPrice,is_active AS isActive,created_by AS createdBy,created_at AS createdAt";
 @Select("SELECT "+COLUMNS+" FROM bike_billing_rule WHERE is_active=1") CampusBillingRule current();
 @Select("SELECT id FROM bike_billing_rule WHERE is_active=1 FOR UPDATE") Long lockCurrent();
 @Select("SELECT "+COLUMNS+" FROM bike_billing_rule ORDER BY id DESC") List<CampusBillingRule> list();
 @Update("UPDATE bike_billing_rule SET is_active=0 WHERE is_active=1") int deactivate();
 @Insert("INSERT INTO bike_billing_rule(rule_name,unit_minutes,unit_price,is_active,created_by) VALUES(#{ruleName},#{unitMinutes},#{unitPrice},1,#{createdBy})")
 @Options(useGeneratedKeys=true,keyProperty="id") int insert(CampusBillingRule rule);
}
