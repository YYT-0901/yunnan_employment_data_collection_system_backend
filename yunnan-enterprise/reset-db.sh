#!/bin/bash
echo "正在清理数据库..."
mysql -u root -p1234567890 project1 << SQL
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS account_info;
DROP TABLE IF EXISTS enterprise_info;
DROP TABLE IF EXISTS enterprise_report_info;
DROP TABLE IF EXISTS log_info;
DROP TABLE IF EXISTS notice_info;
DROP TABLE IF EXISTS notice_read_info;
DROP TABLE IF EXISTS period_info;
DROP TABLE IF EXISTS report_info;
DROP TABLE IF EXISTS report_audit_history;
SET FOREIGN_KEY_CHECKS = 1;
SQL
echo "数据库清理完成！"
