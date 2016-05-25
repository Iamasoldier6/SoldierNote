package com.iamasoldier6.soldiernote.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.iamasoldier6.soldiernote.R;

/**
 * Created by Iamasoldier6 on 5/24/16.
 */
public class SplashActivity extends Activity {

    private static final long DELAY_MILLIS = 1 * 1000; // 暂先改 1 秒
    private static final int GOTO_MAIN_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, DELAY_MILLIS);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

}
