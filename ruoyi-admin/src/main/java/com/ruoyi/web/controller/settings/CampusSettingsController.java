package com.ruoyi.web.controller.settings;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.settings.CampusBillingRule;
import com.ruoyi.system.service.settings.ICampusBackupService;
import com.ruoyi.system.service.settings.ICampusBillingRuleService;
@RestController
@RequestMapping("/campus/settings")
public class CampusSettingsController extends BaseController {
 private final ICampusBillingRuleService rules; private final ICampusBackupService backups;
 public CampusSettingsController(ICampusBillingRuleService rules,ICampusBackupService backups){this.rules=rules;this.backups=backups;}
 @PreAuthorize("isAuthenticated()") @GetMapping("/billing/current")
 public AjaxResult current(){CampusBillingRule r=rules.current();return r==null?error("暂未配置收费规则"):success(Map.of("ruleName",r.getRuleName(),"unitMinutes",r.getUnitMinutes(),"unitPrice",r.getUnitPrice()));}
 @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:settings:rule')") @GetMapping("/billing")
 public TableDataInfo listRules(){startPage();return getDataTable(rules.list());}
 @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:settings:rule')") @Log(title="收费设置",businessType=BusinessType.UPDATE) @PostMapping("/billing")
 public AjaxResult activate(@RequestBody CampusBillingRule rule){return success(rules.activate(rule,getUserId()));}
 @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:settings:backup')") @GetMapping("/backups")
 public TableDataInfo listBackups(){startPage();return getDataTable(backups.list());}
 @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('campus:settings:backup')") @Log(title="数据库备份",businessType=BusinessType.OTHER) @PostMapping("/backups")
 public AjaxResult backup(){return success(backups.backup(getUserId()));}
}
