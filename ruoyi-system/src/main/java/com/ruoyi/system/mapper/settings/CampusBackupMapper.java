package com.ruoyi.system.mapper.settings;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.ruoyi.system.domain.settings.CampusBackupRecord;
public interface CampusBackupMapper {
 @Insert("INSERT INTO bike_backup_record(operator_id,status) VALUES(#{operatorId},'running')") @Options(useGeneratedKeys=true,keyProperty="id") int insert(CampusBackupRecord record);
 @Update("UPDATE bike_backup_record SET status=#{status},file_path=#{path},result_message=#{message},finished_at=NOW() WHERE id=#{id}")
 int finish(@Param("id") Long id,@Param("status") String status,@Param("path") String path,@Param("message") String message);
 @Select("SELECT id,operator_id AS operatorId,status,file_path AS filePath,result_message AS resultMessage,created_at AS createdAt,finished_at AS finishedAt FROM bike_backup_record ORDER BY id DESC") List<CampusBackupRecord> list();
}
