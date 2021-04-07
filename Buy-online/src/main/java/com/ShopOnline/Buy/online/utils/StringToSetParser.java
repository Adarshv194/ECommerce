package com.ShopOnline.Buy.online.utils;

import java.util.HashSet;
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

    public static Set<String> toSetOfvalues(String filedValues) {
        Set<String> values = new HashSet<>();
        String []splitValues = filedValues.split(",");

        for(String splitValue : splitValues) {
            values.add(splitValue);
        }

        return values;
    }
}
