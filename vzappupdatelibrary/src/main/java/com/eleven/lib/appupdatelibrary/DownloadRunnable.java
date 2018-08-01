package com.eleven.lib.appupdatelibrary;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eleven.lib.appupdatelibrary.modle.AppUpdateInfoInterface;
import com.eleven.lib.appupdatelibrary.utils.DialogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import static com.eleven.lib.appupdatelibrary.AppUpdateUtils.TAG;

/**
 * @author Vic Zhou @link bxvipsky@gamil.com
 * @project: PHPKotlin com.eleven.lib.appupdatelibrary.DownloadRunnable
 * @description:
 * @date 2017/10/15 14:59
 */

public class DownloadRunnable implements Runnable, View.OnClickListener {
    private static final int ERROR = -1;
    private static final int DIALOG_SHOW = 0;
    private static final int DIALOG_DISMISS = 1;
    private static final int DOWNLOADING = 2;
    private static final int DOWNLOAD_FINISH = 3;

    private Context mContext;
    private AppUpdateInfoInterface mAppInfo; //升级信息对象
    private int mUpdateType;

    private String mErrorStr = "";
    private int mProgress = 0; //记录进度条数量
    private static boolean mCancelUpdate = false; //是否取消更新
    private DialogUtils mProgressDialog;
    private ProgressBar progressBar;
    private TextView tvPercent;
    private TextView tvDescribe;
    private Handler mainHandler;

    /**
     * Notification管理
     */
    private static NotificationManager mNotificationManager;
    private static int notifyId = 102;
    private static NotificationCompat.Builder mBuilder;
    private static boolean mIsBackgroudDownload = false;//是否在后台下载


    public DownloadRunnable(Context context, AppUpdateInfoInterface info) {
        if (info == null)
            throw new NullPointerException("info == null");
        mContext = context;
        mAppInfo = info;
        mUpdateType = info.getApkUpdateType();
        mainHandler = new Handler(Looper.getMainLooper());
    }


    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public void run() {
        try {
            // 判断SD卡是否存在，并且是否具有读写权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
                mErrorStr = "没有SD卡的读写权限！";
                mHandler.sendEmptyMessage(ERROR);
                return;
            }
            mHandler.sendEmptyMessage(DIALOG_SHOW);

            if (!TextUtils.isEmpty(mAppInfo.getApkUrl()) && !mAppInfo.getApkUrl().startsWith("http://")) {
                mAppInfo.setApkUrl("http://" + mAppInfo.getApkUrl());
            }
            URL url = new URL(mAppInfo.getApkUrl());
            // 创建连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setConnectTimeout(60000);
            conn.connect();
            // 获取文件大小
            int length = conn.getContentLength();
            if (length == -1) {
                mErrorStr = "未找不到下载文件！";
                mHandler.sendEmptyMessage(ERROR);
                return;
            }
            // 创建输入流
            InputStream is = conn.getInputStream();
            // 设置APK保存路径
            String appPath = mContext.getExternalCacheDir() + File.separator + "TEMP.apk";
            File file = new File(appPath);
            // 文件已存在则删除，否则创建父目录
            boolean a = file.exists() ? file.delete() : file.getParentFile().mkdirs();

            Log.i(TAG, "开始下载...");
            FileOutputStream fos = new FileOutputStream(file);
            int count = 0;
            byte buf[] = new byte[1024 * 4];
            do {
                int numread = is.read(buf);
                if (numread <= 0) {
                    break;
                }
                // 写入文件
                fos.write(buf, 0, numread);
                count += numread;
                // 更新进度
                mProgress = (int) (((float) count / length) * 100);
                mHandler.sendEmptyMessage(DOWNLOADING);
                Log.i(TAG, "正在下载更新，已完成：" + mProgress + "%");
            } while (!mCancelUpdate);// 点击取消就停止下载
            fos.close();
            is.close();

            // 关闭对话框
            mHandler.sendEmptyMessage(DIALOG_DISMISS);
            if (mCancelUpdate == false) {
                //安装
                Log.i(TAG, "下载完成，installApk：" + appPath);
//                    if (mAppInfo.getApkMd5() != null) {
//                        if (mAppInfo.getApkMd5().toLowerCase().equals(getFileMD5(new File(appPath)).toLowerCase())) {
                installApk(appPath);
//                        } else {
//                            Log.i(TAG, "Apk已经被篡改！：" + appPath);
//                        }
//                    }
                //退出应用
                exit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrorStr = "升级失败！";
            mHandler.sendEmptyMessage(ERROR);
        }
    }


