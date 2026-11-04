package com.ruoyi.system.service.settings.impl;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.settings.CampusBackupRecord;
import com.ruoyi.system.mapper.settings.CampusBackupMapper;
import com.ruoyi.system.service.settings.ICampusBackupService;

@Service
public class CampusBackupServiceImpl implements ICampusBackupService {
 private final CampusBackupMapper mapper;
 @Value("${spring.datasource.druid.master.url}") private String jdbcUrl;
 @Value("${spring.datasource.druid.master.username}") private String username;
 @Value("${spring.datasource.druid.master.password:}") private String password;
 @Value("${campus.backup.dump-executable:mysqldump}") private String executable;
 @Value("${campus.backup.directory:${user.home}/.codex/campus-bike-backups}") private String directory;
 @Value("${campus.backup.login-path:}") private String loginPath;
 public CampusBackupServiceImpl(CampusBackupMapper mapper){this.mapper=mapper;}
 @Override public List<CampusBackupRecord> list(){return mapper.list();}
 @Override public CampusBackupRecord backup(Long userId){
  CampusBackupRecord record=new CampusBackupRecord(); record.setOperatorId(userId); mapper.insert(record);
  Path dump=null,errors=null; Process process=null;
  try {
   if(!jdbcUrl.startsWith("jdbc:mysql://")) throw new IllegalArgumentException("仅支持 MySQL 数据库");
   URI uri=URI.create(jdbcUrl.substring(5)); String db=uri.getPath().substring(1);
   if(uri.getHost()==null || !db.matches("[A-Za-z0-9_]+")) throw new IllegalArgumentException("数据库连接配置无效");
   if(!loginPath.isEmpty() && !loginPath.matches("[A-Za-z0-9_-]{1,64}")) throw new IllegalArgumentException("备份登录配置无效");
   Path dir=Path.of(directory).toAbsolutePath().normalize(); Files.createDirectories(dir);
   String name="campus_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))+"_"+UUID.randomUUID().toString().substring(0,8);
   dump=dir.resolve(name+".sql"); errors=Files.createTempFile(dir,".backup-",".log");
   List<String> args=new ArrayList<>(); args.add(executable);
   if(!loginPath.isEmpty()) args.add("--login-path="+loginPath);
   args.add("--host="+uri.getHost()); args.add("--port="+(uri.getPort()>0?uri.getPort():3306));
   if(loginPath.isEmpty()) args.add("--user="+username);
   args.add("--default-character-set=utf8mb4"); args.add("--single-transaction"); args.add("--quick"); args.add("--skip-lock-tables");
   args.add("--no-tablespaces"); args.add("--set-gtid-purged=OFF"); args.add("--routines"); args.add("--events"); args.add("--triggers"); args.add("--databases"); args.add(db);
   ProcessBuilder builder=new ProcessBuilder(args).redirectOutput(dump.toFile()).redirectError(errors.toFile());
   if(loginPath.isEmpty()) builder.environment().put("MYSQL_PWD",password);
   process=builder.start();
   if(!process.waitFor(120,TimeUnit.SECONDS)){ process.destroyForcibly(); throw new IllegalStateException("备份超时"); }
   if(process.exitValue()!=0) throw new IllegalStateException("备份程序退出码 "+process.exitValue()+"，请检查账号权限和程序配置");
   if(Files.size(dump)==0) throw new IllegalStateException("备份文件为空");
   record.setStatus("success"); record.setFilePath(dump.toString()); record.setResultMessage("备份完成");
  } catch(Exception e){
   if(e instanceof InterruptedException) Thread.currentThread().interrupt();
   if(process!=null && process.isAlive()) process.destroyForcibly();
   if(dump!=null) try{Files.deleteIfExists(dump);}catch(Exception ignored){}
   record.setStatus("failed");
   record.setResultMessage(e instanceof IllegalStateException ? e.getMessage() : "备份执行失败，请检查服务器配置");
  } finally {if(errors!=null) try{Files.deleteIfExists(errors);}catch(Exception ignored){}}
  mapper.finish(record.getId(),record.getStatus(),record.getFilePath(),record.getResultMessage()); return record;
 }
}
