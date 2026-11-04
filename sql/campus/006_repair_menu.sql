-- 报修菜单：先执行 003_account_role.sql。请确认菜单 ID 6400–6405 未被其他用途占用。
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6400,'车辆报修',0,13,'bikeRepair',NULL,'BikeRepairRoot','M','0','0','','tool','admin',NOW(),'车辆报修功能'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6400);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6401,'报修工单',6400,1,'manage','bike/repair/index','BikeRepairAdmin','C','0','0','bike:repair:list','list','admin',NOW(),'管理员处理报修'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6401);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6402,'我的报修',6400,2,'mine','bike/repair/mine','BikeRepairMine','C','0','0','bike:repair:mine','form','admin',NOW(),'用户提交并查看本人报修'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6402);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6403,'工单详情',6401,1,'','#','','F','0','0','bike:repair:query','#','admin',NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6403);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6404,'处理工单',6401,2,'','#','','F','0','0','bike:repair:handle','#','admin',NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6404);
-- 校园用户只获得本人报修入口，管理员功能必须单独授权。
INSERT INTO sys_role_menu(role_id,menu_id)
SELECT r.role_id,m.menu_id FROM sys_role r JOIN sys_menu m
  ON (m.menu_id=6400 AND m.path='bikeRepair' AND m.menu_type='M')
  OR (m.menu_id=6402 AND m.parent_id=6400 AND m.perms='bike:repair:mine' AND m.component='bike/repair/mine')
WHERE r.role_key='campus_user' AND r.del_flag='0'
AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);
