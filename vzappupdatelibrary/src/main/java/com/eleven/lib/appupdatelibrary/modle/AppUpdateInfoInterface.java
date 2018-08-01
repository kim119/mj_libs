package com.eleven.lib.appupdatelibrary.modle;

/**
 * Created by Vic Zhou on 2017/8/30.
 * app更新接口类
 * "apkEnv":1,// 环境：1：正式2：测试3：开发
 * "versionCode":101,
 * "versionName":"1.0.1",
 * "apkUpdateType":1,// 1：强制更新，2：普通更新,3:ohter
 * "apkUrl":"http://uploads.bxvip588.com/...",
 * "apkDesc":"这个版本跟新了，修复了xxxbug",
 * "apkMd5":"43CCA4B3DE2097B9558EFEFD0ECC3588"
 */

public interface AppUpdateInfoInterface {
//    private int apkEnv;
//    private int versionCode;
//    private int apkUpdateType;
//    private String versionName;
//    private String apkUrl;
//    private String apkDesc;
//    private String apkMd5;

    /**
     * 获取apk的环境 // 环境：1：正式2：测试3：开发
     */
    int getApkEnv();

    /**
     * 设置apk的环境 // 环境：1：正式2：测试3：开发
     */
    void setApkEnv(int apkEnv);

    /**
     * 获取apk的versionCode
     */
    int getVersionCode();

    /**
     * 设置apk的versionCode
     */
    void setVersionCode(int versionCode);

    /**
     * 获取apk更新的类型 // 1：强制更新，2：普通更新,3:ohter
     */
    int getApkUpdateType();

    /**
     * 设置apk的更新类型 // 1：强制更新，2：普通更新,3:ohter
     */
    void setApkUpdateType(int apkUpdateType);

    /**
     * 获取apk的versionName
     */
    String getVersionName();

    /**
     * 设置apk的versionName
     */
    void setVersionName(String versionName);

    /**
     * 获取apk的更新的地址
     */
    String getApkUrl();

    /**
     * 设置apk的apk更新的地址
     */
    void setApkUrl(String apkUrl);

    /**
     * 获取apk更新的描述
     */
    String getApkDesc();

    /**
     * 设置apk更新的描述
     */
    void setApkDesc(String apkDesc);

    /**
     * 获取aok的更新md5
     */
    String getApkMd5();

    /**
     * 设置apk包的MD5值
     */
    void setApkMd5(String apkMd5);

    String getApplicationId();

    void setApplicationId(String applicationId);
}
