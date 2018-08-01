package com.update.updatelib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.update.updatelib.MainActivity;
import com.update.updatelib.listener.HttpListener;
import com.update.updatelib.listener.HttpResponseListener;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

/**
 * Created by kim on 2018/1/18.
 */

public class InitConfig {
    private Object object = new Object();
    private boolean notShow = false;
    @SuppressLint("StaticFieldLeak")
    public static boolean isSkip = false;
    private static InitConfig instance = null;
    private InitConfig() {
    }
  public synchronized static InitConfig getInstance() {
            if (instance == null) {
                instance = new InitConfig();
            }
            return instance;
    }

    public  Class<? extends Activity> cls;
    RequestQueue mQueue;
    private Context context;
    public void initconfig(MainActivity context){
        this.context=context;
        init();
        mQueue = NoHttp.newRequestQueue(5);

    }

//
//    public  void skipAnotherActivity(MainActivity activity) {
//        if(cls!=null) {
//            Intent intent = new Intent(activity, cls);
//            activity.startActivity(intent);
//            activity.finish();
//        }
//    }
public synchronized void skipAnotherActivity(final MainActivity activity) {
    activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            if (!isSkip) {
                isSkip = true;
                Intent intent = new Intent(activity, cls);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    });
}
    private void init(){
        NoHttp.initialize(InitializationConfig.newBuilder(context)
                // 设置全局连接超时时间，单位毫秒，默认10s。
                .connectionTimeout(30 * 1000)
                // 设置全局服务器响应超时时间，单位毫秒，默认10s。
                .readTimeout(30 * 1000)
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .cacheStore(new DBCacheStore(context).setEnable(true) // 如果不使用缓存，设置setEnable(false)禁用。
                )
                // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
                .cookieStore(new DBCookieStore(context).setEnable(false) // 如果不维护cookie，设置false禁用。
                )
                // 配置网络层，URLConnectionNetworkExecutor，如果想用OkHttp：OkHttpNetworkExecutor。
                .networkExecutor(new URLConnectionNetworkExecutor())
                .build()
        );
    }




    /**
     * 发起请求
     * @param what      what.
     * @param request   请求对象
     * @param callback  回调函数
     * @param canCancel 是否能被用户取消
     * @param isLoading 实现显示加载框
     * @param <T>       想请求到的数据类型
     */
    public <T> void request(int what, Request<T> request, HttpListener<T> callback,
                            boolean canCancel, boolean isLoading) {
        request.setCancelSign(object);
        HttpResponseListener<T> httpResponseListener = new HttpResponseListener<>((MainActivity) context, request, callback, canCancel, isLoading);
        httpResponseListener.setNotShow(notShow);
        mQueue.add(what, request,httpResponseListener);
    }

    public void destroyResouce(){
        // 和声明周期绑定，退出时取消这个队列中的所有请求，当然可以在你想取消的时候取消也可以，不一定和声明周期绑定。
        mQueue.cancelBySign(object);

        // 因为回调函数持有了activity，所以退出activity时请停止队列。
        mQueue.stop();
    }

    protected void cancelAll() {
        mQueue.cancelAll();
    }

    protected void cancelBySign(Object object) {
        mQueue.cancelBySign(object);
    }

}
