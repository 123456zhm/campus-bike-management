package com.ruoyi.system.domain.settings;
import java.time.LocalDateTime;
public class CampusBackupRecord {
 private Long id,operatorId; private String status,filePath,resultMessage; private LocalDateTime createdAt,finishedAt;
 public Long getId(){return id;} public void setId(Long v){id=v;} public Long getOperatorId(){return operatorId;} public void setOperatorId(Long v){operatorId=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getFilePath(){return filePath;} public void setFilePath(String v){filePath=v;}
 public String getResultMessage(){return resultMessage;} public void setResultMessage(String v){resultMessage=v;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
 public LocalDateTime getFinishedAt(){return finishedAt;} public void setFinishedAt(LocalDateTime v){finishedAt=v;}
}
