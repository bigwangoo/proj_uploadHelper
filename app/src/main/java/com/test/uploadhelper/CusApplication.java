package com.test.uploadhelper;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.test.uploadhelper.common.service.SoapService;
import com.test.uploadhelper.utils.SharedPrefHelper;
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

        // bugly
        Bugly.init(getApplicationContext(), "662fc8a1f2", BuildConfig.DEBUG);

        // 下载初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60 * 1000L, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 1000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        // 初始化配置
        SharedPrefHelper.getInstance().setServerUrl(SoapService.URL);
        SharedPrefHelper.getInstance().setDownloadUrl(SoapService.DOWN_URL);
    }

    public static Application getApplication() {
        return application;
    }
}
