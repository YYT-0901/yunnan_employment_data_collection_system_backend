-- ====================================================================
-- 创建 project2 数据库和表结构
-- 从 project1 复制表结构，不复制数据
-- ====================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS project2 CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用 project2
USE project2;

-- 从 project1 复制表结构（不包含数据）
CREATE TABLE IF NOT EXISTS account_info LIKE project1.account_info;
CREATE TABLE IF NOT EXISTS enterprise_info LIKE project1.enterprise_info;
CREATE TABLE IF NOT EXISTS enterprise_report_info LIKE project1.enterprise_report_info;
CREATE TABLE IF NOT EXISTS log_info LIKE project1.log_info;
CREATE TABLE IF NOT EXISTS notice_info LIKE project1.notice_info;
CREATE TABLE IF NOT EXISTS notice_read_info LIKE project1.notice_read_info;
CREATE TABLE IF NOT EXISTS period_info LIKE project1.period_info;
CREATE TABLE IF NOT EXISTS report_info LIKE project1.report_info;
CREATE TABLE IF NOT EXISTS report_audit_history LIKE project1.report_audit_history;

-- 验证表结构
SELECT '已创建的表:' AS info;
SHOW TABLES;

