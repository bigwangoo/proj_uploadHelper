package com.test.uploadhelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.test.uploadhelper.activity.BaseActivity;
import com.test.uploadhelper.activity.PreviewActivity;
import com.test.uploadhelper.activity.SettingActivity;
import com.test.uploadhelper.common.service.SoapDataUtils;
import com.test.uploadhelper.model.DownloadResult;
import com.test.uploadhelper.model.HttpResult;
import com.test.uploadhelper.utils.DbfUtils;
import com.test.uploadhelper.utils.SharedPrefHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private TextView tvID, tvFilePath;

    private final Handler handler = new Handler();
    private SharedPrefHelper spHelper;

    private String id;
    private String metersPath;
    private String noteBookGroupPath;
    private String metersDownloadPath;
    private String noteBookGroupDownloadPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvRight = findViewById(R.id.tvRight);
        tvTitle.setText("UploadHelper v" + getVersionName(this));
        tvRight.setText("预览");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 预览
                if (TextUtils.isEmpty(metersPath) || TextUtils.isEmpty(noteBookGroupPath)) {
                    Toast.makeText(MainActivity.this, "请先设置DBF文件路径", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(MainActivity.this, PreviewActivity.class));
            }
        });

        tvID = findViewById(R.id.tvID);
        tvFilePath = findViewById(R.id.tvFilePath);

        // sp
        spHelper = SharedPrefHelper.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissionDialog();
        setData();
    }

    private void setData() {
        if (!hasPermission()) {
            return;
        }

        id = spHelper.getID();
        noteBookGroupPath = spHelper.getNoteBookGroupPath();
        metersPath = spHelper.getMetersPath();
        noteBookGroupDownloadPath = spHelper.getNoteBookGroupDownloadPath();
        metersDownloadPath = spHelper.getMetersDownloadPath();

        String path = "原Group位置：" + noteBookGroupPath + "\n"
                + "原Meters位置：" + metersPath + "\n\n"
                + "下载后Group位置：" + noteBookGroupDownloadPath + "\n"
                + "下载后Meters位置：" + metersDownloadPath + "\n";
        tvID.setText(id);
        tvFilePath.setText(path);
    }

    private void checkPermissionDialog() {
        if (!hasPermission()) {
            new AlertDialog.Builder(this)
                    .setTitle("申请权限提示")
                    .setMessage("APP需要<访问存储卡权限>才能正常运行，请确保授予APP访问SD卡权限")
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestNecessaryPermission();
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    }).create().show();
        }
    }

    @Override
    public void onGetNecessaryPermissions() {
        super.onGetNecessaryPermissions();
    }

    /**
     * 配置
     */
    public void onConfigClick(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }

    /**
     * 上传数据
     */
    public void onUploadClick(View view) {
        if (TextUtils.isEmpty(metersPath) || TextUtils.isEmpty(noteBookGroupPath)) {
            Toast.makeText(this, "请设置DBF文件路径", Toast.LENGTH_SHORT).show();
            return;
        }

        setProgressBar("数据处理中...");

        // 处理数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 数据
                final List<String> data = new ArrayList<>();
                // 提示
                String tip = "";

                // 要上传的新数据
                List<Map<String, Object>> newData = new ArrayList<>();
                // 所有数据
                List<Map<String, Object>> meters = DbfUtils.getMeters();
                // 抄表状态等于1
                for (int i = 0; i < meters.size(); i++) {
                    Map<String, Object> map = meters.get(i);
                    try {
                        String cbzt = null;
                        String cbzt2 = null;
                        if (map.containsKey("CBZT")) {
                            cbzt = map.get("CBZT").toString();
                        }
                        if (map.containsKey("cbzt")) {
                            cbzt2 = map.get("cbzt").toString();
                        }
                        if ("1".equals(cbzt) || "1".equals(cbzt2)) {
                            newData.add(map);
                            //Log.e(TAG, "run: " + cbzt);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                int size = newData.size();
                if (size <= 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("上传提示")
                                    .setMessage("没有读取到需要上传的数据，请确认是否有数据修改")
                                    .setPositiveButton("确认", null)
                                    .create().show();
                        }
                    });
                    return;
                }

                // 分批上传
                int pageSize = 800;
                final int count = size / pageSize;
                tip = tip + "共读取到" + meters.size() + "条数据\n " +
                        "需要上传共" + size + "条\n "
                        + "分" + (count + 1) + "部分上传\n ";

                for (int i = 0; i <= count; i++) {
                    List<Map<String, Object>> maps;
                    if (i == count) {
                        maps = newData.subList(i * pageSize, size);
                        // Log.e(TAG, "onUploadClick: " + i * pageSize + "-----" + size);
                    } else {
                        maps = newData.subList(i * pageSize, (i + 1) * pageSize);
                        // Log.e(TAG, "onUploadClick: " + i * pageSize + "-----" + (i + 1) * pageSize);
                    }
                    // 构建上传Json数据
                    final int totalCounts = maps.size();
                    Map params = new HashMap();
                    params.put("outData", maps);
                    params.put("totalCounts", totalCounts);
                    String json = new Gson().toJson(params);
                    // Log.e(TAG, "onUploadClick: " + json);

                    data.add(json);
                    tip = tip + "第" + (i + 1) + "部分" + "共" + totalCounts + "条\n";
                }

                final String finalTip = tip;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                        showUploadDialog(finalTip, data);
                    }
                });

            }
        }).start();
    }

    /* 数据验证提示 */
    private void showUploadDialog(String tip, final List<String> data) {
        // 上传数据
        new AlertDialog.Builder(this)
                .setTitle("确认当前数据信息")
                .setMessage(tip)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("上传", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setProgressBar("数据上传中...");
                        // 延时上传
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                uploadReadData(data);
                            }
                        }, 1000);
                    }
                }).create().show();
    }

    /* 上传数据*/
    private void uploadReadData(List<String> data) {
        if (data == null) {
            hideProgressBar();
            return;
        }

        String res = "";
        for (int i = 0; i < data.size(); i++) {
            String json = data.get(i);
            // 上传
            String result = SoapDataUtils.upLoadReadMsg(MainActivity.this, json);
            final HttpResult httpResult = new Gson().fromJson(result, HttpResult.class);

            if (httpResult != null) {
                res = res + "第" + (i + 1) + "部分上传结果：" + httpResult.getOutMsg() + "\n";
            } else {
                res = res + "网络请求错误";
            }
        }

        final String finalRes = res;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("上传结果")
                        .setMessage(finalRes)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        }, 1000);
    }

    /**
     * 下载数据
     */
    public void onDownloadClick(View view) {
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "请设置抄表人ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(metersPath) || TextUtils.isEmpty(noteBookGroupPath)) {
            Toast.makeText(this, "请设置DBF文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(metersDownloadPath) || TextUtils.isEmpty(noteBookGroupDownloadPath)) {
            Toast.makeText(this, "请设置DBF文件下载路径", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(MainActivity.this)
                .setMessage("确认下载抄表人ID为：" + id + " 的数据文件吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        showProgressBar();

                        // 获取下载信息
                        String preReadMsg = SoapDataUtils.getPreReadMsg(MainActivity.this, id);
                        // 解析结果
                        final DownloadResult result = new Gson().fromJson(preReadMsg, DownloadResult.class);
                        // 提示
                        if (result != null) {
                            // 提示
                            if (result.getOutResult() == 1) {
                                setProgressBar("获取成功正在准备下载...");

                                final String downloadUrl = spHelper.getDownloadUrl();
                                final String url1 = downloadUrl + result.getNoteBookGroupDbfName();
                                final String url2 = downloadUrl + result.getMetersDbfName();
                                // 延时下载
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadGroupFile(url1, url2);
                                    }
                                }, 1000);
                            } else {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("获取文件失败")
                                        .setMessage(result.getOutMsg())
                                        .setPositiveButton("确认", null)
                                        .create().show();
                            }
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("获取文件失败")
                                    .setMessage("网络请求错误")
                                    .setPositiveButton("确认", null)
                                    .create().show();
                        }
                    }
                })
                .create()
                .show();
    }

    /*下载文件1*/
    private void downloadGroupFile(String url1, final String url2) {
        final String path = spHelper.getNoteBookGroupDownloadPath();
        final String name = SoapDataUtils.NOTEBOOKGROUP;
        try {
            File file = new File(path, name);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final long timeMillis = System.currentTimeMillis();
        // 下载
        OkHttpUtils.get().url(url1).build()
                .execute(new FileCallBack(path, name) {
                    @Override
                    public void onResponse(final File response, int id) {
                        Log.e(TAG, "onResponse :" + response.getAbsolutePath());

                        long currentTimeMillis = System.currentTimeMillis();
                        int delay = currentTimeMillis - timeMillis < 2000 ? 2000 : 0;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                downloadMeterFile(response.getAbsolutePath(), url2);
                            }
                        }, delay);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        setProgressBar(name + "下载中 " + (int) (100 * progress) + "%");
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError :" + e.getMessage());
                        // Toast.makeText(MainActivity.this, name + "下载失败", Toast.LENGTH_SHORT).show();

                        hideProgressBar();
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("下载失败")
                                .setMessage(e.getMessage())
                                .setPositiveButton("确认", null)
                                .create().show();
                    }
                });
    }

    /*下载文件2*/
    private void downloadMeterFile(final String file1, String url) {
        final String path = spHelper.getMetersDownloadPath();
        final String name = SoapDataUtils.METERS;

        try {
            File file = new File(path, name);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final long timeMillis = System.currentTimeMillis();
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(path, name) {
                    @Override
                    public void onResponse(final File response, int id) {
                        Log.e(TAG, "onResponse :" + response.getAbsolutePath());
                        //Toast.makeText(MainActivity.this, name + "下载成功", Toast.LENGTH_SHORT).show();

                        long currentTimeMillis = System.currentTimeMillis();
                        int delay = currentTimeMillis - timeMillis < 2000 ? 2000 : 0;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressBar();
                            }
                        }, delay);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("下载结果")
                                        .setMessage("下载成功\n" + file1 + "\n" + response.getAbsolutePath())
                                        .setPositiveButton("确定", null)
                                        .create().show();
                            }
                        }, 1000);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        setProgressBar(name + "下载中" + (int) (100 * progress) + "%");
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError :" + e.getMessage());
                        // Toast.makeText(MainActivity.this, name + "下载失败", Toast.LENGTH_SHORT).show();

                        hideProgressBar();
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("下载失败")
                                .setMessage(e.getMessage())
                                .setPositiveButton("确认", null)
                                .create().show();
                    }
                });
    }

    /**
     * 获取应用程序 版本名称
     */
    private String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //static class  UploadDataTask extends AsyncTask
}
