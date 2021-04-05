package com.ShopOnline.Buy.online.utils;

import java.util.Set;

public class StringToSetParser {

    public static String toCommaSeperatedString(Set<String> fieldValues) {
        String values = "";

        if(fieldValues.isEmpty()) {
            return values;
        }
        else {
            for(String value : fieldValues) {
                values = values + value + ",";
            }

            values = values.substring(0, values.length()-1);
            return values;
        }
    }
}
