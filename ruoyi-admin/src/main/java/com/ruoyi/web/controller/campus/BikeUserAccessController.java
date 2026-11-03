package com.ruoyi.web.controller.campus;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.mapper.BikeUserAccessMapper;
import com.ruoyi.system.service.ISysUserService;

@RestController
@RequestMapping("/campus/user/blacklist")
public class BikeUserAccessController extends BaseController
{
    @Autowired
    private BikeUserAccessMapper accessMapper;

    @Autowired
    private ISysUserService userService;

    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @GetMapping("/{userId}")
    public AjaxResult get(@PathVariable Long userId)
    {
        userService.checkUserDataScope(userId);
        Map<String, Object> profile = accessMapper.selectBlacklist(userId);
        return profile == null ? error("用户不存在") : success(profile);
    }

    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户黑名单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody BlacklistUpdate request)
    {
        if (request.getUserId() == null || request.getBlacklisted() == null)
        {
            return error("用户和黑名单状态不能为空");
        }
        userService.checkUserDataScope(request.getUserId());
        SysUser user = userService.selectUserById(request.getUserId());
        if (user == null || "2".equals(user.getDelFlag()))
        {
            return error("用户不存在");
        }
        userService.checkUserAllowed(user);
        if (user.getRoles() != null && user.getRoles().stream().anyMatch(role -> "admin".equals(role.getRoleKey())))
        {
            return error("不允许将管理员加入黑名单");
        }
        String reason = request.getReason() == null ? "" : request.getReason().trim();
        if (request.getBlacklisted() && (reason.isEmpty() || reason.length() > 255))
        {
            return error("请填写不超过255字的原因");
        }
        accessMapper.saveBlacklist(request.getUserId(), request.getBlacklisted() ? 1 : 0,
                request.getBlacklisted() ? reason : null);
        return success();
    }

    public static class BlacklistUpdate
    {
        private Long userId;
        private Boolean blacklisted;
        private String reason;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Boolean getBlacklisted() { return blacklisted; }
        public void setBlacklisted(Boolean blacklisted) { this.blacklisted = blacklisted; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
