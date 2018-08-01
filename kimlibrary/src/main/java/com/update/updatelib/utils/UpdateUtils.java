package com.update.updatelib.utils;


import com.alibaba.fastjson.JSONObject;
import com.update.updatelib.MainActivity;
import com.update.updatelib.config.VersionCodeBean;
import com.update.updatelib.listener.FastJsonRequest;
import com.update.updatelib.listener.HttpListener;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import java.util.HashMap;
import java.util.Map;

import co.bxvip.android.lib.update.AppUpdateInfoInterface;
import co.bxvip.android.lib.update.UpdateApkUtils;


public class UpdateUtils {
    public static boolean isUploading = false;
   public  synchronized  static  void checkVersion(final MainActivity context, String updateUrl, final String versionCode, final String versionName, String website, String number, final String applicationId) {
       if(isUploading){
           return;
       }
       isUploading=true;
        Map<String, Object> postPrarm = new HashMap<>();
        postPrarm.put("apkEnv", "1");
        postPrarm.put("versionCode", versionCode);
        postPrarm.put("versionName", versionName);
        postPrarm.put("getData", "1");
        postPrarm.put("website", website);
        postPrarm.put("number", number);
        postPrarm.put("applicationId", applicationId);
        FastJsonRequest request = new FastJsonRequest(updateUrl, RequestMethod.POST);
        request.set(postPrarm);
       InitConfig.getInstance().request(0x88888, request, new HttpListener<JSONObject>() {
            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                JSONObject jsonObject = response.get();
                final VersionCodeBean data = JSONObject.parseObject(jsonObject.getString("data"), VersionCodeBean.class);
                AppUpdateInfoInterface infoInterface = new AppUpdateInfoInterface() {
                    @Override
                    public int getApkEnv() {
                        return 1;
                    }

                    @Override
                    public void setApkEnv(int i) {

                    }

                    @Override
                    public int getVersionCode() {
                        return Integer.valueOf(versionCode);
                    }

                    @Override
                    public void setVersionCode(int i) {

                    }

                    @Override
                    public int getApkUpdateType() {
                        return Integer.valueOf(data.getType());
                    }

                    @Override
                    public void setApkUpdateType(int i) {

                    }

                    @Override
                    public String getVersionName() {
                        return versionName;
                    }

                    @Override
                    public void setVersionName(String s) {

                    }

                    @Override
                    public String getApkUrl() {
                        return data.getUrl();
                    }

                    @Override
                    public void setApkUrl(String s) {

                    }

                    @Override
                    public String getApkDesc() {
                        return data.getDesc();
                    }

                    @Override
                    public void setApkDesc(String s) {

                    }

                    @Override
                    public String getApkMd5() {
                        return data.getMd5_key();
                    }

                    @Override
                    public void setApkMd5(String s) {
                    }

                    @Override
                    public String getApplicationId() {
                        return applicationId;
                    }

                    @Override
                    public void setApplicationId(String s) {

                    }
                };
                UpdateApkUtils.doDownload(context, infoInterface);
            }

            @Override
            public void onFailed(int what, Response<JSONObject> response) {

            }
        }, false, false);
  }




}
