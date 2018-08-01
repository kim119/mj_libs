package com.eleven.lib.appupdatelibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.eleven.lib.appupdatelibrary.modle.AppUpdateInfoInterface;
import com.eleven.lib.appupdatelibrary.utils.DialogUtils;
import com.eleven.lib.net.OkHttpUtils;
import com.eleven.lib.net.callback.BaseCallback;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Vic Zhou on 2017/8/30.
 * app 更新工具类
 */

public class AppUpdateUtils {
    public static final String TAG = "ContentValues";

    public static boolean doUpdate(Context mContext, String result, final String appJsonElement, final Class<? extends AppUpdateInfoInterface> clazz) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            AppUpdateInfoInterface data = new Gson().fromJson(jsonObject.getString(appJsonElement), clazz);
            if (data != null) {
                if (data.getApkUpdateType() == 1) {// 强制更新
                    forceUpdate(mContext, data);
                } else {
                    offerUpdate(mContext, data);
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查app更新
     *
     * @param mContext       上下文对象
     * @param checkUrl       检查更新apk的服务器地址
     * @param postParam      post请求参数
     * @param appJsonElement AppUpdateInfoInterface 实现类的对象的json节点
     * @param clazz          AppUpdateInfoInterface 实现类
     */
    public static void doCheckUpdate(final Context mContext, String checkUrl, Map<String, String> postParam, final String appJsonElement, final Class<? extends AppUpdateInfoInterface> clazz) {
        System.out.println("********************" + postParam.toString());
        OkHttpUtils.getInstance().post(checkUrl, postParam, new BaseCallback<String>() {
            @Override
            public void onRequestBefore() {

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onSuccess(Response response, String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    AppUpdateInfoInterface data = new Gson().fromJson(jsonObject.getString(appJsonElement), clazz);
                    if (data != null && !TextUtils.isEmpty(data.getApkUrl())) {
                        if (data.getApkUpdateType() == 1) {// 强制更新
                            forceUpdate(mContext, data);
                        } else {
                            offerUpdate(mContext, data);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response, int errorCode, Exception e) {

            }
        });
    }

    /**
     * 强制更新，否则退出
     *
     * @param appInfo
     */
    public static void forceUpdate(final Context context, final AppUpdateInfoInterface appInfo) {
//        new Download(context, appInfo).run();
        new Thread(new DownloadRunnable(context, appInfo)).start();
    }

    /**
     * 建议更新
     *
     * @param appInfo
     */
    public static void offerUpdate(final Context context, final AppUpdateInfoInterface appInfo) {
        new DialogUtils.Builder(context).setTitle("版本更新").setMessage(appInfo.getApkDesc())
                .setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                forceUpdate(context, appInfo);
            }
        }).setVersionName(appInfo.getVersionName()).create().show();
    }
}
