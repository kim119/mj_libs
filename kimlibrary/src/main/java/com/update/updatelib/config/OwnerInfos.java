package com.update.updatelib.config;

import com.update.updatelib.utils.HttpUtils;

/**
 * Created by kim on 2018/1/26.
 */

public class OwnerInfos {
    private static OwnerInfos instance;
    public static OwnerInfos getInstance() {
        synchronized (HttpUtils.class) {
            if (OwnerInfos.instance == null) {
                OwnerInfos.instance = new OwnerInfos();
            }
        }
        return OwnerInfos.instance;
    }


    public  String owerNumber;
    public  String owerWebsite;
    public  String downoPagerUrl;
    public String updateUrl;
}
