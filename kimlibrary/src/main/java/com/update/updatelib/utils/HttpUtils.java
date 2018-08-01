package com.update.updatelib.utils;


import android.util.Log;
import com.update.updatelib.MainActivity;
import com.update.updatelib.R;
import com.update.updatelib.config.Contants;
import com.update.updatelib.config.OwnerInfos;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * Created by kim
 * on 2018/1/12.
 */
public class HttpUtils {
   //public boolean isSwtichLine = true;//是否切换线路x／／
    private static HttpUtils instance;
    private InitConfig initConfigInstance;
    private MainActivity mainActivityInstance;
    private HttpUtils() {
        initConfigInstance = InitConfig.getInstance();
    }
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                0L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public static HttpUtils getInstance() {
        synchronized (HttpUtils.class) {
            if (HttpUtils.instance == null) {
                HttpUtils.instance = new HttpUtils();
            }
        }
        return HttpUtils.instance;
    }

public boolean[] isAllCheck = new boolean[]{false, false, false, false};
public void getDataByAsync(int position, String address, final MainActivity context) {
    //子线程
    mainActivityInstance = context;
    boolean netWorkAvailable = isPingNetWorkAvailable(address);
    isAllCheck[position] = true;
    if (netWorkAvailable) {
        askNet(context.getPackageName(), address,context);
    } else {
        if (isAllCheck[0]&&isAllCheck[1]&&isAllCheck[2]&&isAllCheck[3]){

            initConfigInstance.skipAnotherActivity(context);
        }
    }
}

    private boolean isPingNetWorkAvailable(final String address) {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process pingProcess = runtime.exec("/system/bin/ping -c 1 " + address);
            InputStreamReader isr = new InputStreamReader(pingProcess.getInputStream());
            BufferedReader buf = new BufferedReader(isr);
            if (buf.readLine() == null) {
                //TODO 没网
                return false;
            } else {

            }
            buf.close();
            isr.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            runtime.gc();
        }
        return false;
    }

    private void askNet(String appid, final String baseUrl, final MainActivity context) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + baseUrl + "/index.php/appApi/request/ac/getAppData/appid/" + appid + "/key/d20a1bf73c288b4ad4ddc8eb3fc59274704a0495/client/2")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // startActivity(context);

                if(initConfigInstance != null) {
                    initConfigInstance.skipAnotherActivity(context);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String data = response.body().string();
                   parseData(mainActivityInstance, data, baseUrl);


                }
            }
        });
    }


    //数据解析
    private void parseData(MainActivity context, String data,String baseUrl) {
        try {
            JSONObject person = new JSONObject(data);
            if (person.getInt("msg") != 0) {
                if(initConfigInstance != null) {
                    initConfigInstance.skipAnotherActivity(context);
                }
                return;
            }
            String en_data = person.getString("data");
            String de_data = decode(en_data, "bxvip588");
            JSONObject de_jobject = new JSONObject(de_data);
            String status = de_jobject.getString("status");
            //根据网络进行切换
            switch (status) {
                case Contants.PROJECT_ANDROID:
                    Log.d("status", "parseData: " + status);
                    if (initConfigInstance != null) {
                        initConfigInstance.skipAnotherActivity(context);
                    }

                    break;
                case Contants.PROJECT_MAIN:
                   // EventBus.getDefault().post(OwnerInfos.getInstance());
                   UpdateUtils.checkVersion(context,
                           "http://"+baseUrl,
                         context.getResources().getString(R.string.versionCode),
                           context.getResources().getString(R.string.appVersionName),
                          OwnerInfos.getInstance().owerWebsite,
                           OwnerInfos.getInstance().owerNumber,
                           OwnerInfos.getInstance().downoPagerUrl);
                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
            // startActivity(context);
        }

    }

    //解码url
    private static String decode(String target, String pass) {
        String result = "";
        //存储ascii码数组
        int[] asciiCode = new int[pass.length()];
        //获取密码的ascii码
        for (int i = 0; i < pass.length(); i++) {
            asciiCode[i] = ((int) pass.charAt(i));
        }
        //获取两个十六进制，并转换为十进制
        for (int i = 0; i < target.length(); i += 2) {
            try {

            } catch (Exception e) {
                return null;
            }
            int hex = Integer.parseInt(target.substring(i, i + 2), 16);
            int inte = Integer.parseInt(String.valueOf(hex), 10);
            for (int j = pass.length(); j > 0; j--) {
                int val = inte - (asciiCode[j - 1]) * j;
                if (val < 0) {
                    inte = 256 - (Math.abs(val) % 256);
                } else {
                    inte = val % 256;
                }
            }
            result += (char) inte;//转换成ascii，并拼接到字符串中
        }
        return result;
    }

}
