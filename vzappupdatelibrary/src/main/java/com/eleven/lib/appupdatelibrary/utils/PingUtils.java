package com.eleven.lib.appupdatelibrary.utils;


import com.eleven.lib.net.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 39862 on 2017/9/29.
 */

public class PingUtils {
    private final static String TAG = "vz ping utuls ! ";

    public static void ping(String host, int time, PingCallBack<Long> callBack) {
        String delay = new String();
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("/system/bin/ping -c 4 " + host);
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str = new String();
            while ((str = buf.readLine()) != null) {
                if (str.contains("avg")) {
                    int i = str.indexOf("/", 20);
                    int j = str.indexOf(".", i);
                    System.out.println("延迟:" + str.substring(i + 1, j));
                    delay = str.substring(i + 1, j);
                    System.out.println();
                }
            }
            if (delay.equals("")) {
                callBack.back((long) 1000);
                log(delay);
            } else {
                callBack.back(Long.parseLong(delay));
            }
        } catch (IOException e) {
            callBack.back((long) 1000);
            e.printStackTrace();
        }
    }

    public static void ping(List<String> hosts, int time, PingCallBack<Map<String, Long>> callBack) {
        final Map<String, Long> res = new HashMap();
        for (final String key : hosts) {
            ping(key, time, new PingCallBack<Long>() {
                @Override
                public void back(Long aLong) {
                    res.put(key, aLong);
                }
            });
        }
        callBack.back(res);
    }


    private static void log(String string) {
        if (BuildConfig.DEBUG) {
            System.out.println(TAG + string);
        }
    }

    /**
     * ping回掉
     *
     * @param <T> speed or speed list
     */
    public interface PingCallBack<T> {
        void back(T t);
    }
}
