package me.kteq.hiddenarmor.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class StrUtil {

    public static String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> color(List<String> stringList){
        List<String> coloredStringList = new ArrayList<>();
        for(String s : stringList){
            coloredStringList.add(color(s));
        }
        return coloredStringList;
    }

}