    /**
     * 下载
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case DIALOG_SHOW:
                    // 显示下载对话框
                    showDialog();
                    break;
                case DIALOG_DISMISS:
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                    break;
                case DOWNLOADING:
                    // 设置进度条位置
                    if (mIsBackgroudDownload) {
                        //后台下载
                        mBuilder.setContentTitle("下载进度，已完成：" + mProgress + "%").setProgress(100, mProgress, false); // 这个方法是显示进度条
                        mNotificationManager.notify(notifyId, mBuilder.build());
                    } else {
                        progressBar.setProgress(mProgress);
                        tvPercent.setText("正在下载更新，已完成：" + mProgress + "%");
                    }
                    break;
                case DOWNLOAD_FINISH:
                    // 设置进度条位置
                    exit();
                    break;
                case ERROR:
                    sendEmptyMessage(DIALOG_DISMISS);
                    Toast.makeText(mContext, "App 更新失败！", Toast.LENGTH_SHORT).show();
                    //取消通知栏通知
                    if (mNotificationManager != null)
                        mNotificationManager.cancel(notifyId);
                    break;
            }
        }
    };

    /**
     * 显示下载进度对话框
     */

    private void showDialog() {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_update_apk_progress, null);
        view.findViewById(R.id.btnCancel).setOnClickListener(this); //取消事件
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tvPercent = (TextView) view.findViewById(R.id.tvPercent);
        tvDescribe = (TextView) view.findViewById(R.id.tvDescribe);
        ((TextView) view.findViewById(R.id.tv_version)).setText("V" + mAppInfo.getVersionName());
        if (mAppInfo.getApkDesc() != null) {
            tvDescribe.setText(Html.fromHtml(mAppInfo.getApkDesc().replace("\\n", "<br>")));
        } else {
            tvPercent.setText("");
        }
        mProgressDialog = new DialogUtils(mContext, R.style.AlertDialogStyle);
        mProgressDialog.setContentView(view);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setWindow();
        mProgressDialog.show();
    }

    @Override
    public void onClick(View v) {

        //点击“取消”时，根据升级类型给出不同提示

        //强制下载
        if (mUpdateType == 1) {
            DialogUtils.showMessageDialog(mContext, "温馨提示", "取消更新将退出应用，是否退出？", "退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCancelUpdate = true;
                    mProgressDialog.dismiss();
                    exit();
                }
            }, "关闭", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        //建议下载
        else if (mUpdateType == 2) {
            DialogUtils.showMessageDialog(mContext, "下载提示", "1.请选择后台更新<br>或者放弃更新？", "后台更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mProgressDialog.dismiss();
                    mIsBackgroudDownload = true;
                    initNotify(); //显示在通知栏
                }
            }, "取消更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCancelUpdate = true;
                    dialog.dismiss();
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    /**
     * 安装APK文件
     */

    private void installApk(String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            return;
        }

        mContext.startActivity(getApkFileIntent(apkFile));
    }

    private void exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    //Android获取一个用于打开APK文件的intent
    private Intent getApkFileIntent(File appFile) {
        try {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri fileUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileProvider", appFile);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化通知栏
     */
    private void initNotify() {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent dismissIntent = new Intent("action_dismiss", null, mContext, NotificationReceiver.class);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(mContext, 0, dismissIntent, PendingIntent.FLAG_ONE_SHOT);

        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setWhen(System.currentTimeMillis()) // 通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setAutoCancel(true)//设置当用户单击面板就可以让通知将自动取消
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_LIGHTS)// 闪灯
                //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),))
                .setTicker("下载应用") // 通知首次出现在通知栏，带上升动画效果的
                .setSmallIcon(R.mipmap.logo)
                .setProgress(100, 0, false) // 这个方法是显示进度条
                .setContentText("点击可取消下载")
                .setContentIntent(dismissPendingIntent);

    }


    public static class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "NotificationReceiver.onReceive");
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notifyId);
            mCancelUpdate = true;
        }
    }

//    public class UninstallBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            if(Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())){
//                Toast.makeText(context, "有应用被添加", Toast.LENGTH_LONG).show();
//                uninstallApp();
//            }
//            else  if(Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())){
//                Toast.makeText(context, "有应用被删除", Toast.LENGTH_LONG).show();
//            }
//             /*   else  if(Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())){
//                    Toast.makeText(context, "有应用被改变", Toast.LENGTH_LONG).show();
//            }*/
//            else  if(Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())){
//                uninstallApp();
//                Toast.makeText(context, "有应用被替换", Toast.LENGTH_LONG).show();
//            }
//               /* else  if(Intent.ACTION_PACKAGE_RESTARTED.equals(intent.getAction())){
//                    Toast.makeText(context, "有应用被重启", Toast.LENGTH_LONG).show();
//            }*/
//              /*  else  if(Intent.ACTION_PACKAGE_INSTALL.equals(intent.getAction())){
//                    Toast.makeText(context, "有应用被安装", Toast.LENGTH_LONG).show();
//            }*/
//
//        }
//
//    }

    private void uninstallApp() {
        if (!TextUtils.isEmpty(mAppInfo.getApplicationId()) && !mAppInfo.getApplicationId().equals(mContext.getPackageName())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
            Intent uninstall_intent = new Intent();
            uninstall_intent.setAction(Intent.ACTION_DELETE);
            uninstall_intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            mContext.startActivity(uninstall_intent);
        }
    }


    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    private static void setIntentUrl(Context context, Intent intent, String filePath, String dataAndType) {
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File file = new File(filePath);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            intent.setDataAndType(contentUri, dataAndType);
        } else {
            intent.setDataAndType(Uri.fromFile(new File(filePath)), dataAndType);
        }
    }
}
