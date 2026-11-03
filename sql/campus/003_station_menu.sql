-- 站点模块菜单与权限。请先执行账号模块角色初始化，确保 campus_user 角色存在。
-- 分配固定菜单 ID 6100–6106；执行前请确认这些 ID 未被其他菜单占用。
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6100,'校园站点',0,10,'bikeStation',NULL,'BikeStationRoot','M','0','0','','guide','admin',NOW(),'站点相关功能'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6100);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6101,'站点管理',6100,1,'manage','bike/station/index','BikeStationAdmin','C','0','0','bike:station:list','list','admin',NOW(),'管理员维护站点'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6101);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6102,'站点查询',6100,2,'browse','bike/station/browse','BikeStationBrowse','C','0','0','bike:station:browse','guide','admin',NOW(),'用户浏览可用站点'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6102);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6103,'站点详情',6101,1,'','#','','F','0','0','bike:station:query','#','admin',NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6103);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6104,'新增站点',6101,2,'','#','','F','0','0','bike:station:add','#','admin',NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6104);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6105,'修改站点',6101,3,'','#','','F','0','0','bike:station:edit','#','admin',NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6105);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6106,'删除站点',6101,4,'','#','','F','0','0','bike:station:remove','#','admin',NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6106);
-- 普通用户仅获得站点目录和查询菜单，不获得站点管理权限。
INSERT INTO sys_role_menu(role_id,menu_id)
SELECT r.role_id,m.menu_id FROM sys_role r JOIN sys_menu m
  ON (m.menu_id=6100 AND m.path='bikeStation' AND m.menu_type='M')
  OR (m.menu_id=6102 AND m.parent_id=6100 AND m.perms='bike:station:browse' AND m.component='bike/station/browse')
WHERE r.role_key='campus_user' AND r.del_flag='0'
AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);
