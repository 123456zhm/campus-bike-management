package com.ruoyi.system.service.settings;
import java.util.List;
import com.ruoyi.system.domain.settings.CampusBillingRule;
public interface ICampusBillingRuleService { CampusBillingRule current(); List<CampusBillingRule> list(); CampusBillingRule activate(CampusBillingRule rule,Long userId); }
