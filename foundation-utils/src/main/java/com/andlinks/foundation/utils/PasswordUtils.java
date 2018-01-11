package com.andlinks.foundation.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by 王凯斌 on 2017/6/7.
 */
public class PasswordUtils {

    public static String hashPassword(String password, String salt) {

        return DigestUtils.md5Hex(password + salt);
    }

    public static Boolean verifyPassword(String password, String salt, String hashedPassword) {

        if (password == null || salt == null || hashedPassword == null) {
            return false;
        }
        return DigestUtils.md5Hex(password + salt).equals(hashedPassword);
    }
}
