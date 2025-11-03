#!/bin/bash

echo "========================================"
echo "导入使用正确代码的测试数据"
echo "========================================"
echo ""
echo "📊 数据特点:"
echo "  ✓ 129个区县地区（来自 yunnan_region_code.json）"
echo "  ✓ 22种企业性质（来自 enterprise_types.json）"
echo "  ✓ 97个行业分类（来自 enterprise_industries.json）"
echo "  ✓ 1200家企业 × 12个月 = 14400条报表"
echo ""
echo "⚠️  注意: 此操作将清除所有企业和2022年数据！"
echo ""

read -p "确认导入? (y/n): " CONFIRM

if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
    echo "已取消"
    exit 0
fi

echo ""
echo "🚀 开始导入..."
echo ""

mysql -u root -p project1 < test_data_2022_corrected.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 导入成功！"
    echo ""
    echo "验证数据:"
    mysql -u root -p project1 -e "
    SELECT '企业总数:' AS info, COUNT(*) AS count FROM enterprise_info;
    SELECT '调查期数:' AS info, COUNT(*) AS count FROM period_info WHERE investigate_time LIKE '2022%';
    SELECT '报表总数:' AS info, COUNT(*) AS count FROM enterprise_report_info;
    "
else
    echo ""
    echo "❌ 导入失败"
    exit 1
fi

