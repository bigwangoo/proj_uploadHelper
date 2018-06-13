package com.test.uploadhelper.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.uploadhelper.R;

/**
 * Created by wangyd on 2018/6/13.
 */
public class AboutMeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

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
        tvTitle.setText("使用说明");

        TextView tvAbout = (TextView) findViewById(R.id.tvAbout);
        tvAbout.setText("1、本软件是数据传输用，请勿滥用\n" +
                "2、使用时请授予软件读取存储卡权限\n" +
                "3、使用时请先编辑配置，设置相应参数\n" +
                "4、上传是请使用预览功能，校验数据是否正确\n" +
                "5、上传后数据后文件生成可能需要一定时间，请耐心等待\n" +
                "6、使用过程中有任何问题或建议请及时向管理员反馈");
    }

}
