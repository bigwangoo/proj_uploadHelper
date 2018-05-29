package com.test.uploadhelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.test.uploadhelper.R;
import com.test.uploadhelper.adapter.FileDirListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangyd on 2018/5/28.
 */
public class FileDirPickActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    public static final String FILE_RESULT = "file";

    private RecyclerView rv;
    private FileDirListAdapter mAdapter;
    private List<String> mPaths = new ArrayList<>();

    private String rootPath = "/";
    private String curPath = "/";
    private TextView tvPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_dir);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvRight = findViewById(R.id.tvRight);
        tvTitle.setText("文件夹选择");
        tvRight.setText("选择");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(FILE_RESULT, curPath);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        tvPath = findViewById(R.id.tvPath);
        tvPath.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    getFileDirs(new File(curPath).getParent());
                } catch (Exception e) {
                }
            }
        });

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new FileDirListAdapter(mPaths);
        mAdapter.setOnItemClickListener(this);
        rv.setAdapter(mAdapter);

        requestNecessaryPermission();
    }

    @Override
    public void onGetNecessaryPermissions() {
        super.onGetNecessaryPermissions();
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        curPath = rootPath;

        getFileDirs(rootPath);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        try {
            String path = mPaths.get(position);
            getFileDirs(path);
        } catch (Exception e) {

        }
    }

    private void getFileDirs(String filePath) {
        curPath = filePath;
        tvPath.setText("当前路径：" + curPath);
        //设置向上是否可用
        if (rootPath != null && rootPath.equals(filePath)) {
            tvPath.setEnabled(false);
        } else {
            tvPath.setEnabled(true);
        }

        try {
            File f = new File(filePath);
            File[] files = f.listFiles();
            //判断当前下是否有文件夹
            if (files.length <= 0) {
                return;
            }

            mPaths.clear();
            //过滤一遍 1.是否为文件夹 //2.是否可访问
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory() && files[i].listFiles() != null) {
                    mPaths.add(files[i].getPath());
                }
            }
            // 排序
            Collections.sort(mPaths);
            mAdapter.setNewData(mPaths);
        } catch (Exception e) {

        }
    }
}