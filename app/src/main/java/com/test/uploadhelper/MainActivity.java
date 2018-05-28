package com.test.uploadhelper;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.test.uploadhelper.common.service.SoapService;
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
        tvTitle.setText("UploadHelper");
        tvRight.setText("预览");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(metersPath) || TextUtils.isEmpty(noteBookGroupPath)) {
                    Toast.makeText(MainActivity.this, "请设置DBF文件路径", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onGetNecessaryPermissions() {
        super.onGetNecessaryPermissions();
    }

    private void checkPermissionDialog() {
        if (!hasPermission()) {
            new AlertDialog.Builder(this)
                    .setTitle("申请权限提示")
                    .setMessage("APP需要访问存储卡权限才能正常运行，请确保授予APP访问SD卡权限")
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

        new AlertDialog.Builder(this)
                .setMessage("确认上传数据吗？")
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

                        setProgressBar("数据上传中...");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 要上传的新数据
                                List<Map<String, Object>> newData = new ArrayList<>();
                                // 所有数据
                                List<Map<String, Object>> meters = DbfUtils.getMeters();
                                // 抄表状态等于1
                                for (int i = 0; i < meters.size(); i++) {
                                    Map<String, Object> map = meters.get(i);
                                    try {
                                        int cbzt = (int) map.get("CBZT");
                                        if (cbzt == 1) {
                                            newData.add(map);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                                // 分批上传
                                int size = newData.size();
                                int pageSize = 800;
                                final int count = size / pageSize;
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
                                    Gson gson = new Gson();
                                    String json = gson.toJson(params);
                                    // Log.e(TAG, "onUploadClick: " + json);

                                    // 上传
                                    String result = SoapDataUtils.upLoadReadMsg(MainActivity.this, json);
                                    final HttpResult httpResult = gson.fromJson(result, HttpResult.class);

                                    final int finalNum = i;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "共分" + (count + 1)
                                                            + "部分 第" + (finalNum + 1) + "部分共" + totalCounts + "条上传" + httpResult.getOutMsg(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressBar();
                                    }
                                });
                            }
                        }).start();
                    }
                }).create().show();
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

        new AlertDialog.Builder(this)
                .setMessage("确认下载抄表人ID为：" + id + " 的数据吗？")
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

                        // 获取下载信息
                        showProgressBar();

                        String preReadMsg = SoapDataUtils.getPreReadMsg(MainActivity.this, id);

                        // 解析结果
                        final DownloadResult result = new Gson().fromJson(preReadMsg, DownloadResult.class);
                        if (result.getOutResult() == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "获取成功正在准备下载...", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // 下载
                            downloadGroupFile(SoapService.DOWN_URL + result.getNoteBookGroupDbfName(),
                                    SoapService.DOWN_URL + result.getMetersDbfName());

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, result.getOutMsg(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .create()
                .show();
    }

    private void downloadGroupFile(String url, final String url2) {
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

        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(path, name) {
                    @Override
                    public void onResponse(File response, int id) {
                        Log.e(TAG, "onResponse :" + response.getAbsolutePath());
                        Toast.makeText(MainActivity.this, name + "下载成功", Toast.LENGTH_SHORT).show();

                        long currentTimeMillis = System.currentTimeMillis();
                        int delay = currentTimeMillis - timeMillis < 2000 ? 2000 : 0;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                downloadMeterFile(url2);
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
                        hideProgressBar();
                        Toast.makeText(MainActivity.this, name + "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downloadMeterFile(String url) {
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
                    public void onResponse(File response, int id) {
                        Log.e(TAG, "onResponse :" + response.getAbsolutePath());
                        Toast.makeText(MainActivity.this, name + "下载成功", Toast.LENGTH_SHORT).show();


                        long currentTimeMillis = System.currentTimeMillis();
                        int delay = currentTimeMillis - timeMillis < 2000 ? 2000 : 0;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressBar();
                            }
                        }, delay);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        setProgressBar(name + "下载中" + (int) (100 * progress) + "%");
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError :" + e.getMessage());
                        hideProgressBar();
                        Toast.makeText(MainActivity.this, name + "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //static class  UploadDataTask extends AsyncTask
}
