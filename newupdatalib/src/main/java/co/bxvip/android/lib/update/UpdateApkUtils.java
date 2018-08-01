package co.bxvip.android.lib.update;

import android.content.Context;
import android.content.DialogInterface;

import co.bxvip.android.lib.update.utils.DialogUtils;

/**
 * Created by User on 2018/1/13.
 */

public class UpdateApkUtils {
    public static final String TAG = "UpdateApkUtils";
    /**
     * apk 更新工具类
     *
     * @param mContext
     * @param appInfo
     */
    public static void doDownload(Context mContext, AppUpdateInfoInterface appInfo) {
        if (appInfo.getApkUpdateType() == 1) {// 强制更新
            forceUpdate(mContext, appInfo);
        } else {
            offerUpdate(mContext, appInfo);
        }
    }

    /**
     * 强制更新，否则退出
     *
     * @param appInfo
     */
    private static void forceUpdate(final Context context, final AppUpdateInfoInterface appInfo) {
        new Thread(new DownloadRunnable(context, appInfo)).start();
    }

    /**
     * 建议更新
     *
     * @param appInfo
     */
    private static void offerUpdate(final Context context, final AppUpdateInfoInterface appInfo) {
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
