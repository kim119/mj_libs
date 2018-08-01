package com.update.updatelib.utils;
import android.content.Context;
import android.util.Log;

import com.update.updatelib.MainActivity;
import com.update.updatelib.R;
import com.update.updatelib.config.Contants;
import com.update.updatelib.config.OwnerInfos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
/**
 * Created by kim on
 * 2018/2/26.
 */
public class PingNet {
    ExecutorService executorService;
    private static PingNet instance;
    public static PingNet getInstance() {
        synchronized (HttpUtils.class) {
            if (PingNet.instance == null) {
                PingNet.instance = new PingNet();
            }
        }
        return PingNet.instance;
    }
    public boolean[] isAllCheck = new boolean[]{false, false, false, false};
    public void pingNetAddress(MainActivity  context,final String address){
                    boolean netWorkAvailable = isPingNetWorkAvailable(address);
                    synchronized (PingNet.class) {
                        if (netWorkAvailable) {
                            if(OwnerInfos.getInstance().updateUrl==null){
                                OwnerInfos.getInstance().updateUrl=address;
                                Contants.i++;
                                Log.d("PingNet",Contants.i+"");
                                UpdateUtils.checkVersion(context,
                                       "http://"+OwnerInfos.getInstance().updateUrl,
                                        context.getResources().getString(R.string.versionCode),
                                        context.getResources().getString(R.string.appVersionName),
                                        OwnerInfos.getInstance().owerWebsite,
                                        OwnerInfos.getInstance().owerNumber,
                                        OwnerInfos.getInstance().downoPagerUrl);
                            }

                        } else {
                            if (isAllCheck[0] && isAllCheck[1] && isAllCheck[2] && isAllCheck[3]) {
                                // initConfigInstance.skipAnotherActivity(context);
                            }
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







}
