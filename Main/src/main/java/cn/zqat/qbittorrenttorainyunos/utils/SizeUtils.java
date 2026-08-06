package cn.zqat.qbittorrenttorainyunos.utils;

import org.springframework.stereotype.Component;

@Component
public class SizeUtils {

    public String getSize(long size) {
        if (size < 1024) {
            return size + " B";
        }

        int exp = (int) (Math.log(size) / Math.log(1024));   // 计算该用哪一级单位
        String[] units = {"KB", "MB", "GB", "TB", "PB"};
        String unit = units[exp - 1];

        double value = size / Math.pow(1024, exp);           // 换算后的值，恒 < 1024
        return String.format("%.2f %s", value, unit);
    }

}
