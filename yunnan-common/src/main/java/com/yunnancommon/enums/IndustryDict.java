package com.yunnancommon.enums;

public enum IndustryDict {
//    | 1    | 农、林、牧、渔业                 |
//| ---- | -------------------------------- |
//| 2    | 采矿业                           |
//| 3    | 制造业                           |
//| 4    | 电力、热力、燃气及水生产和供应业 |
//| 5    | 建筑业                           |
//| 6    | 批发和零售业                     |
//| 7    | 交通运输、仓储和邮政业           |
//| 8    | 住宿和餐饮业                     |
//| 9    | 信息传输、软件和信息技术服务业   |
//| 10   | 金融业                           |
//| 11   | 房地产业                         |
//| 12   | 租赁和商务服务业                 |
//| 13   | 科学研究和技术服务业             |
//| 14   | 水利、环境和公共设施管理业       |
//| 15   | 居民服务、修理和其他服务业       |
//| 16   | 教育                             |
//| 17   | 卫生和社会工作                   |
//| 18   | 文化、体育和娱乐业               |
//| 19   | 公共管理、社会保障和社会组织     |
//| 20   | 国际组织                         |

    AGRICULTURE_FORESTRY_ANIMAL_HUSBANDRY(1, "农、林、牧、渔业"),
    MINING(2, "采矿业"),
    MANUFACTURING(3, "制造业"),
    ELECTRICITY_HEAT_GAS_WATER_PRODUCTION_SUPPLY(4, "电力、热力、燃气及水生产和供应业"),
    CONSTRUCTION(5, "建筑业"),
    Wholesaling_and_retail_trade(6, "批发和零售业"),
    TRANSPORTATION_WAREHOUSING_POSTAL_SERVICE(7, "交通运输、仓储和邮政业"),
    ACCOMMODATION_CATERING(8, "住宿和餐饮业"),
    INFORMATION_TRANSMISSION_SOFTWARE_INFORMATION_TECHNOLOGY_SERVICE(9, "信息传输、软件和信息技术服务业"),
    FINANCE(10, "金融业"),
    REAL_ESTATE(11, "房地产业"),
    RENTAL_BUSINESS_SERVICE(12, "租赁和商务服务业"),
    SCIENTIFIC_RESEARCH_TECHNOLOGY_SERVICE(13, "科学研究和技术服务业"),
    WATER_ENVIRONMENT_PUBLIC_FACILITY_MANAGEMENT(14, "水利、环境和公共设施管理业"),
    RESIDENTIAL_SERVICE_REPAIR_OTHER_SERVICE(15, "居民服务、修理和其他服务业"),
    EDUCATION(16, "教育"),
    HEALTH_SOCIAL_WORK(17, "卫生和社会工作"),
    CULTURE_SPORT_ENTERTAINMENT(18, "文化、体育和娱乐业"),
    PUBLIC_MANAGEMENT_SOCIAL_SECURITY_SOCIAL_ORGANIZATION(19, "公共管理、社会保障和社会组织"),
    INTERNATIONAL_ORGANIZATION(20, "国际组织");

    private Integer code;
    private String name;

    IndustryDict(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(Integer code) {
        for (IndustryDict industryDict : IndustryDict.values()) {
            if (industryDict.code.equals(code)) {
                return industryDict.name;
            }
        }
        return null;
    }
}
