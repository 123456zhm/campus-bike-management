-- 校园助力车业务表 V1；先导入若依 ry_20260417.sql，再导入本文件。
-- 本脚本只创建业务表，不创建数据库，不删除或修改已有数据。
-- 使用 MySQL 8.0.16+（CHECK 约束生效），InnoDB，utf8mb4。

CREATE TABLE bike_user_profile (
  user_id BIGINT NOT NULL COMMENT '若依用户ID',
  blacklisted TINYINT NOT NULL DEFAULT 0 COMMENT '是否列入黑名单',
  blacklist_reason VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  CONSTRAINT ck_bup_blacklisted CHECK (blacklisted IN (0,1)),
  CONSTRAINT fk_bup_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园用户业务信息';

CREATE TABLE bike_station (
  id BIGINT NOT NULL AUTO_INCREMENT,
  station_no VARCHAR(32) NOT NULL,
  name VARCHAR(100) NOT NULL,
  location_text VARCHAR(255) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'enabled',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id), UNIQUE KEY uk_station_no(station_no),
  KEY idx_station_status(status),
  CONSTRAINT ck_station_status CHECK (status IN ('enabled','disabled'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点';

CREATE TABLE bike_vehicle (
  id BIGINT NOT NULL AUTO_INCREMENT,
  vehicle_no VARCHAR(32) NOT NULL,
  station_id BIGINT NOT NULL COMMENT '最后所属站点；还车时更新',
  status VARCHAR(16) NOT NULL DEFAULT 'idle',
  description VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id), UNIQUE KEY uk_vehicle_no(vehicle_no),
  KEY idx_vehicle_station_status(station_id,status),
  CONSTRAINT ck_vehicle_status CHECK(status IN ('idle','rented','repair','scrapped')),
  CONSTRAINT fk_vehicle_station FOREIGN KEY(station_id) REFERENCES bike_station(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆';

CREATE TABLE bike_billing_rule (
  id BIGINT NOT NULL AUTO_INCREMENT,
  rule_name VARCHAR(100) NOT NULL,
  unit_minutes INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  is_active TINYINT NOT NULL DEFAULT 0,
  active_marker TINYINT GENERATED ALWAYS AS (CASE WHEN is_active=1 THEN 1 ELSE NULL END) STORED,
  created_by BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id), UNIQUE KEY uk_one_active_rule(active_marker),
  CONSTRAINT ck_rule_unit CHECK(unit_minutes>0),
  CONSTRAINT ck_rule_price CHECK(unit_price>=0),
  CONSTRAINT ck_rule_active CHECK(is_active IN (0,1)),
  CONSTRAINT fk_rule_creator FOREIGN KEY(created_by) REFERENCES sys_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计费规则版本';

CREATE TABLE bike_rental_order (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_no VARCHAR(40) NOT NULL,
  user_id BIGINT NOT NULL,
  vehicle_id BIGINT NOT NULL,
  start_station_id BIGINT NOT NULL,
  end_station_id BIGINT DEFAULT NULL,
  billing_rule_id BIGINT NOT NULL,
  unit_minutes INT NOT NULL COMMENT '借车时的计费单位快照',
  unit_price DECIMAL(10,2) NOT NULL COMMENT '借车时的价格快照',
  started_at DATETIME NOT NULL,
  ended_at DATETIME DEFAULT NULL,
  payable_amount DECIMAL(12,2) DEFAULT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'ongoing',
  active_vehicle_id BIGINT GENERATED ALWAYS AS (CASE WHEN status IN ('ongoing','exception') THEN vehicle_id ELSE NULL END) STORED,
  active_user_id BIGINT GENERATED ALWAYS AS (CASE WHEN status IN ('ongoing','exception') THEN user_id ELSE NULL END) STORED,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id), UNIQUE KEY uk_rental_order_no(order_no),
  UNIQUE KEY uk_active_vehicle(active_vehicle_id), UNIQUE KEY uk_active_user(active_user_id),
  KEY idx_rental_user_time(user_id,started_at), KEY idx_rental_status_time(status,started_at),
  CONSTRAINT ck_order_status CHECK(status IN ('ongoing','exception','completed','closed')),
  CONSTRAINT ck_order_rule CHECK(unit_minutes>0 AND unit_price>=0),
  CONSTRAINT ck_order_time CHECK(ended_at IS NULL OR ended_at>=started_at),
  CONSTRAINT ck_active_order_no_end CHECK(status NOT IN ('ongoing','exception') OR (ended_at IS NULL AND end_station_id IS NULL AND payable_amount IS NULL)),
  CONSTRAINT ck_order_amount CHECK(payable_amount IS NULL OR payable_amount>=0),
  CONSTRAINT ck_order_completion CHECK(status<>'completed' OR (ended_at IS NOT NULL AND end_station_id IS NOT NULL AND payable_amount IS NOT NULL)),
  CONSTRAINT fk_order_user FOREIGN KEY(user_id) REFERENCES sys_user(user_id) ON DELETE RESTRICT,
  CONSTRAINT fk_order_vehicle FOREIGN KEY(vehicle_id) REFERENCES bike_vehicle(id) ON DELETE RESTRICT,
  CONSTRAINT fk_order_start_station FOREIGN KEY(start_station_id) REFERENCES bike_station(id) ON DELETE RESTRICT,
  CONSTRAINT fk_order_end_station FOREIGN KEY(end_station_id) REFERENCES bike_station(id) ON DELETE RESTRICT,
  CONSTRAINT fk_order_rule FOREIGN KEY(billing_rule_id) REFERENCES bike_billing_rule(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租借订单';

CREATE TABLE bike_repair_order (
  id BIGINT NOT NULL AUTO_INCREMENT,
  repair_no VARCHAR(40) NOT NULL,
  vehicle_id BIGINT NOT NULL,
  reporter_id BIGINT NOT NULL,
  description VARCHAR(1000) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'pending',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  resolved_at DATETIME DEFAULT NULL,
  PRIMARY KEY(id), UNIQUE KEY uk_repair_no(repair_no),
  KEY idx_repair_vehicle_status(vehicle_id,status), KEY idx_repair_reporter_time(reporter_id,created_at),
  CONSTRAINT ck_repair_status CHECK(status IN ('pending','processing','completed')),
  CONSTRAINT fk_repair_vehicle FOREIGN KEY(vehicle_id) REFERENCES bike_vehicle(id) ON DELETE RESTRICT,
  CONSTRAINT fk_repair_reporter FOREIGN KEY(reporter_id) REFERENCES sys_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修工单';

CREATE TABLE bike_repair_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  repair_id BIGINT NOT NULL,
  operator_id BIGINT NOT NULL,
  from_status VARCHAR(16) NOT NULL,
  to_status VARCHAR(16) NOT NULL,
  note VARCHAR(1000) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id), KEY idx_repair_record(repair_id,created_at),
  CONSTRAINT fk_repair_record_order FOREIGN KEY(repair_id) REFERENCES bike_repair_order(id) ON DELETE RESTRICT,
  CONSTRAINT fk_repair_record_operator FOREIGN KEY(operator_id) REFERENCES sys_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单处理记录';

CREATE TABLE bike_bill (
  id BIGINT NOT NULL AUTO_INCREMENT,
  bill_no VARCHAR(40) NOT NULL,
  order_id BIGINT NOT NULL,
  payable_amount DECIMAL(12,2) NOT NULL,
  paid_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
  status VARCHAR(16) NOT NULL DEFAULT 'unpaid',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id), UNIQUE KEY uk_bill_no(bill_no), UNIQUE KEY uk_bill_order(order_id),
  KEY idx_bill_status_time(status,created_at),
  CONSTRAINT ck_bill_amount CHECK(payable_amount>=0 AND paid_amount>=0 AND paid_amount<=payable_amount),
  CONSTRAINT ck_bill_status CHECK((status='unpaid' AND paid_amount=0 AND payable_amount>0) OR (status='paid' AND paid_amount=payable_amount)),
  CONSTRAINT fk_bill_order FOREIGN KEY(order_id) REFERENCES bike_rental_order(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单账单';

CREATE TABLE bike_payment_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  payment_no VARCHAR(40) NOT NULL,
  request_key VARCHAR(64) NOT NULL COMMENT '客户端请求幂等标识',
  bill_id BIGINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  paid_at DATETIME NOT NULL,
  PRIMARY KEY(id), UNIQUE KEY uk_payment_no(payment_no), UNIQUE KEY uk_payment_request(request_key),
  UNIQUE KEY uk_payment_bill(bill_id), KEY idx_payment_time(paid_at),
  CONSTRAINT ck_payment_amount CHECK(amount>0),
  CONSTRAINT fk_payment_bill FOREIGN KEY(bill_id) REFERENCES bike_bill(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模拟支付成功流水';

CREATE TABLE bike_order_operation_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  operator_id BIGINT NOT NULL,
  operation VARCHAR(40) NOT NULL,
  reason VARCHAR(1000) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id), KEY idx_order_log(order_id,created_at),
  CONSTRAINT fk_order_log_order FOREIGN KEY(order_id) REFERENCES bike_rental_order(id) ON DELETE RESTRICT,
  CONSTRAINT fk_order_log_operator FOREIGN KEY(operator_id) REFERENCES sys_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常订单处理记录';

CREATE TABLE bike_backup_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  operator_id BIGINT NOT NULL,
  status VARCHAR(16) NOT NULL,
  file_path VARCHAR(500) DEFAULT NULL,
  result_message VARCHAR(1000) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  finished_at DATETIME DEFAULT NULL,
  PRIMARY KEY(id), KEY idx_backup_time(created_at),
  CONSTRAINT ck_backup_status CHECK(status IN ('running','success','failed')),
  CONSTRAINT fk_backup_operator FOREIGN KEY(operator_id) REFERENCES sys_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库备份记录';
