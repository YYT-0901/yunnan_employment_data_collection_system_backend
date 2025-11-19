#!/bin/bash

echo "╔════════════════════════════════════════════════════════╗"
echo "║   创建 project2 并导入使用正确代码的测试数据           ║"
echo "╚════════════════════════════════════════════════════════╝"
echo ""
echo "📊 数据特点:"
echo "  ✓ 1461个街道/乡镇（地区最底层 - 3级）"
echo "  ✓ 22种企业性质（性质最底层 - 2级）"
echo "  ✓ 97个行业分类（行业最底层 - 2级）"
echo "  ✓ 1200家企业 × 12个月 = 14400条报表"
echo ""

echo "⚠️  注意: 请确保已经手动创建 project2 数据库和表结构"
echo ""
read -p "按 Enter 继续导入数据..." 
echo ""

# 导入测试数据
echo ""
echo "📥 导入测试数据..."
echo "   (预计耗时: 30秒 - 2分钟)"
echo ""

mysql -u root -p project2 < test_data_2022_final.sql

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ 导入失败"
    exit 1
fi

echo ""
echo "✅ 导入成功！"
echo ""

# 验证数据
echo "🔍 验证数据..."
echo ""
mysql -u root -p project2 -e "
SELECT '企业总数:' AS info, COUNT(*) AS count FROM enterprise_info;
SELECT '调查期数:' AS info, COUNT(*) AS count FROM period_info WHERE investigate_time LIKE '2022%';
SELECT '报表总数:' AS info, COUNT(*) AS count FROM enterprise_report_info;
SELECT '审核记录:' AS info, COUNT(*) AS count FROM report_audit_history;
"

echo ""
echo "地区代码示例（前10个）:"
mysql -u root -p project2 -e "SELECT DISTINCT region FROM enterprise_info ORDER BY region LIMIT 10;"

echo ""
echo "╔════════════════════════════════════════════════════════╗"
echo "║                  设置完成！                            ║"
echo "╚════════════════════════════════════════════════════════╝"
echo ""
echo "📝 下一步："
echo "   1. 修改后端配置使用 project2："
echo "      编辑: backend/yunnan-province/src/main/resources/application.yml"
echo "      将: jdbc:mysql://127.0.0.1:3306/project1"
echo "      改为: jdbc:mysql://127.0.0.1:3306/project2"
echo ""
echo "   2. 重启后端服务"
echo ""
echo "   3. 刷新前端页面，查看地区名称是否正确显示"
echo ""
echo "🔐 测试账号："
echo "   省级: admin_province / 123456"
echo "   市级: admin_kunming / 123456"
echo ""

