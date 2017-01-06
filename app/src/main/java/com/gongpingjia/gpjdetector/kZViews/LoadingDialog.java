package com.gongpingjia.gpjdetector.kZViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.gongpingjia.gpjdetector.R;

/**
 * 加载中Dialog
 *
 * @author xm
 */
public class LoadingDialog extends Dialog {

    private TextView tips_loading_msg;

    private String message = null;

    private Context mContext = null;


    public LoadingDialog(Context context) {
        super(context, R.style.dialogs);

        message = context.getResources().getString(R.string.msg_load_ing);
        this.mContext = context;
    }

    public LoadingDialog(Context context, String message) {
        super(context, R.style.dialogs);
        this.message = message;
        this.mContext = context;
    }

    public LoadingDialog(Context context, int theme, String message) {
        super(context, R.style.dialogs);
        this.message = message;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_tips_loading);
        setCancelable(false);
        tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);
        tips_loading_msg.setText(this.message);
    }

    public void setText(String message) {
        this.message = message;
        if (tips_loading_msg != null)
            tips_loading_msg.setText(message);

    }


    public void setText(int resId) {
        setText(getContext().getResources().getString(resId));
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.dismiss();
        return super.onTouchEvent(event);
    }


}
