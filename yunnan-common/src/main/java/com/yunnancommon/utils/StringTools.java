package com.yunnancommon.utils;

import com.yunnancommon.entity.constants.Constants;

public class StringTools {

    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";

    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int index = (int)(Math.random() * CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    public static String getRandomNumber(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int index = (int)(Math.random() * NUMBERS.length());
            sb.append(NUMBERS.charAt(index));
        }
        return sb.toString();
    }

    public static Integer getTopCategoryCode(Integer code, String category) {
        if (code == null) {
            return null;
        }
        return switch (category) {
            case Constants.REGION -> code / 10000;
            case Constants.NATURE, Constants.INDUSTRY -> code / 100;
            default -> null;
        };
    }
}
