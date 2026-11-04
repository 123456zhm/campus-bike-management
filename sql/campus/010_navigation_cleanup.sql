-- 隐藏若依示例官网入口；仅匹配原始默认菜单，不删除菜单或业务数据。
UPDATE sys_menu SET visible='1' WHERE menu_id=4 AND menu_name='若依官网' AND path='http://ruoyi.vip';
