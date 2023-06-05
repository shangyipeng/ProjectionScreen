package com.example.myapplication.utile;

public class AddressUtils {
    public static String directionType(int d) {
        if ((d >= 337 && d <= 360) || (d >= 0 && d <= 23)) {
            return "北向";
        } else if (d >= 23 && d <= 68) {
            return "东北向";
        } else if (d >= 68 && d <= 113) {
            return "东向";
        } else if (d >= 113 && d <= 158) {
            return "东南向";
        } else if (d >= 158 && d <= 203) {
            return "南向";
        } else if (d >= 203 && d <= 248) {
            return "西南向";
        } else if (d >= 248 && d <= 293) {
            return "西向";
        } else //if (d >= 293 && d <= 338) {
            return "西北向";
    }
}
