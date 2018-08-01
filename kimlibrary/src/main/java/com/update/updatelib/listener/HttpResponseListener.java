package com.update.updatelib.listener;

import android.app.Activity;
import android.content.DialogInterface;

import com.update.updatelib.R;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;


/**
 * Created in Nov 4, 2015 12:02:55 PM.
 *
 * @author Yan Zhenjie.
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

    private Activity mActivity;

    /**
     * Request.
     */
    private Request<?> mRequest;
    /**
     * 结果回调.
     */
    private HttpListener<T> callback;


    private boolean notShow;

    public void setNotShow(boolean notShow) {
        this.notShow = notShow;
    }

    /**
     * @param activity     context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     * @param canCancel    是否允许用户取消请求.
     * @param isLoading    是否显示dialog.
     */
    private WaitDialog mWaitDialog;
    public HttpResponseListener(Activity activity, Request<?> request, HttpListener<T> httpCallback, boolean
            canCancel, boolean isLoading) {
        this.mActivity = activity;
        this.mRequest = request;
        if (activity != null && isLoading) {
            mWaitDialog = new WaitDialog(activity, R.style.shareDialog_style);
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel();
                }
            });
        }
        this.callback = httpCallback;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
  

    /**
     * 结束请求, 这里关闭dialog.
     */


    @Override
    public void onStart(int what) {

    }

    /**
     * 成功回调.统一处理code。由于接口code响应不同。所以没做处理
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (callback != null) {
            // 这里判断一下http响应码，这个响应码问下你们的服务端你们的状态有几种，一般是200成功。
            // w3c标准http响应码：http://www.w3school.com.cn/tags/html_ref_httpmessages.asp
            try {
                callback.onSucceed(what, response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
}

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, Response<T> response) {
        if (notShow)
            return;
        Exception exception = response.getException();
//        if (exception instanceof NetworkError) {// 网络不好
//            Toast.show(mActivity, R .string.error_please_check_network);
//        } else if (exception instanceof TimeoutError) {// 请求超时
//            Toast.show(mActivity, R.string.error_timeout);
//        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
//            Toast.show(mActivity, R.string.error_timeout);
//        } else if (exception instanceof URLError) {// URL是错的
//            Toast.show(mActivity, R.string.error_url_error);
//        } else if (exception instanceof NotFoundCacheError) {
//            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
//            // 没有缓存一般不提示用户，如果需要随你。
//        } else {
////            Toast.show(mActivity, R.string.error_timeout);
//        }
//        Logger.e("错误：" + exception.getMessage());
        if (callback != null)
            callback.onFailed(what, response);
    }

    @Override
    public void onFinish(int what) {

    }

}
