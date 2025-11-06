package com.yunnancommon.enums;

public enum NatureDict {

    STATE_OWNED_ENTERPRISE(1, "国有企业"),
    COLLECTIVE_ENTERPRISE(2, "集体企业"),
    PRIVATE_ENTERPRISE(3, "私营企业"),
    FOREIGN_INVESTMENT_ENTERPRISE(4, "外商投资企业"),
    HONGKONG_TAIWAN_INVESTMENT_ENTERPRISE(5, "港澳台投资企业"),
    SHAREHOLDING_ENTERPRISE(6, "股份制企业"),
    INDIVIDUAL_BUSINESS(7, "个体工商户"),
    OTHER_ENTERPRISE(8, "其他企业");

    private final Integer code;
    private final String name;

    NatureDict(Integer code, String name) {
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
        for (NatureDict natureDict : NatureDict.values()) {
            if (natureDict.code.equals(code)) {
                return natureDict.name;
            }
        }
        return null;
    }
}
