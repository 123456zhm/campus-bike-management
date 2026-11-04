-- 租借菜单。先执行 003_account_role.sql；预留菜单 ID 6300–6304。
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6300,'租借管理',0,11,'campusRental',NULL,'CampusRentalRoot','M','0','0','','guide','admin',NOW(),'租借相关功能' WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6300);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6301,'我的租借',6300,1,'mine','campus/rental/index','CampusRentalMine','C','0','0','campus:rental:mine','list','admin',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6301);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6302,'订单管理',6300,2,'admin','campus/rental/admin','CampusRentalAdmin','C','0','0','campus:rental:list','list','admin',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6302);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6303,'借车',6301,1,'',NULL,'','F','0','0','campus:rental:borrow','#','admin',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6303);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6304,'还车',6301,2,'',NULL,'','F','0','0','campus:rental:return','#','admin',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6304);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6305,'异常订单处理',6302,1,'',NULL,'','F','0','0','campus:rental:manage','#','admin',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=6305);
INSERT INTO sys_role_menu(role_id,menu_id)
SELECT r.role_id,m.menu_id FROM sys_role r JOIN sys_menu m ON
  (m.menu_id=6300 AND m.path='campusRental' AND m.menu_type='M') OR
  (m.menu_id=6301 AND m.parent_id=6300 AND m.perms='campus:rental:mine') OR
  (m.menu_id=6303 AND m.parent_id=6301 AND m.perms='campus:rental:borrow') OR
  (m.menu_id=6304 AND m.parent_id=6301 AND m.perms='campus:rental:return')
WHERE r.role_key='campus_user' AND r.del_flag='0'
AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);
