package com.test.uploadhelper;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by 46786 on 2018/5/27.
 */
public class CusApplication extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        // Bugly
        Bugly.init(getApplicationContext(), "662fc8a1f2", BuildConfig.DEBUG);

        // 下载初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60 * 60 * 1000L, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 60 * 1000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static Application getApplication() {
        return application;
    }
}
