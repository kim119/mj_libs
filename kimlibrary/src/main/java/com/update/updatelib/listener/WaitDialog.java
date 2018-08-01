/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.update.updatelib.listener;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.update.updatelib.R;


/**
 * Created in Oct 23, 2015 1:19:04 PM.
 *
 * @author Yan Zhenjie.
 */
public class WaitDialog extends Dialog {

    public ImageView iv;


    public WaitDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        RelativeLayout.LayoutParams iv_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv = new ImageView(context);
        iv_params.setMargins(20,20,20,20);
            iv.setImageResource(R.mipmap.icon_loading_1);
        iv.setBackgroundResource(R.mipmap.icon_loading_2);
        iv.setLayoutParams(iv_params);
        setContentView(iv);
    }

    public WaitDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
        RelativeLayout.LayoutParams iv_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv = new ImageView(context);
        iv.setImageResource(R.mipmap.icon_loading_1);
        iv.setBackgroundResource(R.mipmap.icon_loading_2);
        iv.setLayoutParams(iv_params);
        setContentView(iv);
    }

}
