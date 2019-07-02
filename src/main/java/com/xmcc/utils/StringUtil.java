package com.xmcc.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * string 类型 转换成 List
 */
public class StringUtil {

    public static List<Integer> strToList(String str){
        if (str != null || !str.equals("")){
            String[] split = str.split(",");
            ArrayList<Integer> integers = new ArrayList<>();
            for (String id : split){
                integers.add(Integer.parseInt(id));
            }
            return integers;
        }
        return new ArrayList<>();
    }
}
