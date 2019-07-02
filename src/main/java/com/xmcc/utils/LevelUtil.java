package com.xmcc.utils;

public class LevelUtil {

    // 用来表示第一个层级
    public final static String ROOT = "0";


    public final static String SEPARATOR = ".";

    // 计算 部门的层级
    public static String calculate(String parentLevel,Integer parentId){

        StringBuffer buffer = new StringBuffer();
        if (parentLevel == null){
            return ROOT;
        }else {
            buffer.append(parentLevel).append(SEPARATOR).append(parentId);
            return String.valueOf(buffer);
        }
    }

}
