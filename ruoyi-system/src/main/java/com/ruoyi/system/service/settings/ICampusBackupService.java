package com.ruoyi.system.service.settings;
import java.util.List;
import com.ruoyi.system.domain.settings.CampusBackupRecord;
public interface ICampusBackupService { CampusBackupRecord backup(Long userId); List<CampusBackupRecord> list(); }
