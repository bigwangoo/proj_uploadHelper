package com.test.uploadhelper.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.test.uploadhelper.R;
import com.test.uploadhelper.model.AppConfig;
import com.test.uploadhelper.utils.SharedPrefHelper;
import com.test.uploadhelper.utils.UriUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wangyd on 2018/5/27.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SettingActivity.class.getSimpleName();

    public static final Integer RC_FILE_SELECT_CONFIG = 1001;

    public static final Integer RC_FILE_SELECT_METERS = 100;
    public static final Integer RC_FILE_SELECT_NOTE = 101;
    public static final Integer RC_FILE_SELECT_METERS_D = 102;
    public static final Integer RC_FILE_SELECT_NOTE_D = 103;

    private SharedPrefHelper spHelper;
    private TextView tvID, tvGroup, tvMeters, tvGroupDownload, tvMetersDownload, tvServer, tvDownload;
    private RelativeLayout rlHelper;
    private ImageView ivHelperClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        spHelper = SharedPrefHelper.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvRight = findViewById(R.id.tvRight);
        tvTitle.setText("设置Config");
        tvRight.setText("重置");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this).
                        setMessage("确定要重置配置吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetSetting();
                                setData();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create().show();
            }
        });

        tvID = findViewById(R.id.tvID);
        tvGroup = findViewById(R.id.tvGroup);
        tvMeters = findViewById(R.id.tvMeters);
        tvGroupDownload = findViewById(R.id.tvGroupDownload);
        tvMetersDownload = findViewById(R.id.tvMetersDownload);
        tvMetersDownload = findViewById(R.id.tvMetersDownload);
        tvServer = findViewById(R.id.tvServer);
        tvDownload = findViewById(R.id.tvDownload);
        rlHelper = findViewById(R.id.rl_helper);
        ivHelperClose = findViewById(R.id.iv_helper_close);

        findViewById(R.id.llID).setOnClickListener(this);
        findViewById(R.id.llGroup).setOnClickListener(this);
        findViewById(R.id.llMeters).setOnClickListener(this);
        findViewById(R.id.llGroupDownload).setOnClickListener(this);
        findViewById(R.id.llMetersDownload).setOnClickListener(this);
        findViewById(R.id.tvServer).setOnClickListener(this);
        findViewById(R.id.tvDownload).setOnClickListener(this);
        findViewById(R.id.rl_helper).setOnClickListener(this);
        findViewById(R.id.iv_helper_close).setOnClickListener(this);

        requestNecessaryPermission();
        setData();
    }

    private void resetSetting() {
        spHelper.setID("");
        spHelper.setNoteBookGroupPath("");
        spHelper.setMetersPath("");
        spHelper.setNoteBookGroupDownloadPath("");
        spHelper.setMetersDownloadPath("");
        spHelper.setServerUrl("");
        spHelper.setDownloadUrl("");
    }

    private void setData() {
        tvID.setText(spHelper.getID());
        tvGroup.setText(spHelper.getNoteBookGroupPath());
        tvMeters.setText(spHelper.getMetersPath());
        tvGroupDownload.setText(spHelper.getNoteBookGroupDownloadPath());
        tvMetersDownload.setText(spHelper.getMetersDownloadPath());
        tvServer.setText(spHelper.getServerUrl());
        tvDownload.setText(spHelper.getDownloadUrl());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_helper:
                showHelperDialog();
                break;
            case R.id.iv_helper_close:
                rlHelper.setVisibility(View.GONE);
                break;
            case R.id.llID:
                onIdClick();
                break;
            case R.id.llGroup:
                onFilePickClick(RC_FILE_SELECT_NOTE);
                break;
            case R.id.llMeters:
                onFilePickClick(RC_FILE_SELECT_METERS);
                break;
            case R.id.llGroupDownload:
                onFileDownloadPickClick(RC_FILE_SELECT_NOTE_D);
                break;
            case R.id.llMetersDownload:
                onFileDownloadPickClick(RC_FILE_SELECT_METERS_D);
                break;
            case R.id.tvServer:
                onServerUrlClick();
                break;
            case R.id.tvDownload:
                onDownloadUrlClick();
                break;
            default:
                break;
        }
    }

    private void showHelperDialog() {
        String[] items = {"配置文件导入", "粘贴板导入"};
        new AlertDialog.Builder(this)
                .setTitle("请选择导入方式")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (which == 0) {
                            // 选择配置
                            onConfigPickClick(RC_FILE_SELECT_CONFIG);

                        } else if (which == 1) {
                            //对剪贴板文字的操作
                            onConfigCopyClick();
                        }

                    }
                }).create().show();
    }

    private void onConfigPickClick(int requestCode) {
        if (!hasPermission()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择配置文件"), requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    private String getConfigFromFile(String path) {
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "配置文件路径为空", Toast.LENGTH_SHORT).show();
        }
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = new FileInputStream(new File(path));
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content.toString();
    }

    private void onConfigCopyClick() {
        String content = null;
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data;
        if (cm != null) {
            data = cm.getPrimaryClip();
            ClipData.Item item = data.getItemAt(0);
            content = item.getText().toString();
        }
        setConfigInput(content);
    }

    private void setConfigInput(String json) {
        if (TextUtils.isEmpty(json)) {
            Toast.makeText(this, "没有获取到配置信息", Toast.LENGTH_SHORT).show();
            return;
        }

        AppConfig config = null;

        Gson gson = new Gson();
        try {
            config = gson.fromJson(json.trim(), AppConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (config == null) {
            Toast.makeText(this, "配置信息导入失败，请检查信息是否有误", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = config.getUserId();
        String pathNotebookGroup = config.getPathNotebookGroup();
        String pathMeters = config.getPathMeters();
        String pathDownloadNotebookGroup = config.getPathDownloadNotebookGroup();
        String pathDownloadMeters = config.getPathDownloadMeters();
        String serverUrl = config.getServerUrl();
        String downloadUrl = config.getDownloadUrl();

        if (TextUtils.isEmpty(pathNotebookGroup) ||
                TextUtils.isEmpty(pathMeters) ||
                TextUtils.isEmpty(pathDownloadNotebookGroup) ||
                TextUtils.isEmpty(pathDownloadMeters) ||
                TextUtils.isEmpty(serverUrl) ||
                TextUtils.isEmpty(downloadUrl)) {

            Toast.makeText(this, "配置参数不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        spHelper.setID(emptyIfNull(userId));
        spHelper.setNoteBookGroupPath(emptyIfNull(pathNotebookGroup));
        spHelper.setMetersPath(emptyIfNull(pathMeters));
        spHelper.setNoteBookGroupDownloadPath(emptyIfNull(pathDownloadNotebookGroup));
        spHelper.setMetersDownloadPath(emptyIfNull(pathDownloadMeters));
        spHelper.setServerUrl(emptyIfNull(serverUrl));
        spHelper.setDownloadUrl(emptyIfNull(downloadUrl));

        tvID.setText(emptyIfNull(userId));
        tvGroup.setText(emptyIfNull(pathNotebookGroup));
        tvMeters.setText(emptyIfNull(pathMeters));
        tvGroupDownload.setText(emptyIfNull(pathDownloadNotebookGroup));
        tvMetersDownload.setText(emptyIfNull(pathDownloadMeters));
        tvServer.setText(emptyIfNull(serverUrl));
        tvDownload.setText(emptyIfNull(downloadUrl));

        Toast.makeText(this, "配置导入成功", Toast.LENGTH_SHORT).show();
    }

    private String emptyIfNull(String str) {
        return str == null ? "" : str;
    }


    private void onIdClick() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_input, null);
        final EditText edtInput = (EditText) view.findViewById(R.id.edtInput);

        new AlertDialog.Builder(this)
                .setTitle("设置抄表人ID")
                .setView(view)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String trim = edtInput.getText().toString().trim();
                        spHelper.setID(trim);
                        tvID.setText(trim);
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    private void onServerUrlClick() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_input, null);
        final EditText edtInput = (EditText) view.findViewById(R.id.edtInput);
        edtInput.setText(spHelper.getServerUrl());

        new AlertDialog.Builder(this)
                .setTitle("设置接口服务地址")
                .setView(view)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String trim = edtInput.getText().toString().trim();
                        spHelper.setServerUrl(trim);
                        tvServer.setText(trim);
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    private void onDownloadUrlClick() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_input, null);
        final EditText edtInput = (EditText) view.findViewById(R.id.edtInput);
        edtInput.setText(spHelper.getDownloadUrl());

        new AlertDialog.Builder(this)
                .setTitle("设置下载服务地址")
                .setView(view)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String trim = edtInput.getText().toString().trim();
                        spHelper.setDownloadUrl(trim);
                        tvDownload.setText(trim);
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    private void onFilePickClick(int requestCode) {
        if (!hasPermission()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    private void onFileDownloadPickClick(int requestCode) {
        if (!hasPermission()) {
            return;
        }
        startActivityForResult(new Intent(this, FileDirPickActivity.class), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_FILE_SELECT_CONFIG && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            Log.e(TAG, "onActivityResult: " + uri.toString());
            String path = UriUtils.getPathFromUri(this, uri);
            setConfigInput(getConfigFromFile(path));

        } else if (requestCode == RC_FILE_SELECT_NOTE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            Log.e(TAG, "onActivityResult: " + uri.toString());
            String path = UriUtils.getPathFromUri(this, uri);
            spHelper.setNoteBookGroupPath(path);
            tvGroup.setText(path);

        } else if (requestCode == RC_FILE_SELECT_METERS && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            Log.e(TAG, "onActivityResult: " + uri.toString());
            String path = UriUtils.getPathFromUri(this, uri);
            spHelper.setMetersPath(path);
            tvMeters.setText(path);

        } else if (requestCode == RC_FILE_SELECT_NOTE_D && resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra(FileDirPickActivity.FILE_RESULT);
            spHelper.setNoteBookGroupDownloadPath(path);
            tvGroupDownload.setText(path);

        } else if (requestCode == RC_FILE_SELECT_METERS_D && resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra(FileDirPickActivity.FILE_RESULT);
            spHelper.setMetersDownloadPath(path);
            tvMetersDownload.setText(path);
        }
    }
}
