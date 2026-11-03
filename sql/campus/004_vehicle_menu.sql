-- 车辆模块菜单，保留 ID 6200-6206。请在账号模块创建 campus_user 角色后执行一次。
-- 开发环境中请检查 ID 是否已被占用；本脚本不覆盖已有菜单。
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,remark) VALUES
(6200,'车辆运营',0,6,'vehicle-admin',NULL,'','',1,0,'M','0','0','','component','admin',NOW(),'','车辆运营目录'),
(6201,'车辆管理',6200,1,'vehicle','campus/vehicle/index','','',1,0,'C','0','0','campus:vehicle:list','list','admin',NOW(),'','车辆管理'),
(6202,'车辆新增',6201,1,'',NULL,'','',1,0,'F','0','0','campus:vehicle:add','#','admin',NOW(),'',''),
(6203,'车辆修改',6201,2,'',NULL,'','',1,0,'F','0','0','campus:vehicle:edit','#','admin',NOW(),'',''),
(6204,'车辆详情',6201,3,'',NULL,'','',1,0,'F','0','0','campus:vehicle:query','#','admin',NOW(),'',''),
(6205,'校园骑行',0,7,'campus-ride',NULL,'','',1,0,'M','0','0','','guide','admin',NOW(),'','校园用车目录'),
(6206,'可借车辆',6205,1,'available','campus/vehicleBrowse/index','','',1,0,'C','0','0','campus:vehicle:browse','list','admin',NOW(),'','普通用户车辆浏览');
INSERT INTO sys_role_menu(role_id,menu_id)
SELECT role_id,6205 FROM sys_role WHERE role_key='campus_user' AND del_flag='0'
UNION ALL
SELECT role_id,6206 FROM sys_role WHERE role_key='campus_user' AND del_flag='0';
