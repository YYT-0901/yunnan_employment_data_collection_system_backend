#!/bin/bash

# ====================================================================
# 大规模测试数据快速导入脚本
# ====================================================================

echo "=========================================="
echo "云南省就业数据管理系统"
echo "大规模测试数据导入工具"
echo "=========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查SQL文件是否存在
if [ ! -f "test_data_2022_massive.sql" ]; then
    echo -e "${RED}错误: 找不到 test_data_2022_massive.sql${NC}"
    echo "请先运行: python3 generate_massive_test_data.py"
    exit 1
fi

echo -e "${BLUE}📊 数据文件信息:${NC}"
echo "  文件名: test_data_2022_massive.sql"
echo "  文件大小: $(du -h test_data_2022_massive.sql | cut -f1)"
echo "  行数: $(wc -l < test_data_2022_massive.sql)"
echo ""

# 数据库配置
echo -e "${BLUE}🔧 数据库配置:${NC}"
read -p "数据库地址 [localhost]: " DB_HOST
DB_HOST=${DB_HOST:-localhost}

read -p "数据库端口 [3306]: " DB_PORT
DB_PORT=${DB_PORT:-3306}

read -p "数据库名称 [project1]: " DB_NAME
DB_NAME=${DB_NAME:-project1}

read -p "数据库用户名 [root]: " DB_USER
DB_USER=${DB_USER:-root}

read -sp "数据库密码: " DB_PASS
echo ""
echo ""

# 测试数据库连接
echo -e "${YELLOW}🔍 测试数据库连接...${NC}"
mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1;" 2>/dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 数据库连接失败！请检查配置。${NC}"
    exit 1
fi
echo -e "${GREEN}✅ 数据库连接成功${NC}"
echo ""

# 检查数据库是否存在
echo -e "${YELLOW}🔍 检查数据库...${NC}"
mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME;" 2>/dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 数据库 '$DB_NAME' 不存在${NC}"
    read -p "是否创建数据库? (y/n): " CREATE_DB
    if [ "$CREATE_DB" = "y" ] || [ "$CREATE_DB" = "Y" ]; then
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
        echo -e "${GREEN}✅ 数据库创建成功${NC}"
    else
        echo "导入已取消"
        exit 0
    fi
fi
echo ""

# 备份确认
echo -e "${YELLOW}⚠️  警告: 此操作将清除数据库中的所有企业和2022年数据！${NC}"
read -p "是否需要先备份现有数据? (y/n): " BACKUP
echo ""

if [ "$BACKUP" = "y" ] || [ "$BACKUP" = "Y" ]; then
    BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
    echo -e "${BLUE}📦 正在备份到: $BACKUP_FILE${NC}"
    mysqldump -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" > "$BACKUP_FILE"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 备份完成: $BACKUP_FILE ($(du -h $BACKUP_FILE | cut -f1))${NC}"
    else
        echo -e "${RED}❌ 备份失败${NC}"
        exit 1
    fi
    echo ""
fi

# 最终确认
echo -e "${YELLOW}准备导入以下数据:${NC}"
echo "  • 1,200 家企业"
echo "  • 12 个调查期 (2022年全年)"
echo "  • 14,400 条报表记录"
echo "  • 28,800 条审核记录"
echo "  • 总计 44,400+ 条数据"
echo ""
read -p "确认开始导入? (y/n): " CONFIRM

if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
    echo "导入已取消"
    exit 0
fi

# 开始导入
echo ""
echo -e "${BLUE}🚀 开始导入数据...${NC}"
echo "  这可能需要 30秒 - 2分钟，请耐心等待..."
echo ""

START_TIME=$(date +%s)

# 执行导入
mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < test_data_2022_massive.sql

if [ $? -eq 0 ]; then
    END_TIME=$(date +%s)
    DURATION=$((END_TIME - START_TIME))
    
    echo ""
    echo -e "${GREEN}╔════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                        ║${NC}"
    echo -e "${GREEN}║    ✅  数据导入成功！                   ║${NC}"
    echo -e "${GREEN}║                                        ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${BLUE}⏱️  导入耗时: ${DURATION} 秒${NC}"
    echo ""
    
    # 验证数据
    echo -e "${BLUE}🔍 正在验证数据...${NC}"
    echo ""
    
    ENTERPRISE_COUNT=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -sN -e "SELECT COUNT(*) FROM enterprise_info;")
    PERIOD_COUNT=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -sN -e "SELECT COUNT(*) FROM period_info WHERE investigate_time LIKE '2022%';")
    REPORT_COUNT=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -sN -e "SELECT COUNT(*) FROM enterprise_report_info;")
    AUDIT_COUNT=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -sN -e "SELECT COUNT(*) FROM report_audit_history;")
    
    echo -e "${GREEN}✓${NC} 企业数量: ${ENTERPRISE_COUNT}"
    echo -e "${GREEN}✓${NC} 调查期数: ${PERIOD_COUNT}"
    echo -e "${GREEN}✓${NC} 报表记录: ${REPORT_COUNT}"
    echo -e "${GREEN}✓${NC} 审核记录: ${AUDIT_COUNT}"
    echo ""
    
    if [ "$ENTERPRISE_COUNT" -eq 1200 ] && [ "$PERIOD_COUNT" -eq 12 ] && [ "$REPORT_COUNT" -eq 14400 ]; then
        echo -e "${GREEN}🎉 所有数据验证通过！${NC}"
    else
        echo -e "${YELLOW}⚠️  数据数量与预期不符，请检查导入日志${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}📝 测试账号信息:${NC}"
    echo "  省级: admin_province / 123456"
    echo "  市级: admin_kunming / 123456"
    echo "  企业: ent_01010001 / 123456"
    echo ""
    echo -e "${BLUE}📖 详细说明请查看: MASSIVE_DATA_README.md${NC}"
    echo ""
    
else
    echo ""
    echo -e "${RED}❌ 数据导入失败！${NC}"
    echo "请检查错误信息并重试。"
    echo ""
    exit 1
fi

