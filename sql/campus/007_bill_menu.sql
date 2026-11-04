-- 费用菜单：先执行 003_account_role.sql；预留 ID 6500–6503。
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time,remark)
SELECT 6500,'费用运营',0,9,'bill-admin',NULL,'CampusBillAdminRoot','M','0','0','','money','admin',NOW(),'费用运营目录' WHERE NOT EXISTS(SELECT 1 FROM sys_menu WHERE menu_id=6500);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6501,'全部账单',6500,1,'bills','campus/billAdmin/index','CampusBillAdmin','C','0','0','campus:bill:list','list','admin',NOW() WHERE NOT EXISTS(SELECT 1 FROM sys_menu WHERE menu_id=6501);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6502,'我的费用',0,10,'my-expenses',NULL,'CampusBillMineRoot','M','0','0','','money','admin',NOW() WHERE NOT EXISTS(SELECT 1 FROM sys_menu WHERE menu_id=6502);
INSERT INTO sys_menu(menu_id,menu_name,parent_id,order_num,path,component,route_name,menu_type,visible,status,perms,icon,create_by,create_time)
SELECT 6503,'我的账单',6502,1,'bills','campus/billMine/index','CampusBillMine','C','0','0','campus:bill:mine','list','admin',NOW() WHERE NOT EXISTS(SELECT 1 FROM sys_menu WHERE menu_id=6503);
INSERT INTO sys_role_menu(role_id,menu_id)
SELECT r.role_id,m.menu_id FROM sys_role r JOIN sys_menu m ON
 (m.menu_id=6502 AND m.path='my-expenses' AND m.menu_type='M') OR
 (m.menu_id=6503 AND m.parent_id=6502 AND m.perms='campus:bill:mine' AND m.component='campus/billMine/index')
WHERE r.role_key='campus_user' AND r.del_flag='0'
AND NOT EXISTS(SELECT 1 FROM sys_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);
