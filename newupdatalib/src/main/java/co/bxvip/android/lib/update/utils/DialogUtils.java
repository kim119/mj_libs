package co.bxvip.android.lib.update.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import co.bxvip.android.lib.update.R;


/**
 * Created by Vic Zhou on 2017/8/30.
 */

public class DialogUtils extends Dialog {

    public DialogUtils(Context context) {
        super(context);
    }

    public DialogUtils(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 设置自适应屏幕
     *
     * @return
     */
    public DialogUtils setWindow() {
        // 适应屏幕
        DisplayMetrics mDisplayMetrics = this.getContext().getResources()
                .getDisplayMetrics();
        if (mDisplayMetrics.widthPixels < mDisplayMetrics.heightPixels) {
            int paddWidth = mDisplayMetrics.widthPixels / 6;
            getWindow().setLayout(mDisplayMetrics.widthPixels - paddWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            int paddWidth = mDisplayMetrics.widthPixels / 2;
            getWindow().setLayout(mDisplayMetrics.widthPixels - paddWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return this;
    }

    public static class Builder {
        private Context context;
        private int mTheme = R.style.AlertDialogStyle;
        private String title;
        private String message;
        private String versionName;
        private Spanned spMessage;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(Context context, int theme) {
            this.context = context;
            this.mTheme = theme;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(Spanned message) {
            this.spMessage = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setVersionName(String versionName) {
            this.versionName = versionName;
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public DialogUtils create() {
            // instantiate the dialog with the custom Theme
            final DialogUtils dialog = new DialogUtils(context, mTheme);
            dialog.setContentView(R.layout.view_update_dialog);
            // set the dialog title
            ((TextView) dialog.findViewById(R.id.tv_title)).setText(title);
            if (versionName != null) {
                ((TextView) dialog.findViewById(R.id.tv_version)).setText(versionName);
            }
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) dialog.findViewById(R.id.positiveButton)).setText(positiveButtonText);
                dialog.findViewById(R.id.positiveButton)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (positiveButtonClickListener != null) {
                                    positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        });

            } else {
                dialog.findViewById(R.id.positiveButton).setVisibility(View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) dialog.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);

                ((Button) dialog.findViewById(R.id.negativeButton))
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                dialog.dismiss();
                                if (negativeButtonClickListener != null) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            }
                        });

            } else {
                // if no confirm button just set the visibility to GONE
                dialog.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }
            // set the content message
            if (spMessage != null) {
                ((TextView) dialog.findViewById(R.id.tv_message)).setText(Html.fromHtml(spMessage.toString().replace("\\n", "<br>")));

            }
            if (message != null) {
                ((TextView) dialog.findViewById(R.id.tv_message)).setText(Html.fromHtml(message.replace("\\n", "<br>")));
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((ViewGroup) dialog.findViewById(R.id.content)).removeAllViews();
                ((ViewGroup) dialog.findViewById(R.id.content)).addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            }
            dialog.setWindow();
            return dialog;
        }
    }

    /**
     * 弹出消息对话框
     *
     * @param context
     * @param title   标题
     * @param content 内容
     */
    public static void showMessageDialog(Context context, String title, String content, String PositiveButtonStr, OnClickListener PositiveListener,
                                         String NegativeButtonStr, OnClickListener NegativeListener) {

        Builder builder = new Builder(context);
        builder.setTitle(title).setMessage(Html.fromHtml(content.replace("\\n", "<br>")));

        builder.setPositiveButton(PositiveButtonStr, PositiveListener);

        builder.setNegativeButton(NegativeButtonStr, NegativeListener);
        DialogUtils dialog = builder.create();
        // 提示内容居中
        TextView message = (TextView) dialog.findViewById(R.id.tv_message);
        ViewGroup.LayoutParams params = message.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        message.setLayoutParams(params);
        message.setGravity(Gravity.CENTER);
        dialog.show();

    }

}
