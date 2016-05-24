package com.example.gulshan.youtubewebview;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gulshan on 13/5/16.
 */
public class MyUtils {
    public static String getCurrentDateAsFormat(String pFormat) {
        return new SimpleDateFormat(pFormat).format(new Date());
    }
}
