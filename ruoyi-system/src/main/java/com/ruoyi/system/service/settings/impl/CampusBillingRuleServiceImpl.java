package com.ruoyi.system.service.settings.impl;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.settings.CampusBillingRule;
import com.ruoyi.system.mapper.settings.CampusBillingRuleMapper;
import com.ruoyi.system.service.settings.ICampusBillingRuleService;
@Service
public class CampusBillingRuleServiceImpl implements ICampusBillingRuleService {
 private final CampusBillingRuleMapper mapper;
 public CampusBillingRuleServiceImpl(CampusBillingRuleMapper mapper){this.mapper=mapper;}
 @Override public CampusBillingRule current(){return mapper.current();}
 @Override public List<CampusBillingRule> list(){return mapper.list();}
 @Override @Transactional(rollbackFor=Exception.class)
 public CampusBillingRule activate(CampusBillingRule rule,Long userId){
  if(rule==null || rule.getRuleName()==null || rule.getRuleName().trim().isEmpty() || rule.getRuleName().trim().length()>100) throw new ServiceException("请填写不超过100字的规则名称");
  if(rule.getUnitMinutes()==null || rule.getUnitMinutes()<1 || rule.getUnitMinutes()>1440) throw new ServiceException("计费单位须为1至1440分钟");
  BigDecimal price=rule.getUnitPrice();
  if(price==null || price.signum()<0 || price.scale()>2 || price.compareTo(new BigDecimal("99999999.99"))>0) throw new ServiceException("单价须为0至99999999.99元，最多两位小数");
  rule.setRuleName(rule.getRuleName().trim()); rule.setCreatedBy(userId);
  mapper.lockCurrent(); mapper.deactivate();
  try {mapper.insert(rule);} catch(DuplicateKeyException ex){throw new ServiceException("收费规则已变化，请刷新后重试");}
  return mapper.current();
 }
}
