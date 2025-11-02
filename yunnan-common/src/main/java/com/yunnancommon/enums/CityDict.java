package com.yunnancommon.enums;

public enum CityDict {
    LIN_CANG("1", "临沧市"),
    LI_JIANG("2", "丽江市"),
    BAO_SHAN("3", "保山市"),
    DA_LI("4", "大理白族自治州"),
    DE_HONG("5", "德宏傣族景颇族自治州"),
    NU_JIANG("6", "怒江傈僳族自治州"),
    WEN_SHAN("7", "文山壮族苗族自治州"),
    KUN_MING("8", "昆明市"),
    ZHAO_TONG("9", "昭通市"),
    PU_ER("10", "普洱市"),
    QU_JING("11", "曲靖市"),
    CHU_XIONG("12", "楚雄彝族自治州"),
    YU_XI("13", "玉溪市"),
    HONG_HE("14", "红河哈尼族彝族自治州"),
    XI_SHUANG("15", "西双版纳傣族自治州"),
    DI_QING("16", "迪庆藏族自治州");

    private String code;
    private String name;

    CityDict(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(String code) {
        for (CityDict cityDict : CityDict.values()) {
            if (cityDict.code.equals(code)) {
                return cityDict.name;
            }
        }
        return null;
    }
}
