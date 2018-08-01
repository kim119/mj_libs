package com.update.updatelib;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import com.update.updatelib.config.Contants;
import com.update.updatelib.config.OwnerInfos;
import com.update.updatelib.utils.HttpUtils;
import com.update.updatelib.utils.InitConfig;
import com.update.updatelib.utils.IsJudgeInstall;
import com.update.updatelib.utils.IsNetWorkAvailable;
import com.update.updatelib.utils.StringUtils;
import com.update.updatelib.utils.UpdateUtils;


import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 项目引导页
 */
public class MainActivity extends AppCompatActivity {
    /**
     * 用来标记取消
     */
    //网络配置
    @SuppressLint("StaticFieldLeak")
    private Handler mHandler = new Handler();
    HttpUtils instance;
    ImageView imageView;


    //主包参数配置对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitConfig.isSkip = false;
        UpdateUtils.isUploading = false;
      //  EventBus.getDefault().register(this);
        instance = HttpUtils.getInstance();
        InitConfig.getInstance().initconfig(MainActivity.this);
        int imageID = getIntent().getIntExtra("imageID", 0);
        setContentView(R.layout.activity_welcom);
        //初始化背景

        initBackground(imageID);
        //获取assets下的资源文件并进行保存
        initOwerInfos();
        //判断是否有网
        boolean isNetworkAvailable = IsNetWorkAvailable.isNetworkAvailable(MainActivity.this);
        boolean appisInstalled = IsJudgeInstall.isAppInstalled(MainActivity.this, OwnerInfos.getInstance().downoPagerUrl);
        exeAskNet(isNetworkAvailable, appisInstalled);


    }

    private void initBackground(int imageID) {
        if(imageID==0){
            return;
        }
        imageView = (ImageView) findViewById(R.id.iv_bg);
        imageView.setBackground(getResources().getDrawable(imageID));

    }

    /**
     * 初始化业主信息
     */
    private void initOwerInfos() {
        Map<Integer, String> integerStringMap = StringUtils.readAssetsTxt(MainActivity.this, Contants.UPDATEFILENAME);
        int type = getIntent().getIntExtra("type", 0);
        String info = integerStringMap.get(type);
        String[] infos = info.split("=");
        OwnerInfos.getInstance().owerWebsite = infos[0];
        OwnerInfos.getInstance().owerNumber = infos[1];
        OwnerInfos.getInstance().downoPagerUrl = infos[2];
    }

    //判断是否有网络，跳转处理逻辑
    private void exeAskNet(boolean isNetworkAvailable, boolean appisInstalled) {
        if (appisInstalled) {
            openApp();
        } else {
            if (isNetworkAvailable) {
                getNetData();
                //  mHandler.post(runnable);
            } else {
                Toast.makeText(MainActivity.this, "请打开网络", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void launchMainActivity(Activity context, Class<? extends Activity> cls, int type, int imageID) {
        InitConfig.getInstance().cls = cls;
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra("type", type);
        starter.putExtra("imageID", imageID);
        context.startActivity(starter);
        context.finish();
    }


    @Override
    protected void onDestroy() {
        InitConfig.getInstance().destroyResouce();


//       if(null!=runnable)
//          mHandler.removeCallbacks(runnable);-
        if (executorService != null) {
            executorService.shutdownNow();
        }
        super.onDestroy();
    }


    private void openApp() {
        PackageManager manager = this.getPackageManager();
        Intent openApp = manager.getLaunchIntentForPackage(OwnerInfos.getInstance().downoPagerUrl);
        this.startActivity(openApp);
    }

    ExecutorService executorService;

    //获取网络数据
    private void getNetData() {
        try {
            executorService = HttpUtils.newCachedThreadPool();
            for (int i = 0; i < Contants.ips.length; i++) {
                final int finalI = i;
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        instance.getDataByAsync(finalI, Contants.ips[finalI], MainActivity.this);
                    }
                });
            }
        } catch (Exception e) {

        }
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void pingNet(OwnerInfos infos){
//        try {
//        executorService = HttpUtils.newCachedThreadPool();
//        for (int i = 0; i < Contants.ips.length; i++) {
//            final int finalI = i;
//            executorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    PingNet.getInstance().pingNetAddress(MainActivity.this, Contants.ips[finalI]);
//                }
//            });
//        }
//    } catch (Exception e) {
//
//    }
//
//
//    }
}
