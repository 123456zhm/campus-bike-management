-- 仅供课程演示：在空白业务表中执行；需先导入若依初始化数据及 001_schema.sql。
-- 不包含真实账户或密码。重复执行不会新增同编号的站点、车辆或第二条生效规则。
INSERT INTO bike_station(station_no,name,location_text)
SELECT 'ST001','教学楼站','校园教学楼旁'
WHERE NOT EXISTS (SELECT 1 FROM bike_station WHERE station_no='ST001');
INSERT INTO bike_station(station_no,name,location_text)
SELECT 'ST002','宿舍站','学生宿舍旁'
WHERE NOT EXISTS (SELECT 1 FROM bike_station WHERE station_no='ST002');
INSERT INTO bike_vehicle(vehicle_no,station_id)
SELECT 'BIKE001',s.id FROM bike_station s WHERE s.station_no='ST001'
AND NOT EXISTS (SELECT 1 FROM bike_vehicle WHERE vehicle_no='BIKE001');
INSERT INTO bike_vehicle(vehicle_no,station_id)
SELECT 'BIKE002',s.id FROM bike_station s WHERE s.station_no='ST002'
AND NOT EXISTS (SELECT 1 FROM bike_vehicle WHERE vehicle_no='BIKE002');
INSERT INTO bike_billing_rule(rule_name,unit_minutes,unit_price,is_active,created_by)
SELECT '标准计费',30,1.00,1,u.user_id FROM sys_user u
WHERE u.user_name='admin' AND NOT EXISTS (SELECT 1 FROM bike_billing_rule WHERE is_active=1);
