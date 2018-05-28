package com.test.uploadhelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.test.uploadhelper.R;

/**
 * Created by wangyd on 2018/5/27.
 */
public class PreviewActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("数据预览");
    }

    public void onPreviewGroupClick(View view) {
        startActivity(new Intent(this, GroupPreviewActivity.class));
    }

    public void onPreviewMeterClick(View view) {
        startActivity(new Intent(this, MeterPreviewActivity.class));
    }
}
