package com.example.demo.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;


@UtilityClass
public class Validator {
    public static boolean isEmpty(String str) {
        return str == null || str.isBlank();
    }

    public static boolean isCollectionEmpty(Collection<?> col) {
        return col == null || col.isEmpty();
    }

    public static boolean isConsistOfOnlyDigits(String str) {
        return str != null && str.matches("[0-9]+");
    }

    public static boolean isLongValid(Long value) {
        return value != null && value > 0;
    }

    public static boolean isEmailValid(String email) {
        String regex = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Zа-яА-Я\\-0-9]+\\.)+[a-zA-Zа-яА-Я]{2,}))$";
        return email != null && email.matches(regex);
    }

    public static boolean isIntegerValid(Integer value) { return value == null || value < 0; }
}
