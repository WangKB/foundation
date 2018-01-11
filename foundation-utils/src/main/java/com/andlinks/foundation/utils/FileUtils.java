package com.andlinks.foundation.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by 王凯斌 on 2017/8/30.
 */
public class FileUtils {

    public static void base64ToFile(String base64,String filePath){
        byte[] data = Base64.decodeBase64(base64);
        try (OutputStream stream = new FileOutputStream(filePath)) {
            stream.write(data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
