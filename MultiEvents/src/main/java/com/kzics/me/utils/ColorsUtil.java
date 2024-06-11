package com.kzics.me.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.function.Function;

public class ColorsUtil {

    public static Function<String,String> translate = (str)-> ChatColor.translateAlternateColorCodes('&',str);

    public static Function<List<String>, List<String>> translateList = (list) -> {
        list.replaceAll(t -> translate.apply(t));
        return list;
    };
}
