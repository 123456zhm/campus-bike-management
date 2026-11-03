-- 普通用车用户只获得单独授予的校园业务权限，不使用若依自带的 common 管理角色。
-- 对已有数据库可重复执行；原有账户和角色关系不作修改。
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time, remark)
SELECT '校园普通用户', 'campus_user', 3, '5', '0', '0', 'admin', NOW(), '仅使用获授权的个人用车功能'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'campus_user' AND del_flag = '0');
