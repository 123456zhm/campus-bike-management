-- 运营报表菜单：预留 ID 6600–6601，仅管理员访问。
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6600,'运营分析',0,14,'campus-reports',NULL,'CampusReportsRoot','M','0','0','','chart','admin',NOW(),'校园运营分析目录' WHERE NOT EXISTS(SELECT 1 FROM sys_menu WHERE menu_id=6600);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6601,'统计报表',6600,1,'overview','campus/report/index','CampusReport','C','0','0','campus:report:view','chart','admin',NOW(),'仅管理员查看' WHERE NOT EXISTS(SELECT 1 FROM sys_menu WHERE menu_id=6601);
