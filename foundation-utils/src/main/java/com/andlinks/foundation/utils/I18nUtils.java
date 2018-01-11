package com.andlinks.foundation.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by 王凯斌 on 2017/4/26.
 */
public class I18nUtils {

    public static String getMessage(String message, String language, String country) {
        return getBundle(language, country).getString(message);
    }

    public static ResourceBundle getBundle(String language, String country) {
        return ResourceBundle.getBundle("Messages", new Locale(language, country));
    }

}
