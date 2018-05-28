package com.test.uploadhelper.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.test.uploadhelper.R;
import com.test.uploadhelper.adapter.GroupListAdapter;
import com.test.uploadhelper.utils.DbfUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wangyd
 * @date 2018/5/23
 * @description description
 */
public class GroupPreviewActivity extends BaseActivity {

    private RecyclerView rv;
    private GroupListAdapter mAdapter;
    private List<Map<String, Object>> mData = new ArrayList<>();

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("数据预览");

        rv = findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new GroupListAdapter(mData);
        rv.setAdapter(mAdapter);

        showProgressBar();

        requestNecessaryPermission();
    }

    @Override
    public void onGetNecessaryPermissions() {
        super.onGetNecessaryPermissions();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Map<String, Object>> list = DbfUtils.getNoteBookGroup();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mData.clear();
                        mData.addAll(list);
                        mAdapter.notifyDataSetChanged();

                        hideProgressBar();
                    }
                });
            }
        }).start();
    }
}
