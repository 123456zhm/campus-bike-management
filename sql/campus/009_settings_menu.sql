-- 系统设置菜单：预留 ID 6700–6702；仅管理员使用。
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6700,'校园设置',0,15,'campus-settings',NULL,'CampusSettingsRoot','M','0','0','','setting','admin',NOW(),'收费和备份设置' WHERE NOT EXISTS(SELECT 1 FROM sys_menu WHERE menu_id=6700);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6701,'系统设置',6700,1,'index','campus/settings/index','CampusSettings','C','0','0','campus:settings:rule','setting','admin',NOW() WHERE NOT EXISTS(SELECT 1 FROM sys_menu WHERE menu_id=6701);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6702,'数据库备份',6701,1,'',NULL,'','F','0','0','campus:settings:backup','#','admin',NOW() WHERE NOT EXISTS(SELECT 1 FROM sys_menu WHERE menu_id=6702);
